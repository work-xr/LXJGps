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
2. 可以使用jarsigner方式对应用进行签名, 然后将整个apk以拷贝文件方式拷贝到system/app下(此时将不会再进行系统签名) , 如果要编译代码, 可配置Android.mk使用应用自带签名    
```
LOCAL_CERTIFICATE := PRESIGNED
```
