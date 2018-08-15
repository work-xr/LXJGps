package com.hsf1002.sky.xljgps.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.hsf1002.sky.xljgps.app.GpsApplication;
import com.hsf1002.sky.xljgps.model.SocketModel;
import com.hsf1002.sky.xljgps.result.ResultServerDownloadNumberMsg;
import com.hsf1002.sky.xljgps.result.ResultServerIntervalMsg;
import com.hsf1002.sky.xljgps.result.ResultServerOuterElectricMsg;
import com.hsf1002.sky.xljgps.result.ResultServerStatusInfoMsg;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static com.hsf1002.sky.xljgps.util.Constant.RESULT_MSG_SUCCESS;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_COMMAND;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_IMEI;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_INTERVAL;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_NAME;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_NUMBER;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_TIME;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_STATUS_POWERON;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_SUCCESS_1;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_ENCODE_TYPE;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_SERVER_ADDRESS_PORT;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_SERVER_ADDRESS_URL;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_BEATHEART;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_CURRENT;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_DOWNLOAD;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_GET_STATUS_INFO;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_INTERVAL;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_OUTER_ELECTRIC_BAR;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_POWERON;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_SOS;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_TIMING;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_UPLOAD;


/**
*  author:  hefeng
*  created: 18-8-2 下午5:16
*  desc: 这是个sticky service, 始终伴随着应用(应用在收到开机广播就会启动)一起启动, 永远都不会停止
 *  但是sticky service的管理不方便, 不容易判断sticky service是否已经启动, 好在该服务无需判断状态
 *  替代架构可以用普通的service或者IntentService, 只是要每次开机重新启动
*/

public class SocketService extends Service {
    private static final String TAG = "SocketService";
    private static Context sContext;
    private static Socket sSocket = null;

    private InputStream is = null;
    private BufferedReader br = null;
    private OutputStream os = null;
    private DataInputStream dis = null;

    // 用于连接socket, 以及断开时重新连接
    private static Thread connectServerThread;
    // 用于从服务器端读取数据, 解析后再返回数据到服务器端
    private static Thread readServerThread;
    // 用于给服务器端写数据
    private static Thread writeServerThread;
    // 用于保存从其他地方传递过来的数据, 然后在线程中写到服务器端
    private static String gsonString = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");

        sContext = GpsApplication.getAppContext();
        connectServerThread = new ConnectServerThread();
        connectServerThread.start();
        readServerThread = new ReadServerThread();
        readServerThread.start();
        writeServerThread = new WriteDataThread();
        writeServerThread.start();

