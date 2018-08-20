package com.hsf1002.sky.xljgps.util;

/**
 * Created by hefeng on 18-6-8.
 */

public class Constant {
    // 孝老平台主界面List三个菜单
    //public static final int SET_RELATION_NUMBER_INDEX = 0;
    public static final int UPLOAD_INFO_TO_PLATFORM_INDEX = 0;
    public static final int REPORT_INFO_TO_PLATFORM_INDEX = 1;
    // 孝老平台中心号码
    public static final String RELATION_NUMBER = "relation_number_";
    public static final String RELATION_NUMBER_COUNT = "relation_number_count";
    public static final String RELATION_NAME = "relation_name_";
    public static final String RELATION_NUMBER_DEFAULT = "12349";
    // 客户端向孝老平台服务器上传数据类型, 以及服务器下发的指令类型
    public static final int SOCKET_TYPE_BEATHEART = 301;    // 主动: 上报心跳
    public static final int SOCKET_TYPE_SOS = 202;          // 主动: 通过按SOS键上报消息
    public static final int SOCKET_TYPE_UPLOAD = 203;       // 主动: 上传亲情号码
    public static final int SOCKET_TYPE_POWERON = 100;      // 主动: 开机上报消息
    public static final int SOCKET_TYPE_TIMING = 201;       // 主动: 定时定位后上报消息
    public static final int SOCKET_TYPE_CURRENT = 101;                  // 被动: 接收服务器指令后, 实时定位后上报消息
    public static final int SOCKET_TYPE_DOWNLOAD = 103;                 // 被动: 接收服务器指令后, 设置亲情号码
    public static final int SOCKET_TYPE_INTERVAL = 106;                 // 被动: 接收服务器指令后, 设置定位上传频率
    public static final int SOCKET_TYPE_OUTER_ELECTRIC_BAR = 107;       // 被动: 接收服务器指令后, 电子围栏超出通知
    public static final int SOCKET_TYPE_GET_STATUS_INFO = 108;          // 被动: 接收服务器指令后, 获取设备状态信息
    // 项目名称, 即客户名称
    public static final String SOCKET_COMPANY = "xiaolajiao";
    // 传输数据的编码类型
    public static final String SOCKET_ENCODE_TYPE = "UTF-8";
    public static final int THREAD_KEEP_ALIVE_TIMEOUT = 60;

    // 每隔多久启动一次IntentService服务来开始定位,可由服务器端通过调用setStartServiceInterval进行设置更改
    public static final String BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL_NAME = "baidu_gps_scan_span_interval_name";
    // 如果不用定时service,而且不停止百度service, 百度默认多久发起一次定位
    public static final int BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL = 1 * 60 * 1000;
    // gps服务开始后过一段时间再开始定位
    public static final int BAIDU_GPS_FIRST_WAIT_DURATION = 2 * 60 * 1000;

    // 百度定位方式, 目前只有lbs一种
    public static final String BAIDU_GPS_LOCATION_TYPE_GPS = "gps";
    public static final String BAIDU_GPS_LOCATION_TYPE_LBS = "lbs";
    public static final String BAIDU_GPS_LOCATION_TYPE_WIFI = "wifi";
    // 由于上传给服务器的数据不能为空, 定位数据的默认值
    public static final String BAIDU_GPS_LOCATION_DEFAULT_LONGITUDE = "113.957178";
    public static final String BAIDU_GPS_LOCATION_DEFAULT_LANTITUDE = "22.537702";
    public static final String BAIDU_GPS_LOCATION_DEFAULT_LOCTYPE = "1";    // 1: baidu map  2: gaode map
    public static final String BAIDU_GPS_LOCATION_DEFAULT_POSITIONTYPE = "lbs";

    // 按键SOS后发此广播, 收到广播后,上传信息到平台
    public static final String ANCTION_SOS_REPORT_POSITION = "action.sos.report.position";

    // 将SOS的一个紧急号码和紧急短信, 三个亲情号码保存到SystemProperties, 该应用只读,写操作会发送广播让SOS和SimpleLauncher模块实现
    public static final String SOS_EMERGENCY_NUM_PROPERTY = "persist.sys.sos.emergency.num";
    public static final String SOS_NUM_PROPERTY_1 = "persist.sys.sos.num1";
    public static final String SOS_NUM_PROPERTY_2 = "persist.sys.sos.num2";
    public static final String SOS_NUM_PROPERTY_3 = "persist.sys.sos.num3";
    public static final String SOS_NAME_PROPERTY_1 = "persist.sys.sos.name1";
    public static final String SOS_NAME_PROPERTY_2 = "persist.sys.sos.name2";
    public static final String SOS_NAME_PROPERTY_3 = "persist.sys.sos.name3";
    public static final String SOS_SMS_PROPERTY_MSG = "persist.sys.sos.msg";
    public static final String SOS_NUM_INVALID_VALUE = "FFFF";
    public static final String SOS_NAME_INVALID_VALUE = "FFFF";
    public static final String SOS_SMS_INVALID_VALUE = "FFFF";
    public static final int SOS_NUM_COUNT = 3;
    // 给SOS发送设置紧急呼叫号码的广播
    public static final String ACTION_SET_RELATION_NUMBER = "intent.action.SET_RELATION_NUMBER";
    public static final String SET_RELATION_NUMBER = "com.hsf1002.sky.xljgps.setrelationnumber";

