package com.hsf1002.sky.xljgps.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.hsf1002.sky.xljgps.model.RxjavaHttpModel;
import com.hsf1002.sky.xljgps.params.BeatHeartParam;
import com.hsf1002.sky.xljgps.result.ResultMsg;
import com.hsf1002.sky.xljgps.result.StatusInfoSendMsg;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.hsf1002.sky.xljgps.util.Constant.RESULT_MSG_SUCCESS;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_IMEI;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_INTERVAL;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_TIME;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_PARAM_TYPE;
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
    private ExecutorService threadPool;
    private static Socket socket = null;

    private InputStream is = null;
    private StringBuilder sb = new StringBuilder();
    private BufferedReader br = null;

    private OutputStream os = null;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");

        threadPool = Executors.newCachedThreadPool();


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
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectServer();
            }
        }).start();

        BeatHeartParam beatHeartParam = new BeatHeartParam(RXJAVAHTTP_IMEI, RXJAVAHTTP_COMPANY, RXJAVAHTTP_TYPE_BEATHEART);
        String gson = BeatHeartParam.getBeatHeartParamGson(beatHeartParam);
        Log.i(TAG, "onStartCommand: gson = " + gson);

        writeDataToServer(gson);

        return START_STICKY;// super.onStartCommand(intent, flags, startId);
    }

    /**
    *  author:  hefeng
    *  created: 18-8-3 上午10:00
    *  desc:    基于TCP协议连接远程服务器的socket
    *  param:
    *  return:
    */
    private void connectServer()
    {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(SOCKET_SERVER_ADDRESS_URL, SOCKET_SERVER_ADDRESS_PORT);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    return;
                }
            }
        });
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
        return socket.isConnected();
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
                if (isSocketConnected()) {
                    Log.i(TAG, "readDataFromServer: connected to server successfully.");

                    try {
                        is = socket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (is != null)
                    {
                        InputStreamReader isr = new InputStreamReader(is);
                        br = new BufferedReader(isr);

                        Log.i(TAG, "readDataFromServer: isr = " + isr);
                        Log.i(TAG, "readDataFromServer: br = " + br);

                        try {
                            String line;

                            if (br != null && br.readLine() != null)
                            {
                                while ((line = br.readLine()) != null) {
                                    Log.i(TAG, "readDataFromServer: read line = " + line);
                                    sb.append(line);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Log.i(TAG, "readDataFromServer: read sb = " + sb.toString());

                        if (!TextUtils.isEmpty(sb)) {
                            parseServerMsg(sb.toString());
                        }
/*
                    try {
                        br.close();
                        //socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    }
                }
            }
        });
    }

    /**
    *  author:  hefeng
    *  created: 18-8-8 下午1:26
    *  desc:    发送数据给服务器端
    *  param:
    *  return:
    */
    public void writeDataToServer(final String data)
    {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                String completedStr = data;
                String encodedData = null;

                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                completedStr = data.length() + data;

                Log.i(TAG, "writeDataToServer: completedStr = " + completedStr);

                try
                {
                    encodedData = URLEncoder.encode(completedStr, RXJAVAHTTP_ENCODE_TYPE);
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }

                if (!isSocketConnected())
                {
                    Log.i(TAG, "writeDataToServer: connected to server failed.");
                }
                else
                {
                    Log.i(TAG, "writeDataToServer: connected to server successfully.");

                    //String gsonStr = parseServerMsg(null);

                    try
                    {
                        os = socket.getOutputStream();
                        // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
                        os.write((encodedData + "\n").getBytes(RXJAVAHTTP_ENCODE_TYPE));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    try {
                        os.flush();
                        //socket.shutdownOutput();
                        Log.i(TAG, "writeDataToServer: write data to server successfully.");
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
    *  author:  hefeng
    *  created: 18-8-3 上午10:19
    *  desc:    解析从服务器接收的数据
     *  接口4、孝老平台下发修改设备亲情号码等信息 发送数据: {"imei":"869426020023138","company":"IJIA","type":"203","time":"20170102102022" ,"sos_phone":"10010，10086，1008611，059612349,","name":"亲情号码1，亲情号码2，亲情号码3，养老服务中心号码"}
     *  接口6、孝老平台随时下发查询指令后，能随时定位到设备的位置, 还是用HTTP上报                                                     发送数据: {"success": 1,"message": "SUCCEED"} or { "success ": 0, " message": "failing"}
     *  接口7、孝老平台随时下发修改设备定位上传频率 接收数据: {" interval":"3600" ,"type":"106" ,"time":"20170102302022"}            发送数据: {"success": 1,"message": "SUCCEED"} or { "success ": 0, " message": "failing"}
     *  接口8、通知设备用户超出电子围栏            接收数据: {" imei ":"869938027477745"  ,"type":"107" ,"time":"20170102302022"}  发送数据: {"success": 1,"message": "SUCCEED"} or { "success ": 0, " message": "failing"}
     *  接口9、获取设备状态                      接收数据: {" imei ":"869938027477745"  ,"type":"108" ,"time":"20170102302022"}   发送数据: {"success": 1,"message": "SUCCEED"} or { "success ": 0, " message": "failing"}
     *                                         发送数据: {"success":1,"message":"SUCCEED"，"imei ":"869938027477745"  ,"type":"107" ,"time":"20170102302022"}
     *                                               or {"success":0, "message":"failing"，"imei ":"869938027477745"  ,"type":"107" ,"time":"20170102302022"}
    *  param:
    *  return:
    */
    public String parseServerMsg(String msg)
    {
        StatusInfoSendMsg statusInfoSendMsg = new StatusInfoSendMsg();
        ResultMsg<StatusInfoSendMsg> resultMsg = new ResultMsg();
        String encodedMsg = null;
        String[] dataList = null;
        String paramType = null;
        String paramIMEI = null;
        String paramInterval = null;
        String paramTime = null;

        // 首先对接收到的数据解码
        try {
            encodedMsg = URLDecoder.decode(msg, RXJAVAHTTP_ENCODE_TYPE);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        // 再根据secret_code验证sign是否一致
        // 解析数据
        //dataList = encodedMsg.split(",");
        dataList = msg.split(",");

        for (int i=0; i<dataList.length; ++i)
        {
            if (dataList[i].contains(RESULT_PARAM_TYPE))
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
        }

        int type = Integer.valueOf(paramType);

        switch (type)
        {
            case RXJAVAHTTP_TYPE_INTERVAL:
                break;
            case RXJAVAHTTP_TYPE_OUTER_ELECTRIC_BAR:
                break;
            case RXJAVAHTTP_TYPE_BEATHEART:

                break;
        }
/*
        // 设置定时服务的时间间隔
        if (paramType.contains(RXJAVAHTTP_TYPE_INTERVAL))
        {
            GpsService.setStartServiceInterval(0);
            resultMsg.setSuccess(RESULT_SUCCESS_1);
            resultMsg.setMessage(RESULT_MSG_SUCCESS);
        }
        // 超出电子围栏, 给亲情号码发送短信通知
        else if (paramType.contains(RXJAVAHTTP_TYPE_OUTER_ELECTRIC_BAR))
        {
            SprdCommonUtils.getInstance().sendSosSms();
            resultMsg.setSuccess(RESULT_SUCCESS_1);
            resultMsg.setMessage(RESULT_MSG_SUCCESS);
        }
        // 获取设备状态信息, 再返回给服务器
        else if (paramType.contains(RXJAVAHTTP_TYPE_GET_STATUS_INFO))
        {
            statusInfoSendMsg.setImei(SprdCommonUtils.getInstance().getIMEI());
            statusInfoSendMsg.setPower(SprdCommonUtils.getInstance().getCurrentBatteryCapacity());
            statusInfoSendMsg.setStatus(RESULT_STATUS_POWERON);
            statusInfoSendMsg.setTime(SprdCommonUtils.getInstance().getFormatCurrentTime());

            resultMsg.setData(statusInfoSendMsg);
        }
        // 用HTTP上传位置信息
        else if (paramType.equals(RXJAVAHTTP_TYPE_CURRENT))
        {
            RxjavaHttpModel.getInstance().reportPosition(RXJAVAHTTP_TYPE_CURRENT, null);
            resultMsg.setSuccess(RESULT_SUCCESS_1);
            resultMsg.setMessage(RESULT_MSG_SUCCESS);
        }
        // 从平台下载,再设置本地平台中心号码和亲情号码
        else if (paramType.equals(RXJAVAHTTP_TYPE_DOWNLOAD))
        {
            RxjavaHttpModel.getInstance().downloadRelationNumber(null);
            resultMsg.setSuccess(RESULT_SUCCESS_1);
            resultMsg.setMessage(RESULT_MSG_SUCCESS);
        }
*/
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