        //  以下调用用于调试
        //SocketModel.getInstance().reportBeatHeart();
       // writeDataToServer(null);
        //SocketModel.getInstance().reportBeatHeart();
        //parseServerMsg("{\"imei\":\"867400020316620\",\"time\":\"20180810155626\",\"command\":101}");
        //parseServerMsg("{\"interval\":\"3600\",\"command\":106,\"time\":\"20170102302022\"}");
        //parseServerMsg("{\"imei \":\"869938027477745\",\"command\":107,\"time\":\"20170102302022\"}");
        //parseServerMsg("{\"imei\":\"869938027477745\",\"command\":108,\"time\":\"20170102302022\"}");
        //parseServerMsg("{\"sos_phone\":\"10086,12345,19968867878,059212349\",\"name\":\"亲1,亲2,亲3,养老服务中心号码\",\"imei\":\"867400020316620\",\"time\":\"20180811123608\",\"command\":103}");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return null;
    }


    /**
     *  author:  hefeng
     *  created: 18-8-8 下午4:06
     *  desc:    创建单例
     *  param:
     *  return:
     */
    private static final class Holder
    {
        private static final SocketService instance = new SocketService();
    }

    public static SocketService getInstance()
    {
        return Holder.instance;
    }

    /**
    *  author:  hefeng
    *  created: 18-8-3 上午10:00
    *  desc:    返回类型表示这是sticky服务, 将长期运行, 不会停止
    *  param:
    *  return:  START_STICKY
    */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: start service...............");
        return START_STICKY;// super.onStartCommand(intent, flags, startId);
    }

    /**
    *  author:  hefeng
    *  created: 18-8-3 上午10:00
    *  desc:    重新连接socket
    *  param:
    *  return:
    */
    public void reconnectSocketServer()
    {
        if (connectServerThread != null) {
            if (!connectServerThread.isAlive()) {
                connectServerThread.run();
            }
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-13 下午7:50
    *  desc:    连接socket的线程, 连接成功后, 开启心跳服务
    *  param:
    *  return:
    */
    private class ConnectServerThread extends Thread
    {
        @Override
        public void run() {
            if (sSocket == null) {
                 Log.i(TAG, "reconnectSocketServer: start");
                try {
                    sSocket = new Socket(SOCKET_SERVER_ADDRESS_URL, SOCKET_SERVER_ADDRESS_PORT);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                finally {
                    // 开启心跳定时服务, 默认每隔5分钟上报一次心跳
                    BeatHeartService.setServiceAlarm(sContext, true);
                }
                Log.i(TAG, "reconnectSocketServer: finished");
            }
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-8 下午6:10
    *  desc:    断开Socket连接, 目前在两个地方调用: onDestroy的时候以及读取服务器数据的时候, 如果读到的前四个字节不是数字, 则调用此方法
    *  param:
    *  return:
    */
    private void disConnectSocketServer()
    {
        Log.i(TAG, "disConnectSocketServer: start*****************************************************************");

        try
        {
            if (is != null) {
                is.close();
            }
            if (dis != null) {
                dis.close();
            }
            if (os != null) {
                os.close();
            }
            if (br != null) {
                br.close();
            }
            if (sSocket != null) {
                sSocket.close();

                Log.i(TAG, "disConnectSocketServer: isConnected = " + sSocket.isConnected());
                Log.i(TAG, "disConnectSocketServer: isClosed = " + sSocket.isClosed());

                sSocket = null;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Log.i(TAG, "disConnectSocketServer: finished***************************************************************");
    }

    /**
    *  author:  hefeng
    *  created: 18-8-8 下午5:53
    *  desc:    Socket是否处于连接状态
    *  param:
    *  return:
    */
    private boolean isSocketConnected()
    {
        //Log.i(TAG, "isSocketConnected: isConnected = " + sSocket.isConnected()); // true
        //Log.i(TAG, "isSocketConnected: isBound = " + sSocket.isBound());    // false
        //Log.i(TAG, "isSocketConnected: isClosed = " + sSocket.isClosed());  // false
        return confirmSocketConnected();
    }

    /**
    *  author:  hefeng
    *  created: 18-8-11 上午11:27
    *  desc:  判断Socket是否处于连接状态, 如果处于连接状态, 则返回true, 如果处于断开状态, 则开启重连服务
    *  param:
    *  return:
    */
    private boolean confirmSocketConnected()
    {
        boolean disconnected = false;

        if (sSocket == null)
        {
            disconnected = true;
        }
        boolean serviceStarted = ReconnectSocketService.isServiceAlarmOn(sContext);

        Log.i(TAG, "confirmSocketConnected: disconnected = " + disconnected + ", serviceStarted = " + serviceStarted);

        // 如果socket连上了, 且重连服务已经开启, 则关闭
        if (!disconnected && serviceStarted) {
            ReconnectSocketService.setServiceAlarm(sContext, false);
            Log.i(TAG, "confirmSocketConnected: socket already connected, stopped reconncect service**********************************");
            return true;
        }
        // 如果socket断开了, 且重连服务没有开启, 则开启
        else if (disconnected && !serviceStarted) {
            ReconnectSocketService.setServiceAlarm(sContext, true);
            Log.i(TAG, "confirmSocketConnected: start to reconncect service***********************************************************");
        }
        // 如果socket连上了, 且重连服务已经断开或 socket断开了, 且重连服务已经开启, 不处理
        else
        {
            if (!disconnected)
            {
                Log.i(TAG, "confirmSocketConnected:  socket already connected, continue***********************************************");
            }
            else
            {
                Log.i(TAG, "confirmSocketConnected:  connected socket failed, continue************************************************");
            }
        }

        return !disconnected;
    }


    /**
    *  author:  hefeng
    *  created: 18-8-9 上午10:07
    *  desc:    获取传输给服务器的数据前4个字节, 是传输数据的长度, 比如数据长度为8个字节, 则是0008, 如果数据长度是256, 则是0256
    *  param:
    *  return:
    */
    private String getDataPrefix(String data)
    {
        int size = data.length() + 4;
        String prefix = null;

        if (size < 1000)
        {
            if (size > 100)
            {
                prefix = "0" + String.valueOf(size);
            }
            else if (size > 10)
            {
                prefix = "00" + String.valueOf(size);
            }
            else
            {
                prefix = "000" + String.valueOf(size);
            }
        }

        return prefix;
    }

    /**
    *  author:  hefeng
    *  created: 18-8-9 下午1:47
    *  desc:    对从服务器端读到的数据进行处理,先读取数据长度,再读取实际数据,最后解码转为String类型
    *  param:
    *  return:
    */
    private String getParseDataString(DataInputStream dis)
    {
        String dencodedStr = null;
        // 开始的4个字节是数据总长度
        byte[] sizeBytes = new byte[4];
        int size = 0;

        try {
            dis.read(sizeBytes);
            String sizeStr = new String(sizeBytes);
            Log.i(TAG, "getParseDataString: sizeStr = " + sizeStr);

            // 前4个字节应该都是数字,否则丢弃此次读取, 并且断开socket服务
            if (!TextUtils.isDigitsOnly(sizeStr))
            {
                Log.i(TAG, "getParseDataString: the first 4 bytes invalid, we're convinced the socket has been disconnected*************!");
                disConnectSocketServer();
                return null;
            }

            size = Integer.valueOf(sizeStr);
            Log.i(TAG, "getParseDataString: size = " + size);

            // 读取服务器发送的实际数据
            byte[] dataBytes = new byte[size - 4];
            dis.read(dataBytes);

            // 用UTF-8进行解码
            dencodedStr = new String(dataBytes);
            dencodedStr = URLDecoder.decode(dencodedStr, SOCKET_ENCODE_TYPE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Log.i(TAG, "getParseDataString: decodedStr = " + dencodedStr);

        return dencodedStr;
    }

    /**
    *  author:  hefeng
    *  created: 18-8-10 下午1:55
    *  desc:    将发送给服务器的数据先编码,再加上前4个字节
    *  param:
    *  return:
    */
    private String getCompletedString(String data)
    {
        String completedStr = null;
        String encodedData = null;

        if (data == null)
        {
            return null;
        }

        try
        {
            encodedData = URLEncoder.encode(data, SOCKET_ENCODE_TYPE);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        completedStr = getDataPrefix(encodedData) + encodedData;

        return completedStr;
    }

    /**
    *  author:  hefeng
    *  created: 18-8-13 下午7:59
    *  desc:    主动发送数据给服务器端, 然后再接收服务端返回的消息
     *  比如上报心跳报文:       {"imei":"869426020023138","company":"hemiao","type":301}
     *  从服务器读取返回内容:   {"success":1,"company":"hemiao","command":301}
    *  param:
    *  return:
    */
    public class WriteDataThread extends Thread
    {
        @Override
        public void run() {
            // 0. 先等待socket连接成功, 再进行写操作
            waitConnectThread();

            if (!isSocketConnected())
            {
                Log.i(TAG, "writeDataToServer: connected to server failed.");
            }
            else
            {
                Log.i(TAG, "writeDataToServer: connected to server successfully.");

                try {
                    // 1 将完整数据进行编码(发送的数据之前4个字节要添加数据的长度)
                    String completedStr = getCompletedString(gsonString);

                    // 2. 打开输出流, 将数据写入
                    if (!TextUtils.isEmpty(completedStr)) {
                        Log.i(TAG, "writeDataToServer: completedStr = " + completedStr);
                        os = sSocket.getOutputStream();
                        os.write(completedStr.getBytes());
                        os.flush();
                        // 不能关闭输出流
                        //sSocket.shutdownOutput();
                        Log.i(TAG, "writeDataToServer: write data to server successfully^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                    }
                    else
                    {
                        Log.i(TAG, "writeDataToServer: no data to send" );
                    }
                    // 3. 对服务器返回的数据进行读取, 不能在此处处理, 否则可能被读的线程读到数据
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-8 下午1:26
    *  desc:    其他类会通过此接口, 将开机信息, 定位信息 上传到服务器
    *  param:
    *  return:
    */
    public void writeDataToServer(final String data)
    {
        gsonString = data;

        Log.i(TAG, "writeDataToServer: prepared.............................................");

        if (writeServerThread != null ) {
            Log.i(TAG, "writeDataToServer: isAlive = " + writeServerThread.isAlive());
            Log.i(TAG, "writeDataToServer: isInterrupted = " + writeServerThread.isInterrupted());
            writeServerThread.run();
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-11 上午10:17
    *  desc:    把截取到的参数的值value字符串进行解析, 只取数字部分
    *  param:
    *  return:
    */
    private String getParamValueOnlyDigit(String value)
    {
        StringBuilder commandBuilder = new StringBuilder();
        int length = value.length();

        for(int i=0; i<length; ++i)
        {
            String oneChar = value.substring(i, i+1);

            if (TextUtils.isDigitsOnly(oneChar))
            {
                commandBuilder.append(oneChar);
            }
        }

        Log.i(TAG, "getParamValueOnlyDigit: commandBuilder.toString() = " + commandBuilder.toString());

        return commandBuilder.toString();
    }

    /**
    *  author:  hefeng
    *  created: 18-8-3 上午10:19
    *  desc:    解析从服务器接收的数据
     *  接口4、孝老平台下发修改设备亲情号码等信息    接收数据: {"imei":"869426020023138","command":103, "e_order":"老妈，老弟，老姐, 养老服务中心号码"，"sos_phone":",13555555553, 13555555553 , 13555555555，059612349","time":"20170504113555"}   发送数据: {"imei":"869426020023138","company":"hemiao","type":103,"time":"20170402120223","success":1}
     *  接口6、孝老平台随时下发指令能定位设备的位置, 接收数据: {"imei":"869426020023138","command":101, "time":"20170504113555"}   发送数据: {"imei":"869426020023138","company":"hemiao","type":"101","position_type":"gps","time":"20170102302022","loc_type": "1","lon":"117.552954","lat":"25.552954","power":"99"}
     *  接口7、孝老平台随时下发修改设备定位上传频率  接收数据: {"interval":"3600" ,"command":106 ,"time":"20170102302022"}         发送数据: {"company":"hemiao","success":1,"time":"20170102102022","type":106}
     *  接口8、通知设备用户超出电子围栏             接收数据: {"imei ":"869426020023138" ,"command":107 ,"time":"20170102302022"}  发送数据: {"imei":"869426020023138","success":1,"time":"20170102102022","type":107}
     *  接口9、获取设备状态                       接收数据: {"imei ":"869426020023138" ,"command":108 ,"time":"20170102302022"} 发送数据: {"success":1,"message":"SUCCEED"，"imei":"869938027477745"  ,"type":"108"," status ":1," power ":10 ,"time":"20170102302022"}
    *  param:
    *  return:
    */
    public String parseServerMsg(String msg) {
        String gsonStr = null;
        String[] dataList = null;
        String paramType = null;
        String paramIMEI = null;
        String paramInterval = null;
        String paramTime = null;
        String paramName = null;
        String paramNumber = null;

        // 如果服务器下发的是修改亲情号码指令, 不能以 , 分割字符串, 需要单独处理
        if (msg.contains(RESULT_PARAM_NAME) && msg.contains(RESULT_PARAM_NUMBER))
        {
            paramNumber = getOneParam(msg, 0);
            paramName= getOneParam(msg, 1 + paramNumber.length() + 4);   // {"":"",
            int commandPos = msg.indexOf(RESULT_PARAM_COMMAND);
            paramType = msg.substring(commandPos);
        }
        else {
            dataList = msg.split(",");

            for (int i = 0; i < dataList.length; ++i) {
                if (dataList[i].contains(RESULT_PARAM_COMMAND)) {
                    paramType = dataList[i];
                } else if (dataList[i].contains(RESULT_PARAM_INTERVAL)) {
                    paramInterval = dataList[i];
                } else if (dataList[i].contains(RESULT_PARAM_IMEI)) {
                    paramIMEI = dataList[i];
                } else if (dataList[i].contains(RESULT_PARAM_TIME)) {
                    paramTime = dataList[i];
                }
            }
        }

        if (paramType == null)
        {
            Log.i(TAG, "parseServerMsg: no command");
            return null;
        }

        String[] paramList = paramType.split(":");
        String commandStr = paramList[1];
        int command = Integer.valueOf(getParamValueOnlyDigit(commandStr));

        switch (command)
        {
            // 收到平台指令: 从平台下载,再设置本地平台中心号码和亲情号码
            case SOCKET_TYPE_DOWNLOAD:
                if (paramName == null)
                {
                    Log.i(TAG, "parseServerMsg: paramName is null");
                    return null;
                }
                if (paramNumber == null)
                {
                    Log.i(TAG, "parseServerMsg: paramNumber is null");
                    return null;
                }

                paramList = paramName.split(":");
                String name = paramList[1].substring(0, paramList[1].length());
                paramList = paramNumber.split(":");
                String number = paramList[1].substring(0, paramList[1].length());

                Log.i(TAG, "parseServerMsg: name = " + name  + ", number = " + number);

                SprdCommonUtils.getInstance().setRelationNumber(number);

                ResultServerDownloadNumberMsg downloadNumberMsg = new ResultServerDownloadNumberMsg();
                downloadNumberMsg.setSuccess(RESULT_SUCCESS_1);

                gsonStr = ResultServerDownloadNumberMsg.getResultServerDownloadNumberMsgGson(downloadNumberMsg);
                break;
            // 收到平台指令: 上传位置信息
            case SOCKET_TYPE_CURRENT:
                SocketModel.getInstance().reportPosition(SOCKET_TYPE_CURRENT, null);
                break;
            // 收到平台指令: 设置上报定位信息的频率
            case SOCKET_TYPE_INTERVAL:
                if (paramInterval == null)
                {
                    Log.i(TAG, "parseServerMsg: paramInterval is null");
                    return null;
                }
                paramList = paramInterval.split(":");
                Log.i(TAG, "parseServerMsg: paramInterval = " + paramInterval);
                int interval = Integer.valueOf(getParamValueOnlyDigit(paramList[1]));
                Log.i(TAG, "parseServerMsg: interval = " + interval);
                GpsService.setStartServiceInterval(interval);

                ResultServerIntervalMsg intervalMsg = new ResultServerIntervalMsg();
                intervalMsg.setSuccess(RESULT_SUCCESS_1);

                gsonStr = ResultServerIntervalMsg.getResultServerIntervalMsgGson(intervalMsg);
                break;
            // 收到平台指令: 超出电子围栏, 给亲情号码发送短信通知
            case SOCKET_TYPE_OUTER_ELECTRIC_BAR:
                SprdCommonUtils.getInstance().sendSosSms();

                ResultServerOuterElectricMsg outerElectricMsg = new ResultServerOuterElectricMsg();
                outerElectricMsg.setSuccess(RESULT_SUCCESS_1);

                gsonStr = ResultServerOuterElectricMsg.getResultServerOuterElectricMsgGson(outerElectricMsg);
                break;
            // 收到平台指令: 获取设备状态信息, 再返回给服务器
            case SOCKET_TYPE_GET_STATUS_INFO:
                ResultServerStatusInfoMsg statusInfoMsg = new ResultServerStatusInfoMsg();
                statusInfoMsg.setMessage(RESULT_MSG_SUCCESS);
                statusInfoMsg.setStatus(RESULT_STATUS_POWERON);
                statusInfoMsg.setSuccess(RESULT_SUCCESS_1);

                gsonStr = ResultServerStatusInfoMsg.getResultServerStatusInfoMsgGson(statusInfoMsg);
                break;
            // 这几条指令, 是本地主动向服务器端发送数据, 只接收服务器端的返回值即可, 无需向服务器端传输任何数据
            case SOCKET_TYPE_BEATHEART:
            case SOCKET_TYPE_UPLOAD:
            case SOCKET_TYPE_SOS:
            case SOCKET_TYPE_POWERON:
            case SOCKET_TYPE_TIMING:
                break;
            default:
                break;
        }

        Log.i(TAG, "parseServerMsg: send to server gsonStr = " + gsonStr);

        return gsonStr;
    }

    /**
    *  author:  hefeng
    *  created: 18-8-13 下午2:46
    *  desc:    先等待socket连接成功, 再进行读写操作
    *  param:
    *  return:
    */
    public void waitConnectThread()
    {
        try {
            connectServerThread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     *  author:  hefeng
     *  created: 18-8-8 下午1:26
     *  desc:    接收从服务器端下发的指令或者从服务器返回的数据
     *  param:
     *  return:
     */
    private class ReadServerThread extends Thread
    {
        @Override
        public void run(){
            // 0. 先等待socket连接成功, 再进行读写操作
            waitConnectThread();

            while (true)
            {
                if (isSocketConnected()) {
                    try {
                        // 1. 打开输入流, 从服务器端接收数据
                        Log.i(TAG, "readDataFromServer: waiting for server send data.....................blocked");
                        is = sSocket.getInputStream();
                        dis = new DataInputStream(is);
                        //is.close();
                        //dis.close();
                        // 2. 对服务器返回的数据进行处理
                        String decodedStr = getParseDataString(dis);

                        if (!TextUtils.isEmpty(decodedStr)) {

                            Log.i(TAG, "readDataFromServer: decodedStr = " + decodedStr);
                            // 3. 解析数据, 并将需要发送给服务器的数据返回
                            String gsonStr = parseServerMsg(decodedStr);
                            String completedStr = getCompletedString(gsonStr);

                            // 如果为空,表示此次读取数据的上一次操作是客户端主动发送消息到服务器, 而服务器没有返回任何数据
                            // (本来是有返回值的, 而且返回了客户端发送的command, 客户端收到数据后进行解析, 由于是根据command判断并做出响应,
                            // 又发送了一次数据给服务器端, 造成了死循环, 因此客户端要求服务器不要发送返回状态)
                            if (completedStr == null)
                            {
                                Log.i(TAG, "readDataFromServer: completedStr == null, no need to send data to server");
                                continue;
                            }
                            else {
                                Log.i(TAG, "readDataFromServer: completedStr = " + completedStr + ", start to write");
                            }

                            // 4. 打开输出流, 将数据写入
                            os = sSocket.getOutputStream();
                            os.write(completedStr.getBytes());
                            os.flush();
                            //sSocket.shutdownOutput();

                            Log.i(TAG, "readDataFromServer: write data to server successfully^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                            //os.close();
                        }
                        else
                        {
                            Log.i(TAG, "readDataFromServer: read from server empty!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Log.i(TAG, "readDataFromServer: connected to server failed.");
                }
            }
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-11 下午12:55
    *  desc:    将一个JSON字符串进行解析, 拼接成key,value形式返回, 不支持嵌套
    *  param:
    *  return:
    */
    private String getOneParam(String params, int position)
    {
        String param = null;
        int startPos = 0;
        int endPos = 0;

        //Log.i(TAG, "getOneParam: position = " + position);

        startPos = params.indexOf("\"", position);
        endPos = params.indexOf("\"", startPos + 1);
        endPos = params.indexOf("\"", endPos + 1 );
        endPos = params.indexOf("\"", endPos + 1);

        //Log.i(TAG, "getOneParam: startPos = " + startPos + ", endPos = " + endPos);
        try {
            param = params.substring(startPos, endPos + 1);
        }
        catch (StringIndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }

        int keyStartPos = params.indexOf("\"", position);
        int keyEndPos = params.indexOf("\"", keyStartPos + 1);
        int valueStartPos = params.indexOf("\"", keyEndPos + 1);
        int valueEndPos = params.indexOf("\"", valueStartPos + 1);
        String key = params.substring(keyStartPos + 1, keyEndPos);
        String value = params.substring(valueStartPos + 1, valueEndPos);

        //Log.e(TAG, "getOneParam: keyStartPos = " + keyStartPos + ", keyEndPos = " + keyEndPos + ", valueStartPos = " + valueStartPos + ", valueEndPos = " + valueEndPos);
        //HashMap<String, String> hashMap = new HashMap<String, String>();
        //hashMap.put(key, value);
        //Log.i(TAG, "getOneParam: key = " + key + ":value = " + value);

        return key + ":" + value;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "onLowMemory: .........................");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i(TAG, "onTrimMemory: ........................");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ...........................");

        disConnectSocketServer();
    }
}

