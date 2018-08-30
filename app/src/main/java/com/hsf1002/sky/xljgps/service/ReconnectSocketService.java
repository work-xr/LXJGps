package com.hsf1002.sky.xljgps.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.hsf1002.sky.xljgps.util.Constant.RECONNCET_SOCKET_SERVICE_INTERVAL;
import static com.hsf1002.sky.xljgps.util.Constant.RECONNCET_SOCKET_SERVICE_SLEEP;
import static com.hsf1002.sky.xljgps.util.Constant.THREAD_KEEP_ALIVE_TIMEOUT;

/**
 * Created by hefeng on 18-8-10.
 * desc: 如果检测到Socket服务断开了, 每隔一段时间就重新连接一次Socket
 */

public class ReconnectSocketService extends Service {
    private static final String TAG = "ReconnectSocketService";
    private static int startServiceInterval = RECONNCET_SOCKET_SERVICE_INTERVAL;
    private static ThreadPoolExecutor sThreadPool;
    private static int sConnectedCount = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *  author:  hefeng
     *  created: 18-8-17 下午6:38
     *  desc:    Caused by: android.os.NetworkOnMainThreadException
     *  param:
     *  return:
     */
    @Override
    public void onCreate() {
        super.onCreate();

        //createThreadPool();
    }

    /**
    *  author:  hefeng
    *  created: 18-8-24 上午11:15
    *  desc:    setServiceAlarm调用之后,    onStartCommand有可能 没有被调用????????????????
     * 怀疑是android系统为了优化电量, 多个service会一起启动, 导致多个service的  onStartCommand 会在同一时间调用
    *  param:
    *  return:
    */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: " );
        reconnectSocket();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
    *  author:  hefeng
    *  created: 18-8-24 下午4:37
    *  desc:
    *  param:
    *  return:
    */
    private static void createThreadPool()
    {
        if (sThreadPool == null) {
            sThreadPool = new ThreadPoolExecutor(
                    1,
                    1,
                    THREAD_KEEP_ALIVE_TIMEOUT,
                    TimeUnit.SECONDS,
                    new LinkedBlockingDeque<Runnable>(),
                    new ThreadPoolExecutor.AbortPolicy());
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-24 下午4:37
    *  desc:
    *  param:
    *  return:
    */
    private static void reconnectSocket()
    {
        if (sThreadPool != null) {
            sThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        sConnectedCount++;
                        Log.i(TAG, "reconnectSocket: startServiceInterval = " + startServiceInterval + ", sConnectedCount = " + sConnectedCount);
                        Thread.sleep(RECONNCET_SOCKET_SERVICE_SLEEP);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    SocketService socketService = new SocketService();
                    socketService.reconnectSocketServer();
                }
            });
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-11 上午10:54
    *  desc:    用于重新连接socket, 如果已经连上, 则停止服务
    *  param:
    *  return:
    */
    public static void setServiceAlarm(Context context, boolean isOn)
    {
        Intent intent = new Intent(context, ReconnectSocketService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if (isOn)
        {
            // 为啥要在创建线程池, 就开始重连? 因为有时候onStartCommand不会被调用,有时候又连续调用两次
            createThreadPool();
            Log.i(TAG, "setServiceAlarm open alarm : startServiceInterval = " + startServiceInterval);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), startServiceInterval, pi);
            reconnectSocket();
        }
        else
        {
            Log.i(TAG, "setServiceAlarm close alarm : ");
            manager.cancel(pi);
            pi.cancel();
            sConnectedCount = 0;
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-13 下午7:30
    *  desc:    判断该服务是否已经开启
    *  param:
    *  return:
    */
    public static boolean isServiceAlarmOn(Context context)
    {
        Intent intent = new Intent(context, ReconnectSocketService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);

        return pi != null;
    }
}
