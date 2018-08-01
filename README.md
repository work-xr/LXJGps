### LXJGps
* 使用MVP架构
* 使用AlarmService开启定时服务
* 使用百度SDK定位  
* 使用RxJava访问网络

### 客户需求
1. 上报SOS救援、定位信息  
主要流程：客户端触发SOS救援按钮后，首先向孝老中心服务号码和两个亲情号码发送救援短信，然后进行定位；客户端先将定位信息向孝老平台接口传送定位数据
2. 设备端（客户端）修改亲情号码后同步至孝老平台  
主要流程：客户端修改亲情号码后，同步到孝老平台
3. 孝老平台下发修改设备亲情号码等信息  
主要流程：服务端下发设置设备固定亲情号码指令，设备应返回设置是否成功标识
4. 设备一开机就上报定位数据至孝老平台，之后设备间隔一段时间上报一次定位数据至孝老平台  
主要流程：  
> 客户端开机时,进行定位 ，并将定位相关数据传给孝老平台  
> 客户端根据默认间隔时间，每间隔如：30分钟上传一次定位至孝老平台

5. 孝老平台随时下发查询指令后，能随时定位到设备的位置  
主要流程：孝老平台向客户端发送实时定位指令，客户端进行定位，定位成功后，客户端将定位数据上传到接口4，将定位数据上传给孝老平台
6. 孝老平台随时下发修改设备定位上传频率
7. 通知设备用户超出电子围栏  
主要流程：根据孝老平台设置的电子围栏，判断定位上传信息，如果超出电子围栏，并通知设备，设备接收到此消息后向亲情号码发送超出电子围栏手机信息
8. 获取设备状态  
主要流程：孝老平台下发获取设备相关状态信息

### 主要步骤
1. 收到开机广播,开启定时服务
2. 每隔30分钟启动一次服务
3. 后台服务会开始用百度进行定位
4. 如果定位失败如超时,则停止定位服务
5. 如果定位成功,将定位信息上传到服务器,并停止定位服务
6. 将本地孝老平台设置号码上传到服务器
7. 将服务器上设置的号码同步到本地

对传递到服务器的参数data,先转成GSON格式,再排序,然后按照UTF-8编码  
```
ReportParamBean reportParamBean = new ReportParamBean(imei,
                RXJAVAHTTP_COMPANY,
                RXJAVAHTTP_TYPE_REPORT,
                positionType,
                time,
                locType,
                longitude,
                latitude,
                capacity
        );

String gsonString = ReportParamBean.getReportParamGson(reportParamBean);
String sortedGsonString = getSortedParam(gsonString);

try
{
    data = URLEncoder.encode(sortedGsonString, RXJAVAHTTP_ENCODE_TYPE);
}
catch (UnsupportedEncodingException e)
{
    e.printStackTrace();
}
```
对传递到服务器的参数sign,需要进行MD5编码  
```
sign = MD5Utils.encrypt(data + RXJAVAHTTP_SECRET_CODE);
```
### 问题汇总
#### 问题1 自启动失败-接收不到BOOT_COMPLETED广播可能的原因  
1. BOOT_COMPLETED对应的action和uses-permission没有一起添加
2. 应用安装到了sd卡内，安装在sd卡内的应用是收不到BOOT_COMPLETED广播的
3. 系统开启了Fast Boot模式，这种模式下系统启动并不会发送BOOT_COMPLETED广播
4. 应用程序安装后重来没有启动过，这种情况下应用程序接收不到任何广播，包括BOOT_COMPLETED、ACTION_PACKAGE_ADDED、CONNECTIVITY_ACTION等等,
Android3.1之后，系统为了加强了安全性控制，应用程序安装后或是(设置)应用管理中被强制关闭后处于stopped状态，在这种状态下接收不到任何广播，除非广播带有FLAG_INCLUDE_STOPPED_PACKAGES标志，而默认所有系统广播都是FLAG_EXCLUDE_STOPPED_PACKAGES的，所以就没法通过系统广播自启动了。所以Android3.1之后  
1. 应用程序无法在安装后自己启动
2. 没有ui的程序必须通过其他应用激活才能启动，如它的Activity、Service、Content Provider被其他应用调用。
存在一种例外，就是应用程序被adb push you.apk /system/app/下是会自动启动的，不处于stopped状态  

#### 问题2 通过GPS基站或者Wifi MAC地址无法定位成功
不管是GPS基站或者Wifi MAC地址,都需要连接谷歌服务器获取定位信息,由于众所周知的原因,目前无法实现

