### LXJGps
use MVP  
use IntentService & AlarmService start service  
use Baidu SDK to locate  
use RxJava to visit network  

### 自启动失败-接收不到BOOT_COMPLETED广播可能的原因  
1. BOOT_COMPLETED对应的action和uses-permission没有一起添加
2. 应用安装到了sd卡内，安装在sd卡内的应用是收不到BOOT_COMPLETED广播的
3. 系统开启了Fast Boot模式，这种模式下系统启动并不会发送BOOT_COMPLETED广播
4. 应用程序安装后重来没有启动过，这种情况下应用程序接收不到任何广播，包括BOOT_COMPLETED、ACTION_PACKAGE_ADDED、CONNECTIVITY_ACTION等等,
Android3.1之后，系统为了加强了安全性控制，应用程序安装后或是(设置)应用管理中被强制关闭后处于stopped状态，在这种状态下接收不到任何广播，除非广播带有FLAG_INCLUDE_STOPPED_PACKAGES标志，而默认所有系统广播都是FLAG_EXCLUDE_STOPPED_PACKAGES的，所以就没法通过系统广播自启动了。所以Android3.1之后  
1. 应用程序无法在安装后自己启动
2. 没有ui的程序必须通过其他应用激活才能启动，如它的Activity、Service、Content Provider被其他应用调用。
存在一种例外，就是应用程序被adb push you.apk /system/app/下是会自动启动的，不处于stopped状态  

### 百度SDK定位功能, 申请KEY需提供APK签名文件JKS的HASH值
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
