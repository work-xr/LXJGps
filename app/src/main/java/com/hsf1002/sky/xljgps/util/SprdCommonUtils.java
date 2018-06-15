package com.hsf1002.sky.xljgps.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.hsf1002.sky.xljgps.R;
import com.hsf1002.sky.xljgps.app.XLJGpsApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.hsf1002.sky.xljgps.util.Const.RELATION_NUMBER;
import static com.hsf1002.sky.xljgps.util.Const.RELATION_NUMBER_COUNT;

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

    public String getFormatCurrentTime()
    {
        Date date= new Date();
        SimpleDateFormat smft=new SimpleDateFormat("yyyyMMddHHmmss");  // 20160426132222
        String nowTimeString = smft.format(date.getTime());

        return nowTimeString;
    }

    private int getRelationNumberCount()
    {
        return SharedPreUtils.getInstance().getInt(RELATION_NUMBER_COUNT, 0);
    }

    public String getRelationNumber()
    {
        StringBuilder numberString = new StringBuilder();
        int count = getRelationNumberCount();

        for (int i=0; i<count; ++i)
        {
            String relationNumberStr = SharedPreUtils.getInstance().getString(RELATION_NUMBER + i, "");

            if (!TextUtils.isEmpty(relationNumberStr))
            {
                if (i != 0)
                {
                    numberString.append(",");
                }
                numberString.append(relationNumberStr);
            }
        }

        return numberString.toString();
    }

    public String getRelationNumberNames()
    {
        StringBuilder numberStringNames = new StringBuilder();
        String[]  names = XLJGpsApplication.getAppContext().getResources().getStringArray(R.array.relation_item_name);
        int count = getRelationNumberCount();

        for (int i=0; i<count; ++i)
        {
            String itemName = names[i];

            if (!TextUtils.isEmpty(itemName))
            {
                if (i != 0)
                {
                    numberStringNames.append(",");
                }
                numberStringNames.append(itemName);
            }
        }

        return numberStringNames.toString();
    }

    public String getCurrentBatteryCapacity()
    {
        return String.valueOf(90);
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
