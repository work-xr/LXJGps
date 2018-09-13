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
import com.hsf1002.sky.xljgps.util.WakeLockUtil;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.hsf1002.sky.xljgps.util.Constant.ACTION_ACTIVATED_CONNECTIVITY;
import static com.hsf1002.sky.xljgps.util.Constant.ACTION_INACTIVATED_CONNECTIVITY;
import static com.hsf1002.sky.xljgps.util.Constant.ACTION_SET_RELATION_NUMBER;
import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_REPORT_POSITION_SLEEP;
import static com.hsf1002.sky.xljgps.util.Constant.RECONNCET_SOCKET_SERVICE_CONNECTION_SLEEP;
import static com.hsf1002.sky.xljgps.util.Constant.RECONNCET_SOCKET_SERVICE_INTERVAL;
import static com.hsf1002.sky.xljgps.util.Constant.RECONNCET_SOCKET_SERVICE_SLEEP;
import static com.hsf1002.sky.xljgps.util.Constant.THREAD_KEEP_ALIVE_TIMEOUT;

/**
 * Created by hefeng on 18-8-10.
 * desc: 如果检测到Socket服务断开了, 马上启动重连服务, 并且每隔一段时间(心跳)就重新连接一次Socket
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
     *  如果是单纯的断开重新连接, 需要 2s 可以重新连接成功
    *  param:
    *  return:
    */
    private static void startReconnectSocket()
    {
        Log.i(TAG, "startReconnectSocket: ");
        createThreadPool();

        if (sThreadPool != null) {
            sThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    // 如果在灭屏状态下连接, 为了防止系统睡下去, 导致连接成功延迟
                    //Log.i(TAG, "run: begin acquireWakeLock ");
                    //WakeLockUtil.getInstance().acquireWakeLock(TAG);

                    // 如果是在连接socket的时候出现exception或者连接失败, 会不断的调用这个方法, 睡1s
                    try
                    {
                        Log.i(TAG, "run: begin sleep 1");
                        Thread.sleep(RECONNCET_SOCKET_SERVICE_SLEEP);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    finally {
                        //Log.i(TAG, "run: begin releaseWakeLock ");
                        //WakeLockUtil.getInstance().releaseWakeLock(TAG);

                        reconnectSocket();

                        // 等待去连接, 连接成功后释放锁
                        // 只要睡眠就不会releaseWakeLock了
                        /*try {
                            Log.i(TAG, "run: begin sleep 2");
                            Thread.sleep(RECONNCET_SOCKET_SERVICE_CONNECTION_SLEEP);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        finally {*/
                            // 放在finally,保证能释放掉,尽量避免用同步
                            //Log.i(TAG, "run: begin releaseWakeLock ");
                            //WakeLockUtil.getInstance().releaseWakeLock(TAG);
                    }
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

    /**
    *  author:  hefeng
    *  created: 18-9-13 下午1:25
    *  desc:    不管亮屏还是灭屏, 在网络正常条件下, 2s 内连接成功
    *  param:
    *  return:
    */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent: ");
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
        //SocketService socketService = new SocketService();
        //socketService.reconnectSocketServer();
        SocketService.getInstance().reconnectSocketServer();
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
