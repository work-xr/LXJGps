### 项目架构
* 使用MVP架构
* 使用AlarmService开启定位服务-GpsService
* 使用AlarmService开启心跳服务-BeatHeartService
* 使用AlarmService开启重连服务-ReconnectSocketService
* 使用StickyService打开socket连接服务-SocketService
* 使用百度SDK定位(本身支持基站定位, WIFI定位和GPS定位,为了最大限度降低功耗,仅使用基站定位模式)  
~~* 使用RxJava访问网络~~
* 使用TCP-Socket与服务器通信

### 客户需求
1. 设备上传心跳报文（设备请求服务器）  
功能描述：开机后每隔10分钟上传心跳报文，如果超过10分钟服务器没有收到心跳报文则认为终端掉线了，主动断开连接。终端在再次搜到网络后，与服务器建立连接，并继续10分钟为间隔上传心跳报文。心跳报文作用在于检测终端是否在线  
2. 设备同步亲情号设置至服务器（设备请求服务器）  
3. 设备上报一键SOS报警数据（设备请求服务器）
4. 设备一开机就上报定位数据至孝老平台，之后设备间隔一段时间上报一次定位数据至孝老平台（设备请求服务器）
5. 服务器下发固定亲情号码设置(服务器请求设备)  
功能描述：服务端下发设置设备固定亲情号码指令，设备应返回设置是否成功标识
6. 孝老平台随时下发查询指令后，能随时定位到设备的位置(服务器请求设备)
7. 孝老平台下发更改所有设备定位上传频率指令(服务器请求设备)
8. 孝老平台监护人可以根据老人的活动范围，设定电子围栏。如果老人超出电子围栏，设备将用短信通知监护人（亲情号码）(服务器请求设备)
9. 孝老平台下发获取设备相关状态信息(服务器请求设备)

~~1. 上报SOS救援、定位信息
主要流程：客户端触发SOS救援按钮后，首先向孝老中心服务号码和两个亲情号码发送救援短信，然后进行定位；客户端先将定位信息向孝老平台接口传送定位数据~~  
~~2. 设备端（客户端）修改亲情号码后同步至孝老平台  
主要流程：客户端修改亲情号码后，同步到孝老平台~~  
~~3. 孝老平台下发修改设备亲情号码等信息  
主要流程：服务端下发设置设备固定亲情号码指令，设备应返回设置是否成功标识~~  
~~4. 设备一开机就上报定位数据至孝老平台，之后设备间隔一段时间上报一次定位数据至孝老平台  
主要流程： 客户端开机时,进行定位 ，并将定位相关数据传给孝老平台; 客户端根据默认间隔时间，每间隔如：30分钟上传一次定位至孝老平台~~  
~~5. 孝老平台随时下发查询指令后，能随时定位到设备的位置  
主要流程：孝老平台向客户端发送实时定位指令，客户端进行定位，定位成功后，客户端将定位数据上传到接口4，将定位数据上传给孝老平台~~  
~~6. 孝老平台随时下发修改设备定位上传频率~~  
~~7. 通知设备用户超出电子围栏
主要流程：根据孝老平台设置的电子围栏，判断定位上传信息，如果超出电子围栏，并通知设备，设备接收到此消息后向亲情号码发送超出电子围栏手机信息~~
~~8. 获取设备状态  
主要流程：孝老平台下发获取设备相关状态信息~~

### 主要步骤
1. 收到开机广播,同时开启定时服务和sticky service(会打开socket连接,准备接收服务器数据)
2. socket连接成功则开启心跳服务(五分钟启动一次), 连接失败则开启重连服务
3. 每隔30分钟启动一次定位服务(后台服务会开始用百度进行定位)
4. 如果定位失败如超时,则停止定位服务(百度定位服务在单独的进程)
5. 如果定位成功,将定位信息上传到服务器,并停止定位服务
6. 将本地孝老平台设置号码上传到服务器
7. 如果客户按了SOS键,则将SOS定位信息上传到服务器
8. socket有三个线程, 一个用于连接socket, 一个用于接收服务器指令并响应, 一个用于主动发送消息到服务器
9. 对socket连接接收的数据进行解析, 判断是服务器主动发送的指令(此时要返回数据给服务器)还是客户端发送数据到服务器后服务器的返回数据(此时无需再向服务器写数据, 只打印出来即可)
10. 如果接收到服务器的如下5种指令, 需要做出相应处理:  
> 如果是修改设备亲情号码的指令: 将服务器上设置的号码同步到本地;   
> 如果是查询位置指令: 将目前定位信息上传到服务器;  
> 如果是修改设备定位上传频率指令: 则修改定时服务的时间间隔;  
> 如果是超出电子围栏指令: 则向亲情号码发送短信;  
> 如果是获取设备状态指令: 则将终端目前设备相关信息上传到服务器;  