#### 问题3 百度SDK定位功能, 申请KEY需提供APK签名文件JKS的HASH值
1. 常用的android的签名工具有:jarsigner 和apksigner。jarsigner使用keystore文件，apksigner使用pk8+x509.pem,
AndroidStudio可以简便的使用jarsigner方式对应用进行签名, 而编译整个工程可能是apksigner的方式
2. 可以使用jarsigner方式对应用进行签名, 然后将整个apk以拷贝文件方式拷贝到system/app下(此时将不会再进行系统签名) , 如果要编译代码, 可配置Android.mk使用应用自带签名, 而不再进行系统签名    
```
LOCAL_CERTIFICATE := PRESIGNED
```
3. 开发一个带有系统权限的app, 往往需要配置SharedUserId, 如果直接在AS中run，app是装不上的，需要先生成app，然后再使用系统文件对apk进行签名:  
```
java -jar signapk.jar  platform.x509.pem platform.pk8 signDemo.apk signDemo_signed.apk
```
虽然能够满足使用, 但太麻烦    
a. 利用AS生成jks文件   
b. 利用keytool-importkeypair对jks文件引入系统签名   
把platform.x509.pem、platform.pk8和上一部生成的jks文件统一放到一个文件夹下, 执行:  
```
./keytool-importkeypair -k SignDemo.jks -p 123456 -pk8 platform.pk8 -cert platform.x509.pem -alias SignDemo
```
配置gradle:  
```
signingConfigs {
      release {
          storeFile file("../signApk/SignDemo.jks")
          storePassword '123456'
          keyAlias 'SignDemo'
          keyPassword '123456'
      }
      debug {
          storeFile file("../signApk/SignDemo.jks")
          storePassword '123456'
          keyAlias 'SignDemo'
          keyPassword '123456'
      }
  }
```

#### 问题4 获取其他应用SharedPreferences失败
```
private int mode = Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE;
private SharedPreferences sosSharedPreferences = null;
{
    // SOS模块的context
    Context sosContext = null;
    try {
        sosContext = XLJGpsApplication.getAppContext().createPackageContext(SOS_PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
        Log.d(TAG, "instance initializer: get sos context");
    } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
    }
    sosSharedPreferences = sosContext.getSharedPreferences(SOS_NUM_PREFS_NAME, mode);
}
```
* 如果SOS模块的mode是默认或者`MODE_PRIVATE`,其他应用是无法读写的
* 如果SOS模块的mode更改为读和写, 其他应用依然无法写数据
* 第一次从SOS读取成功,但是当在SOS模块更改之后,再次读取则一直无法更新数据, mode需要改为`MODE_MULTI_PROCESS`  
```
sosContext.getSharedPreferences(SOS_NUM_PREFS_NAME, MODE_MULTI_PROCESS);
```


### HTTP 协议基础
#### GET请求的特点:
* GET请求能够被缓存
* GET请求会保存在浏览器的浏览记录中
* 以GET请求的URL能够保存为浏览器书签
* GET请求有长度限制
* GET请求主要用以获取数据  

#### POST请求的特点:  
* POST请求不能被缓存下来
* POST请求不会保存在浏览器浏览记录中
* 以POST请求的URL无法保存为浏览器书签
* POST请求没有长度限制

#### 7种常见请求方式
1. Get是最常用的方法，通常用于请求服务器发送某个资源，而且应该是安全的和幂等的, URL的编码格式采用的是ASCII编码，而不是uniclde，即是说所有的非ASCII字符都要编码之后再传输
2. POST方法向服务器提交数据，比如完成表单数据的提交，会把请求的数据放置在HTTP请求包的包体中提交给服务器处理, GET请求的数据会暴露在地址栏中，而POST请求则不会
3. PUT方法是让服务器用请求的主体部分来创建一个由所请求的URL命名的新文档；如果那个文档存在的话，就用这个主体来代替它
4. DELETE方法就是请求服务器删除指定URL所对应的资源。但是，客户端无法保证删除操作一定会被执行，因为HTTP规范允许服务器在不通知客户端的情况下撤销请求
5. HEAD方法与GET方法的行为很类似，但服务器在响应中只返回实体的主体部分。这就允许客户端在未获取实际资源的情况下，对资源的首部进行检查
6. TRACE方法会在目的服务器端发起一个“回环”诊断，我们都知道，客户端在发起一个请求时，这个请求可能要穿过防火墙、代理、网关、或者其它的一些应用程序。这中间的每个节点都可能会修改原始的HTTP请求，TRACE方法允许客户端在最终将请求发送服务器时，它变成了什么样子。由于有一个“回环”诊断，在请求最终到达服务器时，服务器会弹回一条TRACE响应，并在响应主体中携带它收到的原始请求报文的最终模样。这样客户端就可以查看HTTP请求报文在发送的途中，是否被修改过了
7. OPTIONS方法用于获取当前URL所支持的方法。若请求成功，则它会在HTTP头中包含一个名为“Allow”的头，值是所支持的方法，如“GET, POST”

