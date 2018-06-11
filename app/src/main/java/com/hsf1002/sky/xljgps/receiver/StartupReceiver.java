package com.hsf1002.sky.xljgps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by hefeng on 18-6-6.
 */

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // start gps service
        Log.d(TAG, "onReceive: ");
        //XLJGpsService.setServiceAlarm(context, true);
    }
}
