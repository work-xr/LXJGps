package com.hsf1002.sky.xljgps.util;

/**
 * Created by hefeng on 18-6-8.
 */

public class Const {
    public static final int SET_RELATION_NUMBER_INDEX = 0;
    public static final int UPLOAD_INFO_TO_PLATFORM_INDEX = 1;
    public static final int DOWNLOAD_INFO_FROM_PLATFORM_INDEX = 2;
    public static final int REPORT_INFO_TO_PLATFORM_INDEX = 3;

    public static final String SHARED_PREFERENCE_NAME = "lxjgps_sp";

    public static final String RXJAVAHTTP_BASE_URL = "https://api.douban.com/";
    public static final int RXJAVAHTTP_READ_TIMEOUT = 100;
    public static final int RXJAVAHTTP_WRITE_TIMEOUT = 100;
    public static final int RXJAVAHTTP_CONNCET_TIMEOUT = 100;

    // 每隔多久启动一次IntentService服务来开始定位,可由服务器端通过调用setStartServiceInterval进行设置更改
    public static final int BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL = 1 * 60 * 1000;
    // 每次启动百度地图服务XLJGpsService.setServiceAlarm(getApplicationContext(), true);, 如果3分钟内没有定位成功, 自动停止地图服务stopBaiduGps
    public static final int BAIDU_GPS_FIRST_SCAN_TIME_MAX = 3 * 60 * 1000;
}
