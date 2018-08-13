package com.hsf1002.sky.xljgps.util;

/**
 * Created by hefeng on 18-6-8.
 */

public class Constant {
    public static final int SET_RELATION_NUMBER_INDEX = 0;
    public static final int UPLOAD_INFO_TO_PLATFORM_INDEX = 1;
    //public static final int DOWNLOAD_INFO_FROM_PLATFORM_INDEX = 2;
    public static final int REPORT_INFO_TO_PLATFORM_INDEX = 2;

    public static final String RELATION_NUMBER = "relation_number_";
    public static final String RELATION_NUMBER_COUNT = "relation_number_count";
    public static final String RELATION_NAME = "relation_name_";
    //public static final String RELATION_NAME_COUNT = "relation_name_count";
    public static final String RELATION_NUMBER_DEFAULT = "12349";

    public static final String SHARED_PREFERENCE_NAME = "lxjgps_sp";

    //public static final String RXJAVAHTTP_BASE_URL_DOUBAN = "https://api.douban.com/";
    public static final String RXJAVAHTTP_BASE_URL_TEST = "http://healthdata.4000300659.com:8088/api/xiaolajiaotest/";

    public static final String RXJAVAHTTP_BASE_URL_FORMAL = "http://healthdata.4000300659.com:8088/api/xiaolajiao/";
    public static final String RXJAVAHTTP_REPORT_SOSPOSITION = "sosPosition";
    public static final String RXJAVAHTTP_DOWNLOAD_FROM_PLATFORM = "familyNumber";
    public static final String RXJAVAHTTP_UPDATE_TO_PLATFORM = "updatePhone";
    public static final String RXJAVAHTTP_REPORT_POSITION = "position";
    public static final String RXJAVAHTTP_IMEI = "867400088889999"; //867400020316620
/*
    public static final String RXJAVAHTTP_TYPE_DOWNLOAD = "103";
    //public static final String RXJAVAHTTP_TYPE_REPORT = "202";
    public static final String RXJAVAHTTP_TYPE_SOS = "202";         // 是通过按SOS键上报消息
    public static final String RXJAVAHTTP_TYPE_UPLOAD = "203";
    public static final String RXJAVAHTTP_TYPE_CURRENT = "101";     // 收到服务器指令, 实时定位后上报消息
    public static final String RXJAVAHTTP_TYPE_POWERON = "100";     // 开机上报消息
    public static final String RXJAVAHTTP_TYPE_TIMING = "201";      // 定时定位后上报消息
    public static final String RXJAVAHTTP_TYPE_INTERVAL = "106";                // 代表设置定位上传频率
    public static final String RXJAVAHTTP_TYPE_OUTER_ELECTRIC_BAR = "107";      //代表电子围栏超出通知
    public static final String RXJAVAHTTP_TYPE_GET_STATUS_INFO = "108";         //代表获取设备状态信息
*/
    public static final int RXJAVAHTTP_TYPE_BEATHEART = 301;    // 主动: 上报心跳
    public static final int RXJAVAHTTP_TYPE_SOS = 202;          // 主动: 通过按SOS键上报消息
    public static final int RXJAVAHTTP_TYPE_UPLOAD = 203;       // 主动: 上传亲情号码
    public static final int RXJAVAHTTP_TYPE_POWERON = 100;      // 主动: 开机上报消息
    public static final int RXJAVAHTTP_TYPE_TIMING = 201;       // 主动: 定时定位后上报消息
    public static final int RXJAVAHTTP_TYPE_CURRENT = 101;                  // 被动: 接收服务器指令后, 实时定位后上报消息
    public static final int RXJAVAHTTP_TYPE_DOWNLOAD = 103;                 // 被动: 接收服务器指令后, 设置亲情号码
    public static final int RXJAVAHTTP_TYPE_INTERVAL = 106;                 // 被动: 接收服务器指令后, 设置定位上传频率
    public static final int RXJAVAHTTP_TYPE_OUTER_ELECTRIC_BAR = 107;       // 被动: 接收服务器指令后, 电子围栏超出通知
    public static final int RXJAVAHTTP_TYPE_GET_STATUS_INFO = 108;          // 被动: 接收服务器指令后, 获取设备状态信息
    public static final String RXJAVAHTTP_COMPANY = "xiaolajiao";
    public static final String RXJAVAHTTP_SECRET_CODE = "ab2796145acdf";//"8s6HiyY0yGCLeZp5";     //ab2342145acdf;    // iETOECQ9kiJw75AZ
    public static final String RXJAVAHTTP_ENCODE_TYPE = "UTF-8";

