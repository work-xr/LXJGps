package com.hsf1002.sky.xljgps;

import android.app.Application;
import android.util.Log;

/**
 * Created by hefeng on 18-6-6.
 */

public class XLJGpsApplication extends Application {
    private static final String TAG = "XLJGpsApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: ");
        BaiduGpsApp.getInstance().initBaiduSDK(getApplicationContext());
        XLJGpsService.setServiceAlarm(getApplicationContext(), true);
    }
}