    // 返回给服务器的数据参数, 0表示失败, 1表示成功
    public static final int RESULT_SUCCESS_0 = 0;
    public static final int RESULT_SUCCESS_1 = 1;
    public static final String RESULT_MSG_FAILING = "failing";
    public static final String RESULT_MSG_SUCCESS = "success";
    // 返回给服务器的数据参数, 当前肯定是开机状态
    public static final int RESULT_STATUS_POWERON = 1;
    public static final int RESULT_STATUS_POWEROFF = 0;
    // 返回给服务器的数据参数, 各个参数的名称
    public static final String RESULT_PARAM_IMEI = "imei";
    public static final String RESULT_PARAM_INTERVAL = "interval";
    public static final String RESULT_PARAM_TIME = "time";
    public static final String RESULT_PARAM_COMMAND = "command";
    public static final String RESULT_PARAM_NAME = "name";
    public static final String RESULT_PARAM_NUMBER = "sos_phone";

    // 服务器端socket的地址
    public static final String SOCKET_SERVER_ADDRESS_URL = "healthdata.4000300659.com";
    // 服务器端socket的端口
    public static final int SOCKET_SERVER_ADDRESS_PORT = 12004;
    // 连接socket的服务,收到开机广播一分钟后开始连接socket
    public static final int SOCKET_SERVER_CONNECT_WAIT_DURATION = 1 * 60 * 1000;

    // 心跳服务上报的时间间隔
    public static final int BEATHEART_SERVICE_INTERVAL = 5 * 60 * 1000;
    // 定位服务上报的时间间隔
    public static final int BAIDU_GPS_SERVICE_SCAN_INTERVAL = 3 * 60 * 1000;
    // 重连服务第一次连接等待时长
    public static final int RECONNCET_SOCKET_SERVICE_INTERVAL = 1 * 30 * 1000;


    /********************************** 改为socket通信后, 这些就没用了 ************************************/
    public static final String RXJAVAHTTP_BASE_URL_TEST = "http://healthdata.4000300659.com:8088/api/xiaolajiaotest/";
    public static final String RXJAVAHTTP_BASE_URL_FORMAL = "http://healthdata.4000300659.com:8088/api/xiaolajiao/";
    public static final String RXJAVAHTTP_REPORT_SOSPOSITION = "sosPosition";
    public static final String RXJAVAHTTP_DOWNLOAD_FROM_PLATFORM = "familyNumber";
    public static final String RXJAVAHTTP_UPDATE_TO_PLATFORM = "updatePhone";
    public static final String RXJAVAHTTP_REPORT_POSITION = "position";
    public static final String RXJAVAHTTP_IMEI = "867400088889999"; //867400020316620

    public static final String RXJAVAHTTP_TYPE_DOWNLOAD = "103";
    public static final String RXJAVAHTTP_TYPE_SOS = "202";         // 是通过按SOS键上报消息
    public static final String RXJAVAHTTP_TYPE_UPLOAD = "203";
    public static final String RXJAVAHTTP_TYPE_CURRENT = "101";     // 收到服务器指令, 实时定位后上报消息
    public static final String RXJAVAHTTP_TYPE_POWERON = "100";     // 开机上报消息
    public static final String RXJAVAHTTP_TYPE_TIMING = "201";      // 定时定位后上报消息
    public static final String RXJAVAHTTP_TYPE_INTERVAL = "106";                // 代表设置定位上传频率
    public static final String RXJAVAHTTP_TYPE_OUTER_ELECTRIC_BAR = "107";      //代表电子围栏超出通知
    public static final String RXJAVAHTTP_TYPE_GET_STATUS_INFO = "108";         //代表获取设备状态信息

    public static final String RXJAVAHTTP_COMPANY = "xiaolajiao";
    public static final String RXJAVAHTTP_SECRET_CODE = "ab2796145acdf";//"8s6HiyY0yGCLeZp5";     //ab2342145acdf;    // iETOECQ9kiJw75AZ
    public static final String RXJAVAHTTP_ENCODE_TYPE = "UTF-8";

    public static final int RXJAVAHTTP_READ_TIMEOUT = 100;
    public static final int RXJAVAHTTP_WRITE_TIMEOUT = 100;
    public static final int RXJAVAHTTP_CONNCET_TIMEOUT = 100;
    /********************************** 改为socket通信后, 这些就没用了 ************************************/
}