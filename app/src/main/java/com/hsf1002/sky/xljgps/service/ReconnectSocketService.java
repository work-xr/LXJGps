package com.hsf1002.sky.xljgps.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.hsf1002.sky.xljgps.util.Constant.RECONNCET_SOCKET_SERVICE_INTERVAL;

/**
 * Created by hefeng on 18-8-10.
 * desc 每隔一段时间就重新连接一次socket, 第一次是1分钟, 第二次是2分钟, 4, 8, 16....
 */

public class ReconnectSocketService extends Service {
    private static final String TAG = "ReconnectSocketService";
    private static int startServiceInterval = RECONNCET_SOCKET_SERVICE_INTERVAL;
    private static int sConnectedCount = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SocketService.getInstance().connectSocketServer();

        return super.onStartCommand(intent, flags, startId);
    }

    public static void setServiceAlarm(Context context, boolean isOn)
    {
        Intent intent = new Intent(context, ReconnectSocketService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        sConnectedCount++;
        Log.i(TAG, "setServiceAlarm: sConnectedCount = " + sConnectedCount);

        if  (isOn)
        {
            manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), startServiceInterval * sConnectedCount * 2, pi);
        }
        else
        {
            manager.cancel(pi);
            pi.cancel();
        }
    }

    public void stopGpsService()
    {
        stopSelf();
    }
}
