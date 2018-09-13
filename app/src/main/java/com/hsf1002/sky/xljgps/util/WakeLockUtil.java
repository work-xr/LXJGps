package com.hsf1002.sky.xljgps.util;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.hsf1002.sky.xljgps.app.GpsApplication;


/**
 * Created by hefeng on 18-9-6.
 */

public class WakeLockUtil {
    private static final String TAG = "WakeLockUtil";
    private static PowerManager.WakeLock wakeLock = null;

    public static WakeLockUtil getInstance()
    {
        return Holder.instance;
    }

    private static final class Holder
    {
        private static final WakeLockUtil instance = new WakeLockUtil();
    }

    public void acquireWakeLock(String destination) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) GpsApplication.getAppContext().getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, getClass()
                    .getCanonicalName());
            if (null != wakeLock) {
                Log.i(TAG, "call acquireWakeLock from ----------------------- " + destination);
                wakeLock.acquire();
            }
        }
    }

    public void releaseWakeLock(String destination) {
        if (null != wakeLock && wakeLock.isHeld()) {
            Log.i(TAG, "call releaseWakeLock from ------------------------" + destination);
            wakeLock.release();
            wakeLock = null;
        }
    }
}
