package com.hsf1002.sky.xljgps.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hsf1002.sky.xljgps.app.GpsApplication;
import com.hsf1002.sky.xljgps.model.SocketModel;

import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL_NAME;
import static com.hsf1002.sky.xljgps.util.Constant.BAIDU_GPS_SERVICE_SCAN_INTERVAL;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_TIMING;
import static com.hsf1002.sky.xljgps.util.SharedPreUtils.getInstance;

/**
 * Created by hefeng on 18-6-11.
 * desc: 定时默认10分钟 BAIDU_GPS_SERVICE_SCAN_INTERVAL 上报定位信息, 此服务不会停止
 */

public class GpsService extends Service {
    private static final String TAG = "GpsService";
    private static int startServiceInterval = getInstance().getInt(BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL_NAME, BAIDU_GPS_SERVICE_SCAN_INTERVAL);
    private static Context sContext = null;
    /*private static final String ACTION_TIMING_REPORT_GPS_LOCATION = "action.timing.report.gps.location";
    private static Intent intentReceiver = new Intent(ACTION_TIMING_REPORT_GPS_LOCATION);
    private static PendingIntent pi = null;
    private static AlarmManager manager = null;
*/
    /**
    *  author:  hefeng
    *  created: 18-8-20 上午8:43
    *  desc:    如果是开机第一次上报数据, 一分钟之后再开始定位, 主要是等待socket连接成功, 并且网络已经连上, 确保第一次定位数据准确, 否则会得到一个定位是深圳的默认值
    *  param:
    *  return:
    */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        SocketModel.getInstance().reportPosition(SOCKET_TYPE_TIMING, null);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
    *  author:  hefeng
    *  created: 18-8-15 下午7:29
    *  desc:
    0815  从Android 4.4 版本开始，Alarm 任务的触发时间将会变得不准确，有可能会延迟一段时间后任务才能得到执行,
     *  这是系统在耗电性方面进行的优化。系统会自动检测目前有多少Alarm 任务存在，然后将触发时间将近的几个任务放在一起执行，
     *  这就可以大幅度地减少CPU 被唤醒的次数，从而有效延长电池的使用时间。如果你要求Alarm 任务的执行时间必须准备无误，
     *  Android 仍然提供了解决方案。使用AlarmManager 的setExact()方法来替代set()方法
     *
     *  相对于定时的准确性而言, 功耗更为重要, 依然采用Android默认的处理方式
     *
    0822  gps正式版本30分钟上报一次,可以改为 setExact
    *  param:
    *  return:
    */
    public static void setServiceAlarm(Context context, boolean isOn)
    {
        //PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        /*Intent intent = new Intent(context, GpsService.class);
        sContext = GpsApplication.getAppContext();
        sContext.startService(intent);

        intentReceiver = new Intent(ACTION_TIMING_REPORT_GPS_LOCATION);
        pi = PendingIntent.getBroadcast(context, 0, intentReceiver, 0);
*/
        sContext = GpsApplication.getAppContext();

        Intent intent = new Intent(context, GpsService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.i(TAG, "setServiceAlarm: startServiceInterval = " + startServiceInterval + ", isOn = " + isOn);

        if (isOn)
        {
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), startServiceInterval, pi);
            //manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
        }
        else
        {
            manager.cancel(pi);
            pi.cancel();
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-6 下午2:09
    *  desc:    服务器下发指令的时候调用, 用于更改定位信息上报的时间间隔(正式版本默认是30分钟, 测试默认1分钟)
     *   必须等上一次的时间到了之后, 才会开始生效
    *  param:
    *  return:
    */
    public static void setStartServiceInterval(int interval)
    {
        startServiceInterval = interval;
        getInstance().putInt(BAIDU_GPS_SCAN_SPAN_TIME_INTERVAL_NAME, interval);

        // 设置完时间间隔后, 先关闭服务
        setServiceAlarm(sContext, false);

        // 设置完时间间隔后, 再开启服务
        setServiceAlarm(sContext, true);
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
        Intent intent = new Intent(context, GpsService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);

        return pi != null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //unregisterGpsReceiver();
    }

    /**
     *  author:  hefeng
     *  created: 18-8-22 上午9:04
     *  desc:
     *  param:
     *  return:
     */
    /*
    private void registerGpsReceiver()
    {
        IntentFilter intentFilter = new IntentFilter(ACTION_TIMING_REPORT_GPS_LOCATION);
        sContext.registerReceiver(gpsReceiver, intentFilter);
    }
*/
    /**
     *  author:  hefeng
     *  created: 18-8-22 上午9:04
     *  desc:
     *  param:
     *  return:
     */
    /*
    private void unregisterGpsReceiver()
    {
        sContext.unregisterReceiver(gpsReceiver);
    }
    */

    /**
     *  author:  hefeng
     *  created: 18-8-6 下午2:08
     *  desc:    如果多次执行了Context的startService方法，那么Service的onStartCommand方法也会相应的多次调用
     *  param:
     *  return:
     */

    /**
    *  author:  hefeng
    *  created: 18-8-22 上午9:00
    *  desc:    循环发送广播
    *  param:
    *  return:
    */
    /*
    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: gpsReceiver startServiceInterval = " + startServiceInterval);
            reportPosition();
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + startServiceInterval, pi);
        }
    };*/
}
