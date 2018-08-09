package com.hsf1002.sky.xljgps.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.hsf1002.sky.xljgps.model.SocketModel;
import com.hsf1002.sky.xljgps.params.BeatHeartParam;
import com.hsf1002.sky.xljgps.result.ResultMsg;
import com.hsf1002.sky.xljgps.result.ResultServerStatusInfoMsg;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.hsf1002.sky.xljgps.util.Constant.RESULT_MSG_SUCCESS;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_COMMAND;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_IMEI;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_INTERVAL;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_NAME;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_NUMBER;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_TIME;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_STATUS_POWERON;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_SUCCESS_1;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_COMPANY;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_ENCODE_TYPE;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_IMEI;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_BEATHEART;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_CURRENT;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_DOWNLOAD;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_GET_STATUS_INFO;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_INTERVAL;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_OUTER_ELECTRIC_BAR;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_SERVER_ADDRESS_PORT;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_SERVER_ADDRESS_URL;


/**
*  author:  hefeng
*  created: 18-8-2 下午5:16
*  desc: 这是个sticky service, 始终伴随着应用(应用在收到开机广播就会启动)一起启动, 永远都不会停止
 *  但是sticky service的管理不方便, 不容易判断sticky service是否已经启动, 好在该服务无需判断状态
 *  替代架构可以用普通的service或者IntentService, 只是要每次开机重新启动
*/

public class SocketService extends Service {

    private static final String TAG = "SocketService";
    private ThreadPoolExecutor threadPool;
    private static Socket socket = null;

    private InputStream is = null;
    private StringBuilder sb = new StringBuilder();
    private BufferedReader br = null;
    private PrintWriter pw = null;
    private OutputStream os = null;
    private DataInputStream dis = null;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");

