package com.hsf1002.sky.xljgps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.hsf1002.sky.xljgps.baidu.BaiduGpsApp;
import com.hsf1002.sky.xljgps.service.BeatHeartService;
import com.hsf1002.sky.xljgps.service.GpsService;
import com.hsf1002.sky.xljgps.service.SocketService;

/**
 * Created by hefeng on 18-8-25.
 */

public class NetworkReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkReceiver";
    private Context appContext = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        appContext = context.getApplicationContext();

        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.d(TAG, "onReceive: CONNECTIVITY_ACTION .....................................");
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if (info != null) {
                // 网络连接上了,开启socket服务
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Log.d(TAG, "onReceive: NetworkInfo.State.CONNECTED info.type = TYPE_MOBILE");
                    }

                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        Log.d(TAG, "onReceive: NetworkInfo.State.CONNECTED info.type = TYPE_WIFI");
                    }

                    // 开启定位服务, 为了开机上报的定位信息能够成功
                    BaiduGpsApp.getInstance().startBaiduGps();

                    // 开启定时服务, 默认每隔10分钟上报一次位置信息
                    if (!GpsService.isServiceAlarmOn(appContext)) {
                        GpsService.setServiceAlarm(appContext, true);
                    }
                    // 开启sticky服务, 接收服务器下发的各种指令
                    appContext.startService(new Intent(appContext, SocketService.class));
                    return;
                }

                // 如果网络断开了, 关闭socket
                if (NetworkInfo.State.DISCONNECTED == info.getState())
                {
                    Log.d(TAG, "onReceive: NetworkInfo.State.DISCONNECTED ***************************");
                    SocketService socketService = new SocketService();
                    socketService.stopSocketService();
                    GpsService.setServiceAlarm(appContext, false);
                    BeatHeartService.setServiceAlarm(appContext, false);
                    return;
                }
            }
        }
    }
}