    public static final int RXJAVAHTTP_READ_TIMEOUT = 100;
    public static final int RXJAVAHTTP_WRITE_TIMEOUT = 100;
    public static final int RXJAVAHTTP_CONNCET_TIMEOUT = 100;

    // 每隔多久启动一次IntentService服务来开始定位,可由服务器端通过调用setStartServiceInterval进行设置更改
    public static final String BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL_NAME = "baidu_gps_scan_span_interval_name";
    public static final int BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL_VALUE = 2 * 60 * 1000;
    // 如果不用service, 百度默认多久发起一次定位
    public static final int BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL = 1 * 60 * 1000;
    // 每次启动百度地图服务XLJGpsService.setServiceAlarm(getApplicationContext(), true);, 如果3分钟内没有定位成功, 自动停止地图服务stopBaiduGps
    //public static final int BAIDU_GPS_FIRST_SCAN_TIME_MAX = 3 * 60 * 1000;
    public static final String BAIDU_GPS_LOCATION_TYPE_GPS = "gps";
    public static final String BAIDU_GPS_LOCATION_TYPE_LBS = "lbs";
    public static final String BAIDU_GPS_LOCATION_TYPE_WIFI = "wifi";

    public static final String BAIDU_GPS_LOCATION_DEFAULT_LONGITUDE = "113.957178";
    public static final String BAIDU_GPS_LOCATION_DEFAULT_LANTITUDE = "22.537702";
    public static final String BAIDU_GPS_LOCATION_DEFAULT_LOCTYPE = "1";    // 1: baidu map  2: gaode map
    public static final String BAIDU_GPS_LOCATION_DEFAULT_POSITIONTYPE = "lbs";

    // 按键sos后发此广播, 收到广播后,上传信息到平台
    public static final String ANCTION_SOS_REPORT_POSITION = "action.sos.report.position";

    // SOS包的名字
    public static final String SOS_PACKAGE_NAME = "com.android.sos";
    // SOS包的SharedPreferences的名字
    public static final String SOS_NUM_PREFS_NAME = "sos_num_prefs";
    public static final String SOS_NUM_PREFS_ = "sosNum";
    // 第一个紧急呼叫号码的KEY
    public static final String SOS_NUM_PREFS_1 = "sosNum1";
    // 第二个紧急呼叫号码的KEY
    public static final String SOS_NUM_PREFS_2 = "sosNum2";
    // 第三个紧急呼叫号码的KEY
    public static final String SOS_NUM_PREFS_3 = "sosNum3";

    public static final String SOS_NUM_PROPERTY_1 = "persist.sys.sos.num1";
    public static final String SOS_NUM_PROPERTY_2 = "persist.sys.sos.num2";
    public static final String SOS_NUM_PROPERTY_3 = "persist.sys.sos.num3";
    public static final String SOS_SMS_PROPERTY_MSG = "persist.sys.sos.msg";
    public static final String SOS_NUM_INVALID_VALUE = "FFFF";

    //public static final String SOS_SMS_PREFS_NAME = "sos_msg_prefs";
    //public static final String SOS_SMS_PREFS_MSG = "sosMsg";

    public static final int SOS_NUM_COUNT = 3;

    public static final String ACTION_SET_RELATION_NUMBER = "intent.action.SET_RELATION_NUMBER";
    public static final String SET_RELATION_NUMBER = "com.hsf1002.sky.xljgps.setrelationnumber";

    public static final int RESULT_SUCCESS_0 = 0;
    public static final int RESULT_SUCCESS_1 = 1;
    public static final String RESULT_MSG_FAILING = "failing";
    public static final String RESULT_MSG_SUCCESS = "success";
    public static final int RESULT_STATUS_POWERON = 1;
    public static final int RESULT_STATUS_POWEROFF = 0;

    public static final String RESULT_PARAM_IMEI = "imei";
    public static final String RESULT_PARAM_INTERVAL = "interval";
    public static final String RESULT_PARAM_TYPE = "type";
    public static final String RESULT_PARAM_TIME = "time";
    public static final String RESULT_PARAM_COMMAND = "command";
    public static final String RESULT_PARAM_NAME = "name";
    public static final String RESULT_PARAM_NUMBER = "sos_phone";

    public static final String SOCKET_SERVER_ADDRESS_URL = "healthdata.4000300659.com";
    public static final int SOCKET_SERVER_ADDRESS_PORT = 12004;

    public static final int BEATHEART_SERVICE_INTERVAL = 5 * 60 * 1000;
    public static final int RECONNCET_SOCKET_SERVICE_INTERVAL = 1 * 60 * 1000;
}
