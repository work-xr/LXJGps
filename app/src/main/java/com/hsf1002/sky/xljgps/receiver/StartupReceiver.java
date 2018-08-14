package com.hsf1002.sky.xljgps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hsf1002.sky.xljgps.app.GpsApplication;
import com.hsf1002.sky.xljgps.model.SocketModel;
import com.hsf1002.sky.xljgps.service.GpsService;
import com.hsf1002.sky.xljgps.service.SocketService;

import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_POWERON;

/**
 * Created by hefeng on 18-8-6.
 */

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");

        Context appContext = GpsApplication.getAppContext();

        // 开启定时服务, 默认每隔10分钟上报一次位置信息
        GpsService.setServiceAlarm(appContext, true);

        // 开启sticky服务, 接收服务器下发的各种指令
        appContext.startService(new Intent(appContext, SocketService.class));

        // 开机就上报一次位置信息, socket服务连接成功后再上报
        SocketModel.getInstance().reportPosition(SOCKET_TYPE_POWERON, null);
    }
}
