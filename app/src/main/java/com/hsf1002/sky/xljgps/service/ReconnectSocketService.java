package com.hsf1002.sky.xljgps.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hsf1002.sky.xljgps.app.GpsApplication;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.hsf1002.sky.xljgps.util.Constant.ACTION_ACTIVATED_CONNECTIVITY;
import static com.hsf1002.sky.xljgps.util.Constant.ACTION_INACTIVATED_CONNECTIVITY;
import static com.hsf1002.sky.xljgps.util.Constant.ACTION_SET_RELATION_NUMBER;
import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_REPORT_POSITION_SLEEP;
import static com.hsf1002.sky.xljgps.util.Constant.RECONNCET_SOCKET_SERVICE_INTERVAL;
import static com.hsf1002.sky.xljgps.util.Constant.RECONNCET_SOCKET_SERVICE_SLEEP;
import static com.hsf1002.sky.xljgps.util.Constant.THREAD_KEEP_ALIVE_TIMEOUT;

/**
 * Created by hefeng on 18-8-10.
 * desc: 如果检测到Socket服务断开了, 每隔一段时间就重新连接一次Socket
 */

public class ReconnectSocketService extends IntentService {
    private static final String TAG = "ReconnectSocketService";
    private static int startServiceInterval = RECONNCET_SOCKET_SERVICE_INTERVAL;
    private static Context sContext = null;
    private static final String ACTION_TIMING_RECONNECT_SOCKET = "action.timing.reconnect.socket";
    private static Intent sIntentReceiver = new Intent(ACTION_TIMING_RECONNECT_SOCKET);
    private static PendingIntent sPendingIntent = null;
    private static AlarmManager sManager = null;
    private static ThreadPoolExecutor sThreadPool = null;

    /**
    *  author:  hefeng
    *  created: 18-9-11 下午2:06
    *  desc:    compile error
    *  param:
    *  return:
    */
    public ReconnectSocketService()
    {
        super(TAG);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *  author:  hefeng
     *  created: 18-8-17 下午6:38
     *  desc:
     *  param:
     *  return:
     */
    @Override
    public void onCreate() {
        super.onCreate();
        sContext = GpsApplication.getAppContext();
        //registerReconnectReceiver();
    }

    /**
    *  author:  hefeng
    *  created: 18-9-11 上午11:04
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
    *  created: 18-9-11 上午11:04
    *  desc:
    *  param:
    *  return:
    */
    private static void startReconnectSocket()
    {
        if (sThreadPool != null) {
            sThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    // 如果是在连接socket的时候出现exception或者连接失败, 会不断的调用这个方法
                    try
                    {
                        Thread.sleep(RECONNCET_SOCKET_SERVICE_SLEEP);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    reconnectSocket();
                }
            });
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-24 上午11:15
    *  desc:    setServiceAlarm调用之后,    onStartCommand有可能 没有被调用????????????????
     * 怀疑是android系统为了优化电量, 多个service会一起启动, 导致多个service的  onStartCommand 会在同一时间调用
    *  param:
    *  return:
    */
    //@Override
    //public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.i(TAG, "onStartCommand: " );
        //reconnectSocket();
        //return START_STICKY;// super.onStartCommand(intent, flags, startId);
    //}
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent: ");
        createThreadPool();
        startReconnectSocket();
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
        SocketService socketService = new SocketService();
        socketService.reconnectSocketServer();
    }

    /**
    *  author:  hefeng
    *  created: 18-8-11 上午10:54
    *  desc:    用于重新连接socket, 如果已经连上, 则停止服务
    *  param:
    *  return:
    */
    @Deprecated
    public static void setServiceAlarm(Context context, boolean isOn)
    {
        Intent intent = new Intent(context, ReconnectSocketService.class);
        sContext = GpsApplication.getAppContext();
        sContext.startService(intent);

        sIntentReceiver = new Intent(ACTION_TIMING_RECONNECT_SOCKET);
        sPendingIntent = PendingIntent.getBroadcast(context, 0, sIntentReceiver, FLAG_UPDATE_CURRENT);

        sManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.e(TAG, "setServiceAlarm: startServiceInterval = " + startServiceInterval + ", isOn = " + isOn);

        createThreadPool();

        if (isOn)
        {
            startReconnectSocket();
            sManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), sPendingIntent);
        }
        else
        {
            sManager.cancel(sPendingIntent);
            sPendingIntent.cancel();
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

    /**
     *  author:  hefeng
     *  created: 18-8-30 下午9:12
     *  desc:
     *  param:
     *  return:
     */
    private void registerReconnectReceiver()
    {
        IntentFilter intentFilter = new IntentFilter(ACTION_TIMING_RECONNECT_SOCKET);
        sContext.registerReceiver(reconnectReceiver, intentFilter);
    }

    /**
     *  author:  hefeng
     *  created: 18-8-22 上午9:04
     *  desc:
     *  param:
     *  return:
     */
    private void unregisterReconnectReceiver()
    {
        sContext.unregisterReceiver(reconnectReceiver);
    }

    /**
     *  author:  hefeng
     *  created: 18-8-30 下午9:12
     *  desc:
     *  param:
     *  return:
     */
    private BroadcastReceiver reconnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: reconnectReceiver startServiceInterval = " + startServiceInterval);
            startReconnectSocket();
            sManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + startServiceInterval, sPendingIntent);
        }
    };

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        //unregisterReconnectReceiver();
        super.onDestroy();
    }
}
