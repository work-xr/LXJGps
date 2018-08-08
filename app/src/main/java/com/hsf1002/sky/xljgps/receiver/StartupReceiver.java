package com.hsf1002.sky.xljgps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hsf1002.sky.xljgps.app.XLJGpsApplication;
import com.hsf1002.sky.xljgps.model.RxjavaHttpModel;
import com.hsf1002.sky.xljgps.service.GpsIntentService;
import com.hsf1002.sky.xljgps.service.SocketService;

import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_POWERON;

/**
 * Created by hefeng on 18-6-6.
 */

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");

        // 开启定时服务, 每隔30分钟上报一次位置信息
        //GpsIntentService.setServiceAlarm(context, true);

        // 开启sticky服务, 接收服务器下发的各种指令
        //XLJGpsApplication.getAppContext().startService(new Intent(XLJGpsApplication.getAppContext(), SocketService.class));

        // 开机就上报一次位置信息
        //RxjavaHttpModel.getInstance().reportPosition(RXJAVAHTTP_TYPE_POWERON, null);
    }
}
