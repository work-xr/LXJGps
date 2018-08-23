package com.hsf1002.sky.xljgps.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hsf1002.sky.xljgps.model.SocketModel;

import static com.hsf1002.sky.xljgps.util.Constant.BEATHEART_SERVICE_INTERVAL;

/**
 * Created by hefeng on 18-8-8.
 * desc: 定时默认5分钟 BEATHEART_SERVICE_INTERVAL 上报心跳信息, 此服务不会停止
 */

public class BeatHeartService extends Service {

    private static final String TAG = "BeatHeartService";
    private static int startServiceInterval = BEATHEART_SERVICE_INTERVAL;
    private static Handler handler = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // 在主线程上创建Handler, 不然报错 Can't create handler inside thread that has not called Looper.prepare()
        handler = new Handler();
        handler.postDelayed(beatHeartTask, startServiceInterval);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
    *  author:  hefeng
    *  created: 18-8-22 下午4:39
    *  desc:    只会运行一次
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
    *  desc:    用轻量级的 handler.postDelayed 来替换重量级的 manager.setRepeating
    *  param:
    *  return:
    */
    public static void setServiceAlarm(Context context, boolean isOn)
    {
        /*
        Intent intent = new Intent(context, BeatHeartService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.i(TAG, "setServiceAlarm: startServiceInterval = " + startServiceInterval);

        if (isOn)
        {
            manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), startServiceInterval, pi);
        }
        else
        {
            manager.cancel(pi);
            pi.cancel();
        }*/

        Intent intent = new Intent(context, BeatHeartService.class);
        context.startService(intent);
    }

    /**
    *  author:  hefeng
    *  created: 18-8-15 下午7:19
    *  desc:
    *  param:
    *  return:
    */
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

        handler.removeCallbacks(beatHeartTask);
    }
}
