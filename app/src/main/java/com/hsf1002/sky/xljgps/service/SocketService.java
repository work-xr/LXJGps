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
import com.hsf1002.sky.xljgps.util.NetworkUtils;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_SERVER_CONNECT_WAIT_DURATION;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_SERVER_TIMEOUT;
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
*
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
    private static Thread sConnectServerThread;
    // 用于从服务器端读取数据, 解析后再返回数据到服务器端
    private static Thread sReadServerThread;
    // 用于给服务器端写数据
    private static Thread sWriteServerThread;
    // 用于保存从其他地方传递过来的数据, 然后写到服务器端
    private static String sGsonString = null;
    // 用于标记这是个重连服务(网络正常情况下出现的各种异常的时候才会启动重连服务), 而不是开机第一次连接或断开后的重连
    private static boolean sReconnectFlag = false;
    // 如果连接断开了, 在写数据前加锁, 连接成功后解锁,保证当连接重新连上后定时数据还可以上传
    //private Object lockWaitConntected = new Object();
    // 用于标记读的线程是否在运行, 置为false即停止, 待socket重新连接上之后再重新运行
    private volatile boolean isRunning = true;
    // 读的线程是否进入了wait阻塞状态
    private static boolean sIsReadThreadWaited = false;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        sContext = GpsApplication.getAppContext();
        startThreads();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return null;
    }

    /**
    *  author:  hefeng
    *  created: 18-8-27 下午7:53
    *  desc:    该服务调用onCreate的时候调用此函数, 不会停止运行
    *  param:
    *  return:
    */
    private void startThreads()
    {
        if (sConnectServerThread == null) {
            sConnectServerThread = new ConnectServerThread();
            sConnectServerThread.start();
        }

        if (sReadServerThread == null) {
            sReadServerThread = new ReadServerThread();
            sReadServerThread.start();
        }

        if (sWriteServerThread == null) {
            sWriteServerThread = new WriteDataThread();
            sWriteServerThread.start();
        }
    }

    /**
     *  author:  hefeng
     *  created: 18-8-27 下午7:53
     *  desc:   断开网络重新连接的时候, 重新运行三个线程
     *          由于 onStartCommand 的调用是混乱的, 有时候不调用, 有时候连续调用多次
     *  param:
     *  return:
     */
    private void runThreads()
    {
        Thread runThread = new RunThread();
        runThread.start();
    }

    private class RunThread extends Thread
    {
        @Override
        public void run() {
            if (sConnectServerThread != null) {
                if (!sConnectServerThread.isAlive()) {
                    sConnectServerThread.run();
                }
            }

            if (sReadServerThread != null) {
                if (!sReadServerThread.isAlive()) {
                    sReadServerThread.run();
                }
            }

            if (sWriteServerThread != null) {
                if (!sWriteServerThread.isAlive()) {
                    sWriteServerThread.run();
                }
            }
        }
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
        runThreads();
        return START_STICKY;
    }

    /**
    *  author:  hefeng
    *  created: 18-8-3 上午10:00
    *  desc:    重新连接socket, 供重连服务ReconnectSocketService调用 
    *  param:
    *  return:
    */
    public void reconnectSocketServer()
    {
        sReconnectFlag = true;
        Log.i(TAG, "reconnectSocketServer: start............................................");
        if (sConnectServerThread != null) {
            if (!sConnectServerThread.isAlive()) {
                sConnectServerThread.run();
            }
        }
        Log.i(TAG, "reconnectSocketServer: end..............................................");
        sReconnectFlag = false;
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
            Log.i(TAG, "ConnectServerThread: start******************************************");
            if (sSocket == null) {
                if (!sReconnectFlag)
                {
                    Log.i(TAG, "ConnectServerThread: start first enter, do not sleep*****************");
                    // 如果是网络断开后的连接, 加点延迟10s, 如果是服务器端断开重新连接, 直接连
                    /*try {
                        Thread.sleep(SOCKET_SERVER_CONNECT_WAIT_DURATION);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        return;
                    }*/
                }
                else
                {
                    Log.i(TAG, "ConnectServerThread reconnect start*************************");
                }
                try {
                    sSocket = new Socket(SOCKET_SERVER_ADDRESS_URL, SOCKET_SERVER_ADDRESS_PORT);
                    sSocket.setKeepAlive(true);
                    //sSocket.setSoTimeout(SOCKET_SERVER_TIMEOUT);
                }
                catch (IOException e) {
                    handleException(e, "ConnectServerThread: ");
                    return;
                }
                finally {
                    if (sSocket != null && sSocket.isConnected()) {
                        Log.i(TAG, "ConnectServerThread: success^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                        // 让读的线程开始运行
                        Log.i(TAG, "ConnectServerThread ReadThread isRunning = " + isRunning);
                        if (!isRunning) {
                            isRunning = true;
                            if (!sReadServerThread.isAlive()) {
                                sReadServerThread.run();
                            }
                        }

                        // 如果没有开启心跳定时服务, 开启
                        if (!BeatHeartService.isServiceAlarmOn(sContext)) {
                            BeatHeartService.setServiceAlarm(sContext, true);
                        }

                        // 如果没有开启定位定时服务, 开启
                        if (!GpsService.isServiceAlarmOn(sContext)) {
                            GpsService.setServiceAlarm(sContext, true);
                        }

                        // 如果开启了重连定时服务, 关闭
                        if (ReconnectSocketService.isServiceAlarmOn(sContext)) {
                            ReconnectSocketService.setServiceAlarm(sContext, false);
                        }

                        if (sIsReadThreadWaited) {
                            Log.i(TAG, "ConnectServerThread: lockReadWaitConntected notify*************");
                            // 如果上报信息如心跳的时候,socket断开了,就阻塞在那里, 在这里激活
                            /*synchronized (lockReadWaitConntected) {
                                sIsReadThreadWaited = false;
                                lockReadWaitConntected.notify();
                            }*/
                        }
                    }
                    else
                    {
                        Log.i(TAG, "ConnectServerThread: failed, ready to connect again*************");
                        startReconnect("ConnectServerThread:");
                    }
                }
            }
            Log.i(TAG, "ConnectServerThread: stopped***************************************");
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-8 下午6:10
    *  desc:    断开Socket连接, 目前在两个地方调用: onDestroy的时候以及读取服务器数据的时候, 如果读到的前四个字节不是数字, 则调用此方法
    *  param:
     *
     *  读取服务器数据是乱码, 即服务器断开了连接
     08-21 14:58:58.958  3057  3072 I SocketService: getParseDataString: sizeStr = ÀÀÀÀ
     08-21 14:58:58.958  3057  3072 I SocketService: getParseDataString: the first 4 bytes invalid, we're convinced the socket has been disconnected*************!
     08-21 14:58:58.959  3057  3072 I SocketService: disConnectSocketServer: start*****************************************************************

     0823: 目前的socket断开原因有三种,
     一种是服务器主动断开, 报错: Connection reset by peer
     一种是客户端断开, 报错: Connection timed out
     一种是(USB切换或息屏时导致Socket端口关闭, 这个错误的可能性原因比较多)断开, 报错: java.net.SocketException: sendto failed: EPIPE (Broken pipe)
    *  return:
    */
    private void disConnectSocketServer()
    {
        Log.i(TAG, "disConnectSocketServer: start*******************************************");

        try
        {
            if (is != null) {
                is.close();
                is = null;
            }
            if (dis != null) {
                dis.close();
                dis = null;
            }
            if (os != null) {
                os.close();
                os = null;
            }
            if (br != null) {
                br.close();
                br = null;
            }
            if (sSocket != null) {
                sSocket.close();
                sSocket = null;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Log.i(TAG, "disConnectSocketServer: finished****************************************");
    }

    /**
    *  author:  hefeng
    *  created: 18-8-25 下午12:50
    *  desc:   确保连接, 读, 写三个线程都已经正常退出即可, 因为service无法停止
     *     当网络断开的时候调用, 用于让三个线程都停止, 确保网络连上的时候调用 onStartCommand的时候
     *     三个线程重新开始运行能够正常
    *  param:
    *  return:
    */
    public void stopSocketService()
    {
        Log.i(TAG, "stopSocketService: ******************************************************");
        if (sWriteServerThread != null) {
            try {
                sWriteServerThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (sReadServerThread != null) {
            // 在这赋值没有用, 主线程写, 在子线程读不到
            //isRunning = false;
            Log.i(TAG, "stopSocketService: sReadServerThread, isRunning = " + isRunning);

            try {
                sReadServerThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 必须等停止服务(读,写,连接三个线程停止之后)成功之后, 再断开连接, 因为读的线程一直在运行, 断网时间太久, 写的线程也会运行
        //synchronized (this)
        //{
            //Log.i(TAG, "stopSocketService: start to stop socket service, isRunning = " + isRunning);
            //stopSelf();
            //Log.i(TAG, "stopSocketService: socket service has stopped, isRunning = " + isRunning);
        //}

        // 虽然getParseDataString已经调用了
        // 但是对于 java.net.SocketException: sendto failed: EPIPE (Broken pipe) 的错误,缺没有调用
        disConnectSocketServer();
        // 手动调用onDestroy没用, 再次启动Service还是不会调用onCreate
        //onDestroy();
    }

    /**
    *  author:  hefeng
    *  created: 18-8-8 下午5:53
    *  desc:    Socket是否处于连接状态
    *  param:
    *  return:
    */
    public boolean isSocketConnected()
    {
        boolean isConnected = false;

        if (sSocket != null && sSocket.isConnected())
        {
            isConnected = true;
        }

        if (isConnected)
        {
            Log.i(TAG, "socket server: connected normally***********************************");
        }
        else
        {
            Log.i(TAG, "socket server: connected failed*************************************");
        }

        return isConnected;// confirmSocketConnected();
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

        try
        {
            if (size > 9999)
            {
                throw new IOException("data send to server too large");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (size < 9999)
        {
            if (size > 1000)
            {
                Log.i(TAG, "getDataPrefix: do nothing");
            }
            else if (size > 100)
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
     *  返回数据格式: 0054{"success":1,"company":"xiaolajiao","command":301}
    *  param:  getParseDataString: sizeStr = \C0\80\C0\80\C0\80\C0\80
    *  return:
    */
    private String getParseDataString(DataInputStream dis) throws IOException
    {
        String dencodedStr = null;
        // 开始的4个字节是数据总长度
        byte[] sizeBytes = new byte[4];
        int size = 0;

        // 不在此处抛出异常, 统一在 ReadServerThread 中处理
        //try
        {
            dis.read(sizeBytes);
            String sizeStr = new String(sizeBytes);
            Log.i(TAG, "getParseDataString: sizeStr = " + sizeStr + ", dis.available = " + dis.available());

            // 前4个字节应该都是数字,否则丢弃此次读取, 并且断开socket服务
            if (!TextUtils.isDigitsOnly(sizeStr))
            {
                Log.i(TAG, "getParseDataString: the first 4 bytes invalid, we're convinced the socket has been disconnected!");
                disConnectSocketServer();
                startReconnect("");
                return null;
            }

            size = Integer.valueOf(sizeStr);
            Log.i(TAG, "getParseDataString: size = " + size);

            // 读取服务器发送的实际数据
            byte[] dataBytes = new byte[size - 4];
            dis.read(dataBytes);
            // 有时候会把前四个字节给删掉?????
            /*
            08-27 23:06:29.534 2804-3100/com.hsf1002.sky.xljgps I/SocketService: getParseDataString: sizeStr = 0054
            08-27 23:06:29.534 2804-3108/com.hsf1002.sky.xljgps I/SocketService: getParseDataString: sizeStr = {"su
            08-27 23:06:29.534 2804-3100/com.hsf1002.sky.xljgps I/SocketService: getParseDataString: size = 54
            08-27 23:06:29.535 2804-3100/com.hsf1002.sky.xljgps I/SocketService: getParseDataString: decodedStr = ccess":1,"company":"xiaolajiao","command":301}��������
            * */
            // 用UTF-8进行解码
            dencodedStr = new String(dataBytes);
            dencodedStr = URLDecoder.decode(dencodedStr, SOCKET_ENCODE_TYPE);
        }
        /*catch (SocketTimeoutException e)
        {
            e.printStackTrace();
        }
        //  java.net.SocketException: recvfrom failed: ECONNRESET (Connection reset by peer)
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }*/

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
    public String getCompletedString(String data)
    {
        String completedStr = null;
        String encodedData = null;

        if (TextUtils.isEmpty(data))
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
    *  created: 18-8-29 下午7:06
    *  desc:       
    *  param:   
    *  return:  
    */
    private void handleException(Exception e, String tag)
    {
        e.printStackTrace();
        
        if (e instanceof SocketTimeoutException) 
        {
            Log.i(TAG, tag + " SocketTimeoutException********************");
        } 
        else if (e instanceof NoRouteToHostException)
        {
            Log.i(TAG, tag + " NoRouteToHostException********************");
        }
        else if (e instanceof ConnectException)
        {
            Log.i(TAG, tag + " ConnectException**************************");
        }
        else if (e instanceof SocketException) 
        {
            Log.i(TAG, tag + " SocketException***************************");
        }
        else
        {
            Log.i(TAG, tag + " Other IOException*************************");
        }
        disConnectSocketServer();
        startReconnect(tag);
    }
    
    /**
    *  author:  hefeng
    *  created: 18-8-29 下午7:17
    *  desc:    如果网络断开了, 不开启重连服务, 网络连上时, 才试图去连
     *          如果网络正常 才去重连
     *  param:
    *  return:  
    */
    private void startReconnect(String tag)
    {
        if (NetworkUtils.isNetworkAvailable())
        {
            Log.i(TAG, tag + " network available, start to reconnect");
            if (isSocketConnected())
            {
                Log.i(TAG, tag + " network available, socket service already started");
                // 如果重连服务已经开启, 关闭
                if (ReconnectSocketService.isServiceAlarmOn(sContext)) {
                    Log.i(TAG, tag + " network available, reconnect service already started, stop it");
                    ReconnectSocketService.setServiceAlarm(sContext, false);
                } else {
                    Log.i(TAG, tag + " network available, reconnect service did not start, ok");
                }
            }
            else {
                // 如果重连服务已经关闭, 开启
                Log.i(TAG, tag + " network available, socket service stopped");
                if (!ReconnectSocketService.isServiceAlarmOn(sContext)) {
                    Log.i(TAG, tag + " network available, reconnect service did not start, start it");
                    ReconnectSocketService.setServiceAlarm(sContext, true);
                } else {
                    Log.i(TAG, tag + " network available, reconnect service already started, ok");
                }
            }
        }
        else
        {
            Log.i(TAG, tag + " network is not available, waiting");
        }
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
            Log.i(TAG, "WriteDataThread: start..............................................");
            // 如果断开了, 就阻塞在这里, 直到重新连接成功
            /*if (!WriteDataThread())
            {

                Log.i(TAG, "writeDataToServer: connected to server failed.");
                try {
                    synchronized (lockWaitConntected) {
                        Log.d(TAG, "WriteDataThread: lockWaitConnected, waiting.");
                        lockWaitConntected.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "WriteDataThread: lockWaitConnected, continue.");

            }*/

            if (isSocketConnected())
            {
                try {
                    // 1 将完整数据进行编码(发送的数据之前4个字节要添加数据的长度)
                    String completedStr = getCompletedString(sGsonString);

                    // 2. 打开输出流, 将数据写入
                    if (!TextUtils.isEmpty(completedStr)) {
                        Log.i(TAG, "WriteDataThread: completedStr = " + completedStr);
                        os = sSocket.getOutputStream();
                        os.write(completedStr.getBytes());
                        os.flush();
                        // 不能关闭输出流
                        //sSocket.shutdownOutput();
                        Log.i(TAG, "WriteDataThread: write data to server successfully^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                    }
                    else
                    {
                        Log.i(TAG, "WriteDataThread: no data to send" );
                    }
                    // 3. 对服务器返回的数据进行读取, 不能在此处处理, 否则可能被读的线程读到数据
                }
                // 插上USB线会导致这个异常
                /*catch (SocketException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }*/
                // java.net.SocketException: sendto failed: EPIPE (Broken pipe)                 Caused by: android.system.ErrnoException: sendto failed: EPIPE (Broken pipe)
                // java.net.SocketException: sendto failed: ETIMEDOUT (Connection timed out)    Caused by: android.system.ErrnoException: sendto failed: ETIMEDOUT (Connection timed out)
                catch (IOException e)
                {
                    handleException(e, "WriteDataThread:");
                }
            }
            else
            {
                startReconnect("WriteDataThread:");
            }
            Log.i(TAG, "WriteDataThread: thread stopped, ready to run again.................");
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-8 下午1:26
    *  desc:    其他类会通过此接口, 将开机信息, 定位信息, SOS信息, 亲情号码和紧急号码 上传到服务器
    *  param:
    *  return:
    */
    public void writeDataToServer(final String data)
    {
        sGsonString = data;

        Log.i(TAG, "writeDataToServer: prepared to write with gson .........................");

        if (sWriteServerThread != null ) {
            if (!sWriteServerThread.isAlive()) {
                sWriteServerThread.run();
            }
        }

        sGsonString = null;
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
        String oneChar;

        for(int i=0; i<length; ++i)
        {
            oneChar = value.substring(i, i+1);

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

        Log.i(TAG, "parseServerMsg: start parsing msg.......................................");

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

        if (TextUtils.isEmpty(paramType))
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

                SprdCommonUtils.getInstance().setRelationNumber(number + "#" + name);

                ResultServerDownloadNumberMsg downloadNumberMsg = new ResultServerDownloadNumberMsg();
                downloadNumberMsg.setSuccess(RESULT_SUCCESS_1);

                gsonStr = ResultServerDownloadNumberMsg.getResultServerDownloadNumberMsgGson(downloadNumberMsg);
                break;
            // 收到平台指令: 上传位置信息
            case SOCKET_TYPE_CURRENT:
                Log.i(TAG, "parseServerMsg: SOCKET_TYPE_CURRENT");
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
                int interval = Integer.valueOf(getParamValueOnlyDigit(paramList[1]));
                Log.i(TAG, "parseServerMsg: paramInterval = " + paramInterval + ", interval = " + interval);
                GpsService.setStartServiceInterval(interval * 1000);

                ResultServerIntervalMsg intervalMsg = new ResultServerIntervalMsg();
                intervalMsg.setSuccess(RESULT_SUCCESS_1);

                gsonStr = ResultServerIntervalMsg.getResultServerIntervalMsgGson(intervalMsg);
                break;
            // 收到平台指令: 超出电子围栏, 给亲情号码发送短信通知
            case SOCKET_TYPE_OUTER_ELECTRIC_BAR:
                Log.i(TAG, "parseServerMsg: SOCKET_TYPE_OUTER_ELECTRIC_BAR");
                SprdCommonUtils.getInstance().sendSosSmsBroadcast();

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

                Log.d(TAG, "parseServerMsg: SOCKET_TYPE_GET_STATUS_INFO");
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
    private void waitConnectThread()
    {
        try {
            sConnectServerThread.join();
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
            Log.i(TAG, "ReadServerThread: thread start, isRunning = " + isRunning + ".......");

            while (isRunning)
            {
                // 0. 先等待socket连接成功, 再进行读写操作
                waitConnectThread();

                if (isSocketConnected()) {
                    try {
                        // 1. 打开输入流, 从服务器端接收数据
                        Log.i(TAG, "ReadServerThread: waiting for server send data.............blocked");
                        is = sSocket.getInputStream();
                        // java.net.SocketException: recvfrom failed: ECONNRESET (Connection reset by peer)
                        dis = new DataInputStream(is);
                        //is.close();
                        //dis.close();
                        // 2. 对服务器返回的数据进行处理
                        String decodedStr = getParseDataString(dis);

                        if (!TextUtils.isEmpty(decodedStr)) {

                            Log.i(TAG, "ReadServerThread: decodedStr = " + decodedStr);
                            // 3. 解析数据, 并将需要发送给服务器的数据返回
                            String gsonStr = parseServerMsg(decodedStr);
                            String completedStr = getCompletedString(gsonStr);

                            // 如果为空,表示此次读取数据的上一次操作是客户端主动发送消息到服务器, 而服务器没有返回任何数据
                            // (本来是有返回值的, 而且返回了客户端发送的command, 客户端收到数据后进行解析, 由于是根据command判断并做出响应,
                            // 又发送了一次数据给服务器端, 造成了死循环, 因此客户端要求服务器不要发送返回状态)
                            if (TextUtils.isEmpty(completedStr))
                            {
                                Log.i(TAG, "ReadServerThread: completedStr == null, no need to send data to server");
                                continue;
                            }
                            else {
                                Log.i(TAG, "ReadServerThread: completedStr = " + completedStr + ", start to write");
                            }

                            // 4. 打开输出流, 将数据写入
                            os = sSocket.getOutputStream();
                            os.write(completedStr.getBytes());
                            os.flush();
                            //sSocket.shutdownOutput();

                            Log.i(TAG, "ReadServerThread: write data to server successfully^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                            //os.close();
                        }
                        else
                        {
                            Log.i(TAG, "ReadServerThread: data empty, no need to send!");
                        }

                        Thread.sleep(100);
                    }
                    catch (IOException e)
                    {
                        handleException(e, "ReadServerThread:");
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    isRunning = false;
                    Log.i(TAG, "ReadServerThread: connected to server failed, lockReadWaitConntected waiting...... isRunning = " + isRunning);
                    /*synchronized (lockReadWaitConntected) {
                        try {
                            sIsReadThreadWaited = true;
                            lockReadWaitConntected.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }*/

                    startReconnect("ReadServerThread:");
                }
            }

            Log.i(TAG, "ReadServerThread: thread stopped, ready to run again................");
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
        //String param = null;
        int startPos = 0;
        int endPos = 0;

        //Log.i(TAG, "getOneParam: position = " + position);

        startPos = params.indexOf("\"", position);
        endPos = params.indexOf("\"", startPos + 1);
        endPos = params.indexOf("\"", endPos + 1 );
        endPos = params.indexOf("\"", endPos + 1);

        /*Log.i(TAG, "getOneParam: startPos = " + startPos + ", endPos = " + endPos);
        try {
            param = params.substring(startPos, endPos + 1);
        }
        catch (StringIndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }*/

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