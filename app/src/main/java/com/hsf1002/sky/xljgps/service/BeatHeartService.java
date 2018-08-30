package com.hsf1002.sky.xljgps.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hsf1002.sky.xljgps.model.SocketModel;

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
    //private static Handler handler = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // 在主线程上创建Handler, 不然报错 Can't create handler inside thread that has not called Looper.prepare()
        //handler = new Handler();
        //handler.postDelayed(beatHeartTask, startServiceInterval);
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
     *  同一个线程, 同一个时间,为什么执行了三次:
     08-23 20:44:00.896 2931-2945/com.hsf1002.sky.xljgps I/SocketService: connectSocketServer: success
     08-23 20:44:00.900 2931-2945/com.hsf1002.sky.xljgps I/BeatHeartService: setServiceAlarm: startServiceInterval = 300000

     08-23 20:44:00.896 2931-2945/com.hsf1002.sky.xljgps I/SocketService: connectSocketServer: success
     08-23 20:44:00.900 2931-2945/com.hsf1002.sky.xljgps I/BeatHeartService: setServiceAlarm: startServiceInterval = 300000

     08-23 20:44:00.896 2931-2945/com.hsf1002.sky.xljgps I/SocketService: connectSocketServer: success
     08-23 20:44:00.900 2931-2945/com.hsf1002.sky.xljgps I/BeatHeartService: setServiceAlarm: startServiceInterval = 300000
     *
    *  param:
    *  return:
    */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand:");
        SocketModel.getInstance().reportBeatHeart();
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
        Intent intent = new Intent(context, BeatHeartService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.e(TAG, "setServiceAlarm: startServiceInterval = " + startServiceInterval + ", isOn = " + isOn);

        if (isOn)
        {
            manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.currentThreadTimeMillis(), startServiceInterval, pi);
            //manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.currentThreadTimeMillis(), pi);
        }
        else
        {
            manager.cancel(pi);
            pi.cancel();
        }

        //Intent intent = new Intent(context, BeatHeartService.class);
        //context.startService(intent);
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
    *  created: 18-8-15 下午7:19
    *  desc:
    *  param:
    *  return:
    */
    /*
    private static Runnable beatHeartTask = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "task: beatHeartTask postDelayed-------------------------------------------");
            SocketModel.getInstance().reportBeatHeart();
            handler.postDelayed(beatHeartTask, startServiceInterval);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        //handler.removeCallbacks(beatHeartTask);
    }*/
}