        // ....
        threadPool = new ThreadPoolExecutor(
                2,
                5,
                60,
                 TimeUnit.SECONDS,
                 new LinkedBlockingDeque<Runnable>(),
                 new ThreadPoolExecutor.AbortPolicy());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return null;
    }

    private static final class Holder
    {
        private static final SocketService instance = new SocketService();
    }

    /**
    *  author:  hefeng
    *  created: 18-8-8 下午4:06
    *  desc:    获取单例
    *  param:
    *  return:
    */
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

        // 这个方法运行在主线程,必须重新开启子线程
        connectSocketServer();
        readDataFromServer();

        BeatHeartParam beatHeartParam = new BeatHeartParam(RXJAVAHTTP_IMEI, RXJAVAHTTP_COMPANY, RXJAVAHTTP_TYPE_BEATHEART);
        String gson = BeatHeartParam.getBeatHeartParamGson(beatHeartParam);
        Log.i(TAG, "onStartCommand: gson = " + gson);

        //writeDataToServer(gson);

        return START_STICKY;// super.onStartCommand(intent, flags, startId);
    }

    /**
    *  author:  hefeng
    *  created: 18-8-3 上午10:00
    *  desc:    基于TCP协议连接远程服务器的socket
    *  param:
    *  return:
    */
    private void connectSocketServer()
    {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "connectSocketServer: start");
                try {
                    socket = new Socket(SOCKET_SERVER_ADDRESS_URL, SOCKET_SERVER_ADDRESS_PORT);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    return;
                }
                Log.i(TAG, "connectSocketServer: finished");
            }
        });
    }

    /**
    *  author:  hefeng
    *  created: 18-8-8 下午6:10
    *  desc:    断开socket连接
    *  param:
    *  return:
    */
    private void disConnectSocketServer()
    {
        Log.i(TAG, "disConnectSocketServer: start");

        try
        {
            os.close();
            br.close();
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Log.i(TAG, "disConnectSocketServer: finished");
    }

    /**
    *  author:  hefeng
    *  created: 18-8-8 下午5:53
    *  desc:    socket是否处于连接状态
    *  param:
    *  return:
    */
    private boolean isSocketConnected()
    {
        if (socket == null) {
            return false;
        }
        else {
            return socket.isConnected();
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-8 下午1:26
    *  desc:    接收从服务器端下发的指令或者从服务器返回的数据
    *  param:
    *  return:
    */
    public void readDataFromServer()
    {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                while (true)
                {
                    if (isSocketConnected()) {
                        Log.i(TAG, "readDataFromServer: connected to server successfully.");

                        try {
                            // 1. 打开输入流, 从服务器端接收数据
                            is = socket.getInputStream();
                            dis = new DataInputStream(is);
                            Log.d(TAG, "readDataFromServer: is = " + is);
                            Log.i(TAG, "readDataFromServer: dis = " + dis);

                            // 对服务器返回的数据进行处理
                            String dencodedStr = getParseDataString(dis);
                            is.close();
                            dis.close();

                            if (!TextUtils.isEmpty(dencodedStr)) {

                                Log.d(TAG, "readDataFromServer: dencodedStr = " + dencodedStr);
                                // 2. 解析数据, 并将需要发送给服务器的数据返回
                                String gsonStr = parseServerMsg(dencodedStr);
                                String encodedData = null;
                                // 1 先将完整数据进行编码(发送的数据之前4个字节要添加数据的长度)
                                try
                                {
                                    encodedData = URLEncoder.encode(gsonStr, RXJAVAHTTP_ENCODE_TYPE);
                                }
                                catch (UnsupportedEncodingException e)
                                {
                                    e.printStackTrace();
                                }
                                String completedStr = getDataPrefix(encodedData) + encodedData;
                                Log.i(TAG, "readDataFromServer: completedStr = " + completedStr);

                                // 3. 打开输出流, 将数据写入
                                os = socket.getOutputStream();
                                // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
                                os.write(completedStr.getBytes());
                                os.flush();
                                //socket.shutdownOutput();

                                Log.i(TAG, "readDataFromServer: write data to server successfully.");
                                os.close();
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
        });
    }

    /**
    *  author:  hefeng
    *  created: 18-8-9 上午10:07
    *  desc:    获取传输给服务器的数据前4个字节
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
            Log.d(TAG, "getParseDataString: sizeStr = " + sizeStr);
            String decodedSizeStr = URLDecoder.decode(sizeStr, RXJAVAHTTP_ENCODE_TYPE);
            Log.d(TAG, "getParseDataString: decodedSizeStr = " + decodedSizeStr);
            size = Integer.valueOf(sizeStr);
            Log.d(TAG, "getParseDataString: size = " + size);
            byte[] dataBytes = new byte[size - 4];
            dis.read(dataBytes);

            dencodedStr = new String(dataBytes);
            dencodedStr = URLDecoder.decode(dencodedStr, RXJAVAHTTP_ENCODE_TYPE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Log.d(TAG, "getParseDataString: dncodedStr" + dencodedStr);

        return dencodedStr;
    }

    /**
    *  author:  hefeng
    *  created: 18-8-8 下午1:26
    *  desc:    主动发送数据给服务器端, 然后再接收服务端返回的消息
     *  比如上报心跳报文: {"imei":"869426020023138","company":"hemiao","type":301} 之后, 从服务器读取返回内容: {"success":1,"company":"hemiao","command":301}
    *  param:
    *  return:
    */
    public void writeDataToServer(final String data)
    {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                String completedStr = null;
                String encodedData = null;

                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                // 1 先将完整数据进行编码(发送的数据之前4个字节要添加数据的长度)
                try
                {
                    encodedData = URLEncoder.encode(data, RXJAVAHTTP_ENCODE_TYPE);
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }

                completedStr = getDataPrefix(encodedData) + encodedData;

                Log.i(TAG, "writeDataToServer: completedStr = " + completedStr);

                if (!isSocketConnected())
                {
                    Log.i(TAG, "writeDataToServer: connected to server failed.");
                }
                else
                {
                    Log.i(TAG, "writeDataToServer: connected to server successfully.");
                    try
                    {
                        // 2. 打开输出流, 将数据写入
                        os = socket.getOutputStream();
                        os.write(completedStr.getBytes());
                        os.flush();
                        //socket.shutdownOutput();
                        Log.i(TAG, "writeDataToServer: write data to server successfully.");

                        // 3. 打开输入流, 接收从服务器端返回的数据
                        is = socket.getInputStream();
                        dis = new DataInputStream(is);

                        // 对服务器返回的数据进行处理
                        String encodedStr = getParseDataString(dis);

                        if (!TextUtils.isEmpty(encodedStr)) {

                            Log.d(TAG, "writeDataToServer: encodedStr = " + encodedStr);
                        }
                        else
                        {
                            Log.i(TAG, "writeDataToServer: read from server empty!");
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            dis.close();
                            os.close();
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
    *  author:  hefeng
    *  created: 18-8-3 上午10:19
    *  desc:    解析从服务器接收的数据
     *  接口4、孝老平台下发修改设备亲情号码等信息    接收数据: {"imei":"869426020023138","command":103, "e_order":"老妈，老弟，老姐, 养老服务中心号码"，"sos_phone":",13555555553, 13555555553 , 13555555555，059612349","time":"20170504113555"}   发送数据: {"imei":"869426020023138","company":"hemiao","type":103,"time":"20170402120223","success":1}
     *  接口6、孝老平台随时下发指令能定位设备的位置, 接收数据: {"imei":"869426020023138","command":101, "time":"20170504113555"}   发送数据: {"imei":"869426020023138","company":"hemiao","type":"101","position_type":"gps","time":"20170102302022","loc_type": "1","lon":"117.552954","lat":"25.552954","power":"99"}
     *  接口7、孝老平台随时下发修改设备定位上传频率  接收数据: {"interval":"3600" ,"command":106 ,"time":"20170102302022"}         发送数据: {"company":"hemiao","success":1,"time":"20170102102022","type":106}
     *  接口8、通知设备用户超出电子围栏            接收数据: {"imei ":"869426020023138" ,"command":107 ,"time":"20170102302022"}  发送数据: {"imei":"869426020023138","success":1,"time":"20170102102022","type":107}
     *  接口9、获取设备状态                       接收数据: {"imei ":"869426020023138" ,"command":108 ,"time":"20170102302022"} 发送数据: {"success":1,"message":"SUCCEED"，"imei":"869938027477745"  ,"type":"108"," status ":1," power ":10 ,"time":"20170102302022"}
    *  param:
    *  return:
    */
    public String parseServerMsg(String msg)
    {
        ResultServerStatusInfoMsg statusInfoSendMsg = new ResultServerStatusInfoMsg();
        ResultMsg<ResultServerStatusInfoMsg> resultMsg = new ResultMsg();
        String[] dataList = null;
        String paramType = null;
        String paramIMEI = null;
        String paramInterval = null;
        String paramTime = null;
        String paramName = null;
        String paramNumber = null;

        dataList = msg.split(",");

        for (int i=0; i<dataList.length; ++i)
        {
            if (dataList[i].contains(RESULT_PARAM_COMMAND))
            {
                paramType = dataList[i];
                Log.i(TAG, "parseServerMsg: param = " + paramType);
            }
            else if (dataList[i].contains(RESULT_PARAM_INTERVAL))
            {
                paramInterval = dataList[i];
            }
            else if (dataList[i].contains(RESULT_PARAM_IMEI))
            {
                paramIMEI = dataList[i];
            }
            else if (dataList[i].contains(RESULT_PARAM_TIME))
            {
                paramTime = dataList[i];
            }
            else if (dataList[i].contains(RESULT_PARAM_NAME))
            {
                paramName = dataList[i];
            }
            else if (dataList[i].contains(RESULT_PARAM_NUMBER))
            {
                paramNumber = dataList[i];
            }
        }

        if (paramType == null)
        {
            Log.d(TAG, "parseServerMsg: no command");
            return null;
        }

        String[] paramList = paramType.split(":");

        int command = Integer.valueOf(paramList[1]);

        switch (command)
        {
            // 从平台下载,再设置本地平台中心号码和亲情号码
            case RXJAVAHTTP_TYPE_DOWNLOAD:
                if (paramName == null)
                {
                    Log.d(TAG, "parseServerMsg: paramName is null");
                    return null;
                }
                if (paramNumber == null)
                {
                    Log.d(TAG, "parseServerMsg: paramNumber is null");
                    return null;
                }

                paramList = paramName.split(":");
                String name = paramList[1].substring(1, paramList[1].length()-1);
                paramList = paramNumber.split(":");
                String number = paramList[1].substring(1, paramList[1].length()-1);

                SprdCommonUtils.getInstance().setRelationNumber(number);

                resultMsg.setSuccess(RESULT_SUCCESS_1);
                resultMsg.setMessage(RESULT_MSG_SUCCESS);
                break;
            // 上传位置信息
            case RXJAVAHTTP_TYPE_CURRENT:
                SocketModel.getInstance().reportPosition(RXJAVAHTTP_TYPE_CURRENT, null);
                resultMsg.setSuccess(RESULT_SUCCESS_1);
                resultMsg.setMessage(RESULT_MSG_SUCCESS);
                break;
            // 设置上报定位信息的频率
            case RXJAVAHTTP_TYPE_INTERVAL:
                if (paramInterval == null)
                {
                    Log.d(TAG, "parseServerMsg: paramInterval is null");
                    return null;
                }
                paramList = paramInterval.split(":");
                int interval = Integer.valueOf(paramList[1].substring(1, paramList[1].length()-1));
                GpsService.setStartServiceInterval(interval);
                resultMsg.setSuccess(RESULT_SUCCESS_1);
                resultMsg.setMessage(RESULT_MSG_SUCCESS);
                break;
            // 超出电子围栏, 给亲情号码发送短信通知
            case RXJAVAHTTP_TYPE_OUTER_ELECTRIC_BAR:
                SprdCommonUtils.getInstance().sendSosSms();
                resultMsg.setSuccess(RESULT_SUCCESS_1);
                resultMsg.setMessage(RESULT_MSG_SUCCESS);
                break;
            // 获取设备状态信息, 再返回给服务器
            case RXJAVAHTTP_TYPE_GET_STATUS_INFO:
                statusInfoSendMsg.setImei(SprdCommonUtils.getInstance().getIMEI());
                statusInfoSendMsg.setPower(SprdCommonUtils.getInstance().getCurrentBatteryCapacity());
                statusInfoSendMsg.setStatus(RESULT_STATUS_POWERON);
                statusInfoSendMsg.setTime(SprdCommonUtils.getInstance().getFormatCurrentTime());
                resultMsg.setData(statusInfoSendMsg);
                break;
        }

        String gsonStr = ResultMsg.getResultMsgGson(resultMsg);
        Log.i(TAG, "parseServerMsg: resultMsg = " + resultMsg);
        Log.i(TAG, "parseServerMsg: gsonStr = " + gsonStr);

        return gsonStr;
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
    }
}