对传递到服务器的参数data,先转成JSON格式,再排序,然后按照UTF-8编码  
```
SosPositionParam reportParamBean = new SosPositionParam(
        imei,
        SOCKET_COMPANY,
        type,
        positionType,
        time,
        locType,
        longitude,
        latitude,
        capacity
);

final String gsonString = SosPositionParam.getReportParamGson(reportParamBean);

try
{
    encodedData = URLEncoder.encode(data, SOCKET_ENCODE_TYPE);
}
catch (UnsupportedEncodingException e)
{
    e.printStackTrace();
}
```
再取整个编码后的数据长度,放到前四个字节一起传输:  
```
completedStr = getDataPrefix(encodedData) + encodedData;
```
~~对传递到服务器的参数sign,需要进行MD5编码, 服务器端会用同样步骤生成sign, 和本地传递过去的sign进行对比, 以确认数据在网络传输过程中是否被修改~~    
```
sign = MD5Utils.encrypt(data + RXJAVAHTTP_SECRET_CODE);
```

### 友情提示
常用参数配置见Constant.java

### 问题汇总
#### 接口调试中的问题
##### sos exits null
服务器接口地址必须无误, 参数名字必须无误, 每个参数都不能传递空值

##### company error
company是客户名字,每个项目不一样

##### sos error ID
可能是sign有问题,需要检查双方的secretCode是否一致

##### 没有任何提示信息
`{"success":0,"imei":"867400020316620","time":"20180802104641"}`  
可能是IMEI没有入库, 需要第三方协助入库操作

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
不管是基站或者Wifi MAC地址,都需要连接谷歌服务器获取定位信息,由于众所周知的原因,目前无法实现; 现在方案是内置百度SDK, 百度内部已经实现了这三种定位方式,不过我们仅用基站网络(GPS不仅耗电,且与其性能有直接关系,暂不采用)来实现定位功能

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

#### 问题4 读取其他应用SharedPreferences失败
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
不过已经被废弃, Android建议使用ContentProvider进行跨进程读写数据
```
在某些Android版本上无法可靠工作, 并且不提供任何保证跨进程数据同步一致性的机制, 应该尽量避免使用, 建议使用ContentProvider进行跨进程读写数据
does not work reliably in some versions of Android, and furthermore does not provide any mechanism for reconciling concurrent modifications across processes. Applications should not attempt to use it. Instead, they should use an explicit cross-process data management approach such as {@link android.content.ContentProvider ContentProvider}.
```

#### 问题5 AS找不到`SystemProperties`
在build.gradle的android标签里添加:  
```
String SDK_DIR = System.getenv("ANDROID_SDK_HOME")
if(SDK_DIR == null) {
    Properties props = new Properties()
    props.load(new FileInputStream(project.rootProject.file("local.properties")))
    SDK_DIR = props.get('sdk.dir');
}
dependencies {
    provided files("${SDK_DIR}/platforms/android-24/data/layoutlib.jar")
}
```

#### 问题6 生成的签名APK 内置到工程, 无法安装成功
```
01-01 08:01:51.094   576   620 E PackageParser: Package com.hsf1002.sky.xljgps has no certificates at entry AndroidManifest.xml; ignoring!
```
原因是当前Android系统是4.4, 还不支持V2(full APK signature)的方式, 以此方式签名, 系统无法识别  
* 可以用系统的签名文件以方式进行签名
```
java -Djava.library.path=. -jar signapk.jar platform.x509.pem platform.pk8 app-release.apk  app-release_signed.apk  
如果报错Exception in thread "main" java.lang.UnsatisfiedLinkError: no conscrypt_openjdk_jni in java.library.path  
需要把./linux-x86/lib64/libconscrypt_openjdk_jni.so 拷贝过来
```
* Android 7.0 引入一项新的应用签名方案 APK Signature Scheme v2，它能提供更快的应用安装时间和更多针对未授权 APK 文件更改的保护; 在Generated Signed APK 这一步, 单独选择V1(jar Signature), 或者和V2(full APK Signature)一起选择, 如果单独选择V2则会报错, 也可以在build.gradle中添加`v2SigningEnabled false`禁止掉V2签名方式;   
```
signingConfigs{
    releaseConfig{
        keyAlias 'key'
        keyPassword '123456'
        storeFile file('/home/workspace1/workplace/android/gps.jks')
        storePassword '123456'
        v2SigningEnabled false
```

