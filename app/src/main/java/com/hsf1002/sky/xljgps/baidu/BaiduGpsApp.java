package com.hsf1002.sky.xljgps.baidu;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hsf1002.sky.xljgps.app.XLJGpsApplication;
import com.hsf1002.sky.xljgps.model.RxjavaHttpModel;
import com.hsf1002.sky.xljgps.params.BaiduGpsParam;

import static android.content.Context.WIFI_SERVICE;
import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_GPS_LOCATION_DEFAULT_LANTITUDE;
import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_GPS_LOCATION_DEFAULT_LOCTYPE;
import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_GPS_LOCATION_DEFAULT_LONGITUDE;
import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_GPS_LOCATION_DEFAULT_POSITIONTYPE;
import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_GPS_LOCATION_TYPE_GPS;
import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_GPS_LOCATION_TYPE_LBS;
import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_GPS_LOCATION_TYPE_WIFI;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_TIMING;

/**
 * Created by hefeng on 18-6-6.
 */

public class BaiduGpsApp {
    private static final String TAG = "BaiduGpsApp";
    private LocationClient client;
    private MyLocationLister myLocationLister;
    private LocationClientOption option;
    private static BaiduGpsParam sBaiduGpsMsgBean;

    static
    {
        sBaiduGpsMsgBean = new BaiduGpsParam();
        setBaiduGpsStatus(/*null,*/ null, null, null, null);
    }

    public static BaiduGpsApp getInstance()
    {
        return Holder.instance;
    }

    private static class Holder
    {
        private static final BaiduGpsApp instance = new BaiduGpsApp();
    }

    /**
    *  author:  hefeng
    *  created: 18-8-7 上午9:30
    *  desc:    初始化SDK, listener, client
    *  param:
    *  return:
    */
    public void initBaiduSDK(Context context)
    {
        Log.i(TAG, "initBaiduSDK: ");
        myLocationLister = new MyLocationLister();
        client = new LocationClient(context);
        initLocation();
    }

    /**
    *  author:  hefeng
    *  created: 18-8-7 上午9:31
    *  desc:    初始化配置
    *  param:
    *  return:
    */
    private void initLocation()
    {
        Log.i(TAG, "initOption: ");
        option = new LocationClientOption();
        // LocationMode.Hight_Accuracy：高精度； LocationMode. Battery_Saving：低功耗； LocationMode. Device_Sensors：仅使用设备；
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        //可选，设置返回经纬度坐标类型，默认gcj02, gcj02：国测局坐标； d09ll：百度经纬度坐标； bd09：百度墨卡托坐标； 海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标
        //option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        //可选，设置发起定位请求的间隔，int类型，单位ms, 如果设置为0，则代表单次定位，即仅定位一次，默认为0, 如果设置非0，需设置1000ms以上才有效
        option.setScanSpan(5000);
        //可选，设置是否使用gps，默认false, 使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setOpenGps(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.setIgnoreKillProcess(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        //option.setEnableSimulateGps(false);
        //mLocationClient为第二步初始化过的LocationClient对象, 需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        client.setLocOption(option);
    }

