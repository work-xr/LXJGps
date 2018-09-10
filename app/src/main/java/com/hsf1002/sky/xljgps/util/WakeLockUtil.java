package com.hsf1002.sky.xljgps.util;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.hsf1002.sky.xljgps.app.GpsApplication;



/**
 * Created by hefeng on 18-9-6.
 */

@Deprecated
public class WakeLockUtil {
    private static final String TAG = "WakeLockUtil";
    private static PowerManager.WakeLock wakeLock = null;

    public void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) GpsApplication.getAppContext().getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, getClass()
                    .getCanonicalName());
            if (null != wakeLock) {
                Log.i(TAG, "call acquireWakeLock");
                wakeLock.acquire();
            }
        }
    }

    public void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            Log.i(TAG, "call releaseWakeLock");
            wakeLock.release();
            wakeLock = null;
        }
    }
}
