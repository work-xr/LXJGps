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

    public static final String RXJAVAHTTP_BASE_URL_DOUBAN = "https://api.douban.com/";
    public static final String RXJAVAHTTP_BASE_URL_TEST = "http://healthdata.4000300659.com:8088/api/xiaobawangtest/";
    public static final String RXJAVAHTTP_BASE_URL_FORMAL = "http://healthdata.4000300659.com:8088/api/xiaobawang/";
    public static final String RXJAVAHTTP_REPORT_SOSPOSITION = "sosPosition";
    public static final String RXJAVAHTTP_DOWNLOAD_FROM_PLATFORM = "familyNumber";
    public static final String RXJAVAHTTP_UPDATE_TO_PLATFORM = "phone";
    public static final String RXJAVAHTTP_REPORT_POSITION = "position";

    public static final String RXJAVAHTTP_BASE_PERSON_URL = "http://192.168.100.62:8081/api/test/";
    public static final String RXJAVAHTTP_PERSON_LIST = "person";
    public static final String RXJAVAHTTP_PERSON_ID = "person/{id}";

    public static final String RXJAVAHTTP_BASE_GPS_URL = "http://192.168.100.62:8081/api/";
    public static final String RXJAVAHTTP_BASE_GPS_URL_TEST = "http://192.168.100.62:8081/api/test/";
    public static final String RXJAVAHTTP_GPS = "gps";

    public static final String RXJAVAHTTP_TYPE_DOWNLOAD = "103";
    public static final String RXJAVAHTTP_TYPE_UPLOAD = "203";
    public static final String RXJAVAHTTP_TYPE_REPORT = "202";
    //101:实时定位数据；100：开机；201：定时上传定位数据
    public static final String RXJAVAHTTP_TYPE_CURRENT = "101";
    public static final String RXJAVAHTTP_TYPE_POWERON = "100";
    public static final String RXJAVAHTTP_TYPE_TIMING = "201";

    public static final String RXJAVAHTTP_COMPANY = "XUNRUI";
    public static final String RXJAVAHTTP_SECRET_CODE = "iETOECQ9kiJw75AZ";//ab2342145acdf;    // iETOECQ9kiJw75AZ
    public static final String RXJAVAHTTP_ENCODE_TYPE = "UTF-8";
    public static final String RXJAVAHTTP_NAME = "xunrui";

    public static final int RXJAVAHTTP_READ_TIMEOUT = 100;
    public static final int RXJAVAHTTP_WRITE_TIMEOUT = 100;
    public static final int RXJAVAHTTP_CONNCET_TIMEOUT = 100;

    // 每隔多久启动一次IntentService服务来开始定位,可由服务器端通过调用setStartServiceInterval进行设置更改
    public static final int BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL = 1 * 60 * 1000;
    // 每次启动百度地图服务XLJGpsService.setServiceAlarm(getApplicationContext(), true);, 如果3分钟内没有定位成功, 自动停止地图服务stopBaiduGps
    public static final int BAIDU_GPS_FIRST_SCAN_TIME_MAX = 3 * 60 * 1000;
    public static final String BAIDU_GPS_LOCATION_TYPE_GPS = "gps";
    public static final String BAIDU_GPS_LOCATION_TYPE_LBS = "lbs";
    public static final String BAIDU_GPS_LOCATION_TYPE_WIFI = "wifi";

    public static final String RELATION_NUMBER = "relation_number_";
    public static final String RELATION_NUMBER_COUNT = "relation_number_count";
    public static final String RELATION_NAME = "relation_name_";
    public static final String RELATION_NAME_COUNT = "relation_name_count";
}