    /**
    *  author:  hefeng
    *  created: 18-8-7 上午9:31
    *  desc:    启动定位
    *  param:
    *  return:
    */
    public void startBaiduGps()
    {
        Log.i(TAG, "startBaiduGps: isStarted = " + client.isStarted());
        if (!client.isStarted())
        {
            client.registerLocationListener(myLocationLister);
            client.start();
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-7 上午9:31
    *  desc:    停止定位
    *  param:
    *  return:
    */
    public void stopBaiduGps() {
        Log.i(TAG, "stopBaiduGps: isStarted = " + client.isStarted());
        if (client.isStarted()) 
        {
            client.unRegisterLocationListener(myLocationLister);
            client.stop();
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-7 上午9:32
    *  desc:    重新启动定位,暂时不会调用
    *  param:
    *  return:
    */
    public void restartBaiduGps()
    {
        Log.i(TAG, "restartBaiduGps: ");
        client.registerLocationListener(myLocationLister);
        client.restart();
    }

    /**
     *  author:  hefeng
     *  created: 18-8-7 上午9:32
     *  desc:    设置每次定位时间间隔,暂时不会调用
     *  param:
     *  return:
     */
    public void setBaiduGpsScanSpan(int span)
    {
        Log.i(TAG, "setBaiduGpsScanSpan: ");
        option.setScanSpan(span);
    }

    /**
    *  author:  hefeng
    *  created: 18-8-6 上午9:58
    *  desc:
    * http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/addition-func/error-code
    * bdLocation.getLocType()
    * 若返回值是162~167，请将错误码、IMEI、定位唯一标识（自v7.2版本起，通过BDLocation.getLocationID方法获取）和定位时间反馈至邮箱loc-bugs@baidu.com
    *   61: GPS success
    *   62: 无法获取有效定位依据，定位失败，请检查运营商网络或者WiFi网络是否正常开启，尝试重新请求定位
    *   63: 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位
    *   161: Network Success
    *   162: 请求串密文解析失败，一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件
    *   167: 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位
    *   505: AK不存在或者非法，请按照说明文档重新申请AK(1. 将申请的AK写入AndroidMenifest.xml 2. AS->Build中Generated Signed APK再进行安装)
    *  在应用内部, 断网, 还是会取到上次联网时的定位, 退出应用再进入则是断网 63 状态, 此时联网则可正常定位
    */
    public class MyLocationLister extends BDAbstractLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String latitude = String.valueOf(bdLocation.getLatitude());
            String longitude = String.valueOf(bdLocation.getLongitude());
            String locationType = BAIDU_GPS_LOCATION_TYPE_LBS;
            int locType = bdLocation.getLocType();
            String locTypeStr = "1";        // 1->"Baidu" 2->"GaoDe";
            StringBuilder address = new StringBuilder();

            address.append(bdLocation.getCountry()).append(bdLocation.getCity()).append(bdLocation.getDistrict()).append(bdLocation.getStreet());

            if (locType == BDLocation.TypeGpsLocation)
            {
                locationType = BAIDU_GPS_LOCATION_TYPE_GPS;
            }
            else if (locType == BDLocation.TypeNetWorkLocation)
            {
                WifiManager wifiManager = (WifiManager)XLJGpsApplication.getAppContext().getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED && wifiInfo != null)
                {
                    String ssid = wifiInfo.getSSID();
                    Log.i(TAG, "onReceiveLocation: ssid = " + ssid);

                    if (!TextUtils.isEmpty(ssid))
                    {
                        locationType = BAIDU_GPS_LOCATION_TYPE_WIFI;
                    }
                }
                else
                {
                    locationType = BAIDU_GPS_LOCATION_TYPE_LBS;
                }
            }
            else
            {
                locationType = "null";
            }
            Log.i(TAG, "onReceiveLocation: locType = " + locType + ", latitude = " + latitude + ", longitude = " + longitude + ", address = " + address.toString() + ", locationType = " + locationType + ", getLocType = " + locType);

            if (locType == BDLocation.TypeGpsLocation || locType == BDLocation.TypeNetWorkLocation)
            {
                Log.i(TAG, "onReceiveLocation:  get location success, stop gps service");
                setBaiduGpsStatus(/*address.toString(), */latitude, longitude, locTypeStr, locationType);

                //RxjavaHttpModel.getInstance().reportPosition(RXJAVAHTTP_TYPE_TIMING, null);
            }
            else
            {
                Log.i(TAG, "onReceiveLocation:  get location failed, stop gps service");
            }
            stopBaiduGps();
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            super.onConnectHotSpotMessage(s, i);
        }

        @Override
        public void onLocDiagnosticMessage(int i, int i1, String s) {
            super.onLocDiagnosticMessage(i, i1, s);
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-6 上午11:23
    *  desc:    需要保证上传的参数的值不为空, 否则上传失败, 如果定位失败, 给一个默认值
    *  param:
    *  return:
    */
    private static void setBaiduGpsStatus(/*String address,*/ String latitude, String longitude, String locType, String position_type)
    {
        /*
        if (TextUtils.isEmpty(address))
        {
            sBaiduGpsMsgBean.setAddress("");
        }
        else
        {
            sBaiduGpsMsgBean.setAddress(address);
        }*/

        if (TextUtils.isEmpty(latitude))
        {
            sBaiduGpsMsgBean.setLatitude(BAIDU_GPS_LOCATION_DEFAULT_LANTITUDE);
        }
        else
        {
            sBaiduGpsMsgBean.setLatitude(latitude);
        }

        if (TextUtils.isEmpty(longitude))
        {
            sBaiduGpsMsgBean.setLongitude(BAIDU_GPS_LOCATION_DEFAULT_LONGITUDE);
        }
        else
        {
            sBaiduGpsMsgBean.setLongitude(longitude);
        }

        if (TextUtils.isEmpty(locType))
        {
            sBaiduGpsMsgBean.setLoc_type(BAIDU_GPS_LOCATION_DEFAULT_LOCTYPE);
        }
        else
        {
            sBaiduGpsMsgBean.setLoc_type(locType);
        }

        if (TextUtils.isEmpty(position_type))
        {
            sBaiduGpsMsgBean.setPosition_type(BAIDU_GPS_LOCATION_DEFAULT_POSITIONTYPE);
        }
        else
        {
            sBaiduGpsMsgBean.setPosition_type(position_type);
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-7 上午9:32
    *  desc:    取得当前GPS定位信息
    *  param:
    *  return:
    */
    public BaiduGpsParam getBaiduGpsStatus()
    {
        return sBaiduGpsMsgBean;
    }
}
