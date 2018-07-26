package com.hsf1002.sky.xljgps.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hsf1002.sky.xljgps.baidu.BaiduGpsApp;

import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL;

/**
 * Created by hefeng on 18-6-11.
 */

public class GpsService extends Service {
    private static final String TAG = "GpsService";
    private static int startServiceInterval = BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        BaiduGpsApp.getInstance().startBaiduGps();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void setServiceAlarm(Context context, boolean isOn)
    {
        Intent intent = new Intent(context, GpsService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if  (isOn)
        {
            manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), startServiceInterval, pi);
        }
        else
        {
            manager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context)
    {
        Intent intent = new Intent(context, GpsService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);

        return pi != null;
    }

    public void stopGpsService()
    {
        stopSelf();
    }

    public static void setStartServiceInterval(int interval)
    {
        startServiceInterval = interval;
    }
}
