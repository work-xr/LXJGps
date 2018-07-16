package com.hsf1002.sky.xljgps.util;

import android.content.Context;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.hsf1002.sky.xljgps.R;
import com.hsf1002.sky.xljgps.app.XLJGpsApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.BATTERY_SERVICE;
import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NAME;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NAME_COUNT;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NUMBER;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NUMBER_COUNT;

/**
 * Created by hefeng on 18-6-11.
 */

public class SprdCommonUtils {
    private static final String TAG = "SprdCommonUtils";
    private static final String BATTERY_CAPACITY_FILE_PATH = "/sys/class/power_supply/battery/capacity";
    private Context context;

    public void init(Context context)
    {
        this.context = context;
    }

    SprdCommonUtils(){
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

    public String getManufactory()
    {
        return MANUFACTURER;
    }

    public String getModel()
    {
        return MODEL;
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

    public void setRelationNumber(String relationNumber)
    {
        String[] list = relationNumber.split(",");
        int count = list.length;

        for (int i=0; i<count; ++i)
        {
            Log.d(TAG, "setRelationNumber: list[" + i + "] = " + list[i]);
            SharedPreUtils.getInstance().putString(RELATION_NUMBER + i, list[i]);
        }

        SharedPreUtils.getInstance().putInt(RELATION_NUMBER_COUNT, count);
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

    public void setRelationNumberNames(String relationNames)
    {
        String[] list = relationNames.split(",");
        int count = list.length;

        for (int i=0; i<count; ++i)
        {
            Log.d(TAG, "setRelationNumberNames: list[" + i + "] = " + list[i]);
            SharedPreUtils.getInstance().putString(RELATION_NAME + i, list[i]);
        }

        SharedPreUtils.getInstance().putInt(RELATION_NAME_COUNT, count);
    }

    public String getCurrentBatteryCapacity()
    {
        int percent;
        /*
        File localFile = new File(BATTERY_CAPACITY_FILE_PATH);

        try
        {
            FileReader localFileReader = new FileReader(localFile);
            char[] arrayOfChar = new char[30];
            try
            {
                String[] arrayOfString = new String(arrayOfChar, 0, localFileReader.read(arrayOfChar)).trim().split("\n");
                Log.i(TAG, "getCurrentBatteryCapacity: percent = " + arrayOfString[0]);

                //can do run time test only when battery percent >= 40%
                percent = Integer.parseInt(arrayOfString[0]);
            }
            catch (IOException localIOException)
            {
                Log.e(TAG, "getCurrentBatteryCapacity: read battery-capacity file err!");
            }
        }
        catch (FileNotFoundException localFileNotFoundException)
        {
            Log.e(TAG, "getCurrentBatteryCapacity: get battery capacity file err!");                    // the path is correct, Maybe no system permission
        }*/

        BatteryManager batteryManager = (BatteryManager)XLJGpsApplication.getAppContext().getSystemService(BATTERY_SERVICE);
        percent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        Log.d(TAG, "getCurrentBatteryCapacity: battery = " + percent);

        return String.valueOf(percent);
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
