package com.hsf1002.sky.xljgps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hsf1002.sky.xljgps.model.RxjavaHttpModel;
import com.hsf1002.sky.xljgps.service.XLJGpsService;

import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_POWERON;

/**
 * Created by hefeng on 18-6-6.
 */

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        XLJGpsService.setServiceAlarm(context, true);

        RxjavaHttpModel.getInstance().reportPosition(RXJAVAHTTP_TYPE_POWERON, null);
    }
}