form的enctype属性为编码方式，常用有两种：application/x-www-form-urlencoded和multipart/form-data，默认为application /x-www-form-urlencoded

当action为get时候，浏览器用x-www-form-urlencoded的编码方式把form数据转换成一个字串（name1=value1& amp; amp;name2=value2...），然后把这个字串append到url后面，用?分割，加载这个新的url。当action为post时候，浏览器把form数据封装到http body中，然后发送到server

如果没有type=file，用默认的application/x-www-form-urlencoded就可以了。但是如果有 type=file的话，就要用到multipart/form-data了
#### GET和POST传输长度的误区
由于使用GET方法提交数据时，数据会以&符号作为分隔符的形式，在URL后面添加需要提交的参数，浏览器地址栏输入的参数是有限的，而POST不用再地址栏输入，所以POST就比GET可以提交更多的数据? HTTP协议明确地指出，HTTP头和Body都没有长度的要求。而对于URL长度上的限制, 主要是浏览器和服务器, 出于安全, 稳定, 性能的考虑做出的限制; 在HTTP规范中，没有对URL的长度和传输的数据大小进行限制。但是在实际开发过程中，对于GET特定的浏览器和服务器对URL的长度有限制。在使用GET请求时，传输数据会受到URL长度的限制。对于POST，由于不是URL传值，理论上是不会受限制的，但是实际上各个服务器会规定对POST提交数据大小进行限制，Apache、IIS都有各自的配置. HTTP没有要求，如果Method是POST数据就要放在BODY中, 也没有要求，如果Method是GET，数据（参数）就一定要放在URL中而不能放在BODY中  

#### postman中 form-data、x-www-form-urlencoded、raw、binary的区别  
`x-www-form-urlencoded`
```
就是application/x-www-from-urlencoded,会将表单内的数据转换为键值对，比如,name=java&age = 23
```
`raw`
```
可以上传任意格式的文本，可以上传text、json、xml、html等
```
`binary`
```
相当于Content-Type:application/octet-stream,从字面意思得知，只可以上传二进制数据，通常用来上传文件，由于没有键值，所以，一次只能上传一个文件
```


### 信息摘要技术和算法
信息摘要算法来源于CRC算法，最初是用来验证数据完整性，即我们常见的奇偶校验码、循环冗余校验，CRC比这些算法都要早，MD算法比SHA算法早，SHA算法是对MD算法的改进。再后来则发展出了可以带有密码的信息摘要算法-MAC算法. 消息摘要算法的主要特征是加密过程不需要密钥，并且经过加密的数据无法被解密，只有输入相同的明文数据经过相同的消息摘要算法才能得到相同的密文  
#### 特点
1. 变长输入，定长输出, 无论输入的消息有多长，计算出来的消息摘要的长度总是固定的
2. 消息摘要看起来是“随机的”，一般随机都是伪随机
3. 一般只要输入的消息不同，对其进行摘要以后产生的摘要消息也必不相同；但相同的输入必会产生相同的输出. 理论上，不管使用什么样的摘要算法，必然存在2个不同的消息，对应同样的摘要。因为输入是一个无穷集合，而输出是一个有限集合，所以从数学上，必然存在多对一的关系。但是实际上，很难或者说根本不可能人为的造出具有同样摘要的2个不同消息
4. 消息摘要是单向、不可逆的。消息摘要函数是无陷门的单向函数，即只能进行正向的信息摘要，而无法从摘要中恢复出任何的消息，甚至根本就找不到任何与原信息相关的信息
5. 好的摘要算法，没有人能从中找到“碰撞”，但是“碰撞”是肯定存在的

#### 选择
* CRC算法不属于加密场景，比较古老，但是在数据压缩领域被广泛使用作为完整性校验
* MD算法中MD5算法最流行，也是目前最流行的信息摘要算法，是大部分系统的首选，虽然MD算法破解门槛越来越低，但是一般应用足够了
* SHA算法枝繁叶茂，比MD算法安全性高，尝尝用在一些安全性系数要求较高的环境，目前也逐渐替代MD5算法，用在注册、登录模块，在数字证书的签名算法中，SHA算法更广泛
* MAC算法是带有密钥信息的信息摘要算法，吸收了MD和SHA的精髓，安全程度更高
