package com.hsf1002.sky.xljgps.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hsf1002.sky.xljgps.baidu.BaiduGpsApp;

import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL;

/**
 * Created by hefeng on 18-6-6.
 */

public class XLJGpsService extends IntentService {
    private static final String TAG = "XLJGpsService";
    private static int startServiceInterval = BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL;

    public XLJGpsService()
    {
        super("hello world");
    }

    public XLJGpsService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        BaiduGpsApp.getInstance().startBaiduGps();
    }

    public static void setServiceAlarm(Context context, boolean isOn)
    {
        Intent intent = new Intent(context, XLJGpsService.class);
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
        Intent intent = new Intent(context, XLJGpsService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);

        return pi != null;
    }

    public static void setStartServiceInterval(int interval)
    {
        startServiceInterval = interval;
    }
}