#### 问题7 百度定位只有第一次成功,后面一直失败, 返回码505
AK有误,需保证应用的AK正确无误,而AK只与包名和JKS文件的SHA1有关(AK 申请地址: http://lbsyun.baidu.com/apiconsole/key ), 获取APK的签名文件SHA1方法:  
```
keytool -list -keystore gps.jks
```

#### 问题8 手动安装可以正常定位, 把apk内置到system/app下定位失败, 返回码162
```
java.lang.UnsatisfiedLinkError: Couldn't load locSDK7b from loader dalvik.system.PathClassLoader[DexPathList[[zip file "/system/app/XLJGps.apk"],nativeLibraryDirectories=[/system/lib]]]: findLibrary returned null
```
手动安装会提示加载成功:  
```
dll /data/app-lib/com.hsf1002.sky.xljgps-1/liblocSDK7b.so load success
```

通过`cat /proc/cpuinfo`可以查看手机CPU架构和指令集  
so库是NDK编译出来的动态链接库, 一些重要的加密算法或者核心协议一般都用c写然后给java调用, 这样可以避免反编译后查看到应用的源码; 为了减小 apk 体积，一般只保留 armeabi 和 armeabi-v7a 两个文件夹，并保证这两个文件夹中 .so 数量一致, 对只提供 armeabi 版本的第三方 .so，原样复制一份到 armeabi-v7a 文件夹

* armeabi: ARM v5 是相当老旧的一个版本，缺少对浮点数计算的硬件支持，在需要大量计算时有性能瓶颈, 已经很少见
* armeabi-v7a: ARM v7 目前主流版本
* arm64-v8a: 64位支持
* x86/x86_64: Intel提供的称为 Houdini 的指令集动态转码工具，实现对 arm .so 的兼容,通常对应虚拟机，比如在genymotion上开发调试, 即intel IA32

#### 问题9 socket连接一个线程写入的数据, 被另一个线程读取了
* 客户端发送数据给服务器端, 服务器端返回数据给客户端
* 服务器端发送数据给客户端, 客户端返回数据给服务器端

如果socket连接的这两个操作只用一个线程来做读和写操作无法实现, 因为这个线程是个死循环, 客户端发送数据给服务器端后会处于阻塞状态, 无法退出, 而两个线程不能同时做读和写的操作, 否则会出现一个线程写入的数据, 被另一个线程读取了; 客户端发送的数据可以在一个线程处理, 只写不读, 另一个线程先读取服务器端发送的数据, 再解析其数据到底是服务器端主动发送的数据还是返回给客户端的消息

#### 问题10 进入孝老平台主界面无法退出
在onKeyDown中不小心返回了true, 又没有处理KEYCODE_BACK

#### 问题11 孝老平台主界面RecyclerView添加选中框
MainRecycleAdapter中添加:  
```
private int selectedPos = -1;
private int oldPos = -1;

@Override
public void onBindViewHolder(ViewHolder holder, int position) {
    ...
    if(selectedPos == holder.getPosition()) {
        holder.itemName.setBackgroundColor(GpsApplication.getAppContext().getResources().getColor(R.color.list_item_focuse));
    } else {
        holder.itemName.setBackgroundColor(GpsApplication.getAppContext().getResources().getColor(R.color.background_holo_light));
    }
}

public void refreshItem(int position) {
    if (selectedPos != -1) {
        oldPos  = selectedPos;
    }

    selectedPos = position;

    if (oldPos != -1) {
        notifyItemChanged(oldPos);
    }
    notifyItemChanged(selectedPos);
}
```
然后在MainActivity的onCreate中调用:  
```
adapter.refreshItem(currentPosition);

```
并添加onKeydown():
```
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    int count = getResources().getStringArray(R.array.main_item_name).length;
    switch (keyCode)
    {
        case KEYCODE_DPAD_UP:
            if (currentPosition == 0)
            {
                currentPosition = count - 1;
            }
            else
            {
                currentPosition--;
            }
            break;
        case KEYCODE_DPAD_DOWN:
            if (currentPosition == count - 1)
            {
                currentPosition = 0;
            }
            else
            {
                currentPosition++;
            }
            break;
        case KEYCODE_DPAD_CENTER:
            handlePlatformItems(currentPosition);
            break;
    }
    adapter.refreshItem(currentPosition);
    return super.onKeyDown(keyCode, event);
```

#### 问题12 AlarmService定时服务时间不准, 延迟严重
使用AlarmService开启定时服务, `manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+startServiceInterval, startServiceInterval, pi);` 定时5分钟, 但是有时候6, 7分钟, 有时候长达15分钟才会运行一次
```
08-14 15:15:01.044 2535-2535/com.hsf1002.sky.xljgps I/GpsService: onStartCommand:
08-14 15:22:37.839 2535-2535/com.hsf1002.sky.xljgps I/GpsService: onStartCommand:
08-14 15:30:00.992 2535-2535/com.hsf1002.sky.xljgps I/GpsService: onStartCommand:
08-14 15:45:01.040 2535-2535/com.hsf1002.sky.xljgps I/GpsService: onStartCommand:
08-14 15:52:56.153 2535-2535/com.hsf1002.sky.xljgps I/GpsService: onStartCommand:
08-14 16:00:01.058 2535-2535/com.hsf1002.sky.xljgps I/GpsService: onStartCommand:
08-14 16:15:00.996 2535-2535/com.hsf1002.sky.xljgps I/GpsService: onStartCommand:
08-14 16:21:06.713 2535-2535/com.hsf1002.sky.xljgps I/GpsService: onStartCommand:
08-14 16:30:01.054 2535-2535/com.hsf1002.sky.xljgps I/GpsService: onStartCommand:
08-14 16:45:01.022 2535-2535/com.hsf1002.sky.xljgps I/GpsService: onStartCommand:
```
使用循环 handler.postDelayed(task, 1000 * 60);的结果, 误差不会超过50ms:  
```
08-15 17:18:05.724 17422-17422/com.hsf1002.sky.xljgps I/GpsService: task: postDelayed-------------------------------------------
08-15 17:19:05.752 17422-17422/com.hsf1002.sky.xljgps I/GpsService: task: postDelayed-------------------------------------------
08-15 17:20:05.761 17422-17422/com.hsf1002.sky.xljgps I/GpsService: task: postDelayed-------------------------------------------
08-15 17:21:05.769 17422-17422/com.hsf1002.sky.xljgps I/GpsService: task: postDelayed-------------------------------------------
08-15 17:22:05.768 17422-17422/com.hsf1002.sky.xljgps I/GpsService: task: postDelayed-------------------------------------------
08-15 17:23:05.787 17422-17422/com.hsf1002.sky.xljgps I/GpsService: task: postDelayed-------------------------------------------
08-15 17:24:05.795 17422-17422/com.hsf1002.sky.xljgps I/GpsService: task: postDelayed-------------------------------------------
08-15 17:25:05.804 17422-17422/com.hsf1002.sky.xljgps I/GpsService: task: postDelayed-------------------------------------------
08-15 17:26:05.812 17422-17422/com.hsf1002.sky.xljgps I/GpsService: task: postDelayed-------------------------------------------
08-15 17:27:05.821 17422-17422/com.hsf1002.sky.xljgps I/GpsService: task: postDelayed-------------------------------------------
```
使用 AlarmManager的 set 方法循环发送广播的方式:  
```
@Override
public void onReceive(Context context, Intent intent) {
    Log.e(TAG,"+++++++++++++++++++>>>onReceive");
    mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+startServiceInterval,  mPendingIntent);
}
```
误差还是比较大, 1分钟的定时服务, 有时候延迟 15秒:   
```
08-15 18:23:25.598 18152-18152/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 18:24:28.650 18152-18152/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 18:25:28.658 18152-18152/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 18:26:28.667 18152-18152/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 18:27:28.665 18152-18152/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 18:29:02.587 18152-18152/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 18:30:15.138 18152-18152/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 18:31:15.136 18152-18152/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 18:32:19.279 18152-18152/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 18:33:19.277 18152-18152/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
```
如果将AlarmManager的 set 改为 setExact, 误差会小很多, 在60ms左右:  
```
08-15 18:55:48.514 21766-21766/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 18:56:48.582 21766-21766/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 18:57:48.631 21766-21766/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 18:58:48.689 21766-21766/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 18:59:48.748 21766-21766/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 19:00:48.776 21766-21766/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 19:01:48.825 21766-21766/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 19:02:48.894 21766-21766/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 19:03:48.962 21766-21766/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
08-15 19:04:49.021 21766-21766/com.hsf1002.sky.xljgps E/GpsService: +++++++++++++++++++>>>onReceive
```
根本原因:  
从Android 4.4 版本开始，Alarm 任务的触发时间将会变得不准确，有可能会延迟一段时间后任务才能得到执行, 这是系统在耗电性方面进行的优化。系统会自动检测目前有多少Alarm 任务存在，然后将触发时间将近的几个任务放在一起执行，这就可以大幅度地减少CPU 被唤醒的次数，从而有效延长电池的使用时间。如果你要求Alarm 任务的执行时间必须准备无误，Android 仍然提供了解决方案。使用AlarmManager 的setExact()方法来替代set()方法  
解决方案:  
相对于定时的准确性而言, 功耗更为重要, 依然采用Android默认的处理方式

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
