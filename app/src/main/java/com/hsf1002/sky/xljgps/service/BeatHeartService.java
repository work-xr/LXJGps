package com.hsf1002.sky.xljgps.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

    public static final String TAG = "BeatHeartService";
    private static int startServiceInterval = BEATHEART_SERVICE_INTERVAL;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: reportBeatHeart");
        SocketModel.getInstance().reportBeatHeart();

        return super.onStartCommand(intent, flags, startId);
    }

    public static void setServiceAlarm(Context context, boolean isOn)
    {
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
        }
    }
}
