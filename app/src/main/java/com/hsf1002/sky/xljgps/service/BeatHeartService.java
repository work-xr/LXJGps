package com.hsf1002.sky.xljgps.service;

import android.app.AlarmManager;
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
import com.hsf1002.sky.xljgps.baidu.NetworkApp;
import com.hsf1002.sky.xljgps.model.SocketModel;
import com.hsf1002.sky.xljgps.util.NetworkUtils;

import static android.app.PendingIntent.FLAG_NO_CREATE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.hsf1002.sky.xljgps.util.Constant.BEATHEART_SERVICE_INTERVAL;

/**
 * Created by hefeng on 18-8-8.
 * desc: 定时默认5分钟 BEATHEART_SERVICE_INTERVAL 上报心跳信息, 此服务不会停止
 */

public class BeatHeartService extends Service {

    private static final String TAG = "BeatHeartService";
    private static int startServiceInterval = BEATHEART_SERVICE_INTERVAL;
    private static Context sContext = null;
    private static final String ACTION_TIMING_REPORT_BEATHEART = "action.timing.report.beatheart";
    private static Intent sIntentReceiver = new Intent(ACTION_TIMING_REPORT_BEATHEART);
    private static PendingIntent sPendingIntent = null;
    private static AlarmManager sManager = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = GpsApplication.getAppContext();
        registerBeatheartReceiver();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
    *  author:  hefeng
    *  created: 18-8-22 下午4:39
    *  desc:
     *  startService调用一次,就会 运行一次
     *  对于manager.setRepeating的定时服务而言, 每次唤醒一次, 都会调用一次
     *  由系统进行回调,但是和第一次启动服务流程不同
     *  有时候并不会调用, 有时候又连续调用2次
    *  param:
    *  return:
    */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand:");
        //SocketModel.getInstance().reportBeatHeart();
        return START_STICKY;// super.onStartCommand(intent, flags, startId);
    }

    /**
    *  author:  hefeng
    *  created: 18-8-15 下午7:41
    *  desc:
     *  0818: 用轻量级的 handler.postDelayed 来替换重量级的 manager.setRepeating
     *  0823: handler.postDelayed 的方式虽然时间准确, 但是在灭屏的时候无法唤醒AP,导致心跳无法上报
    *  param:
    *  return:
    */
    public static void setServiceAlarm(Context context, boolean isOn)
    {
        //Intent intent = new Intent(context, BeatHeartService.class);
        //PendingIntent pi = PendingIntent.getService(context, 0, intent, FLAG_UPDATE_CURRENT);
        //AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, BeatHeartService.class);
        sContext = GpsApplication.getAppContext();
        sContext.startService(intent);

        sIntentReceiver = new Intent(ACTION_TIMING_REPORT_BEATHEART);
        sPendingIntent = PendingIntent.getBroadcast(context, 0, sIntentReceiver, FLAG_UPDATE_CURRENT);

        sManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.e(TAG, "setServiceAlarm: startServiceInterval = " + startServiceInterval + ", isOn = " + isOn);

        if (isOn)
        {
            //manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), startServiceInterval, pi);
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
    *  created: 18-8-24 下午4:50
    *  desc:
    *  param:
    *  return:
    */
    public static boolean isServiceAlarmOn(Context context)
    {
        Intent intent = new Intent(context, BeatHeartService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, FLAG_NO_CREATE);
        boolean result = (pi != null);
        Log.i(TAG, "isServiceAlarmOn: result = " + result);

        return result;
    }

    /**
    *  author:  hefeng
    *  created: 18-8-30 下午9:12
    *  desc:
    *  param:
    *  return:
    */
    private void registerBeatheartReceiver()
    {
        IntentFilter intentFilter = new IntentFilter(ACTION_TIMING_REPORT_BEATHEART);
        sContext.registerReceiver(beatheartReceiver, intentFilter);
    }

    /**
     *  author:  hefeng
     *  created: 18-8-22 上午9:04
     *  desc:
     *  param:
     *  return:
     */
    private void unregisterBeatheartReceiver()
    {
        sContext.unregisterReceiver(beatheartReceiver);
    }

    /**
    *  author:  hefeng
    *  created: 18-8-30 下午9:12
    *  desc:
    *  param:
    *  return:
    */
    private BroadcastReceiver beatheartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: beatheartReceiver startServiceInterval = " + startServiceInterval);

            // 检查重连服务
            //SocketService service = new SocketService();
            //service.startReconnect(TAG);
            SocketService.getInstance().startReconnect(TAG);

            SocketModel.getInstance().reportBeatHeart();
            sManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + startServiceInterval, sPendingIntent);

            //NetworkApp networkApp = new NetworkApp();
            //networkApp.getLocationNetwork();
            //networkApp.getAllCellInfo();
        }
    };

    @Override
    public void onDestroy() {
        unregisterBeatheartReceiver();
        super.onDestroy();
    }
}
