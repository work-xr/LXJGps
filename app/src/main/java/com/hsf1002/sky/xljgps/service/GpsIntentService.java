package com.hsf1002.sky.xljgps.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hsf1002.sky.xljgps.baidu.BaiduGpsApp;
import com.hsf1002.sky.xljgps.util.SharedPreUtils;

import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL_NAME;
import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL_VALUE;

/**
 * Created by hefeng on 18-6-6.
 */

@Deprecated
public class GpsIntentService extends IntentService {
    private static final String TAG = "GpsIntentService";
    private static int startServiceInterval = SharedPreUtils.getInstance().getInt(BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL_NAME, BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL_VALUE);

    /**
    *  author:  hefeng
    *  created: 18-8-7 上午9:26
    *  desc:    不定义在AndroidManifest.xml会报错
    *  param:
    *  return:
    */
    public GpsIntentService()
    {
        super("GpsIntentService");
    }

    public GpsIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent: ");
        BaiduGpsApp.getInstance().startBaiduGps();
    }

    public static void setServiceAlarm(Context context, boolean isOn)
    {
        Intent intent = new Intent(context, GpsIntentService.class);
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
        Intent intent = new Intent(context, GpsIntentService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);

        return pi != null;
    }

    /**
     *  author:  hefeng
     *  created: 18-8-6 下午2:09
     *  desc:    服务器下发指令的时候调用, 用于更改定位信息上报的时间间隔(正式版本默认是30分钟, 测试默认1分钟)
     *  param:
     *  return:
     */
    public static void setStartServiceInterval(int interval)
    {
        startServiceInterval = interval;
        SharedPreUtils.getInstance().putInt(BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL_NAME, interval);
    }
}
