package com.hsf1002.sky.xljgps.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by hefeng on 18-6-11.
 */

public class SprdCommonUtils {
    private static final String TAG = "SprdCommonUtils";
    private Context context;

    public void init(Context context)
    {
        this.context = context;
    }

    SprdCommonUtils()
    {
    }

    public String getIMEI()
    {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = null;
        int phoneCount = telephonyManager.getPhoneCount();
        Log.d(TAG, "getIMEI: " + phoneCount);

        for (int slot = 0; slot < phoneCount; slot++)
        {
            try {
                deviceId = telephonyManager.getDeviceId(slot);
            }catch (SecurityException e)
            {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(deviceId))
            {
                Log.d(TAG, "getIMEI: " + deviceId + ", i = " + slot);
                break;
            }
        }

        return deviceId;
    }

    public static SprdCommonUtils getInstance()
    {
        return Holder.instance;
    }

    private static class Holder
    {
        private static final SprdCommonUtils instance = new SprdCommonUtils();
    }
}
