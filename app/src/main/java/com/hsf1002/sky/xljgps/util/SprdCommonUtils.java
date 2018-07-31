package com.hsf1002.sky.xljgps.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.hsf1002.sky.xljgps.R;
import com.hsf1002.sky.xljgps.app.XLJGpsApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.BATTERY_SERVICE;
import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NAME;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NAME_COUNT;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NUMBER;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NUMBER_COUNT;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NUMBER_DEFAULT;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NUM_COUNT;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NUM_PREFS_;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NUM_PREFS_1;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NUM_PREFS_2;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NUM_PREFS_3;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NUM_PREFS_NAME;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_PACKAGE_NAME;

/**
 * Created by hefeng on 18-6-11.
 */

public class SprdCommonUtils {
    private static final String TAG = "SprdCommonUtils";
    private static final String BATTERY_CAPACITY_FILE_PATH = "/sys/class/power_supply/battery/capacity";
    // SOS模块的context
    private Context sosContext = null;
    private int mode = Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE;
    private SharedPreferences sosSharedPreferences = null;

    {
        Context c = null;

        try {
            c = XLJGpsApplication.getAppContext().createPackageContext(SOS_PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        sosSharedPreferences = c.getSharedPreferences(SOS_NUM_PREFS_NAME, mode);
    }

    /**
    *  author:  hefeng
    *  created: 18-7-31 下午2:36
    *  desc:    获取IMEI
    *  param:
    *  return:
    */
    public String getIMEI()
    {
        TelephonyManager telephonyManager = (TelephonyManager)XLJGpsApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = null;
        //int phoneCount = telephonyManager.getPhoneCount();    // Android4.4 不支持此方法

        Log.d(TAG, "getIMEI: start " );

        //for (int slot = 0; slot < phoneCount; slot++)
        {
            try {
                deviceId = telephonyManager.getDeviceId();
            }catch (SecurityException e)
            {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(deviceId))
            {
                Log.d(TAG, "getIMEI: imei = " + deviceId );
               // break;
            }
        }

        return deviceId;
    }

    /**
    *  author:  hefeng
    *  created: 18-7-31 下午2:36
    *  desc:    获取厂商名称
    *  param:
    *  return:
    */
    public String getManufactory()
    {
        return MANUFACTURER;
    }

    /**
    *  author:  hefeng
    *  created: 18-7-31 下午2:36
    *  desc:    获取终端型号
    *  param:
    *  return:
    */
    public String getModel()
    {
        return MODEL;
    }

    /**
    *  author:  hefeng
    *  created: 18-7-31 下午2:36
    *  desc:    格式化当前系统日期时间
    *  param:
    *  return:
    */
    public String getFormatCurrentTime()
    {
        Date date= new Date();
        SimpleDateFormat smft=new SimpleDateFormat("yyyyMMddHHmmss");  // 20160426132222
        String nowTimeString = smft.format(date.getTime());

        return nowTimeString;
    }

    /**
    *  author:  hefeng
    *  created: 18-7-31 下午2:37
    *  desc:    获取设置的不为空的亲情号码(紧急呼叫号码)个数, 已废弃,不再统计个数,每次都传全部上传, 如果为空, 以 "" 表示
    *  param:
    *  return:
    */
    @Deprecated
    private int getRelationNumberCount()
    {
        return SharedPreUtils.getInstance().getInt(RELATION_NUMBER_COUNT, 0);
    }

    /**
    *  author:  hefeng
    *  created: 18-7-31 下午2:35
    *  desc:    获取本地孝老平台号码(从PlatformCenterActivity)和亲情号码(从SOS模块)
    *  param:
    *  return:
    */
    public String getRelationNumber()
    {
        StringBuilder numberString = new StringBuilder();
        String platformCenterNumberStr = SharedPreUtils.getInstance().getString(RELATION_NUMBER, RELATION_NUMBER_DEFAULT);
        int count = SOS_NUM_COUNT + 1;//getRelationNumberCount();

        for (int i=0; i<count; ++i)
        {
            //String relationNumberStr = SharedPreUtils.getInstance().getString(RELATION_NUMBER + i, "");
            if (i == 0)
            {
                numberString.append(platformCenterNumberStr);
            }
            else
            {
                String relationNumberStr = sosSharedPreferences.getString(SOS_NUM_PREFS_ + i, "");
                Log.d(TAG, "getRelationNumber  relationNumber[: " + i + "] = " + relationNumberStr);

                //if (!TextUtils.isEmpty(relationNumberStr))    // 为空也添加
                {
                    //if (i != 0)   // 孝老平台号码是第一个了
                    {
                        numberString.append(",");
                    }
                    numberString.append(relationNumberStr);
                }
            }
        }

        Log.d(TAG, "getRelationNumber: numberString = " + numberString.toString());
/*
        // 没有没有设置紧急呼叫号码, 则不能设置为null, 要设置为""
        String relationNumberStr1 = sosSharedPreferences.getString(SOS_NUM_PREFS_1, "");
        String relationNumberStr2 = sosSharedPreferences.getString(SOS_NUM_PREFS_2, "");
        String relationNumberStr3 = sosSharedPreferences.getString(SOS_NUM_PREFS_3, "");

        numberString
                .append(relationNumberStr1)
                .append(",")
                .append(relationNumberStr2)
                .append(",")
                .append(relationNumberStr3);
        */

        return numberString.toString();
    }

    /**
    *  author:  hefeng
    *  created: 18-7-31 下午2:32
    *  desc:    从平台下载的号码设置到本地
    *  param:
    *  return:
    */
    public void setRelationNumber(String relationNumber)
    {
        String[] list = relationNumber.split(",");
        int count = list.length;

        for (int i=0; i<count; ++i)
        {
            Log.d(TAG, "setRelationNumber: list[" + i + "] = " + list[i]);
            //SharedPreUtils.getInstance().putString(RELATION_NUMBER + i, list[i]);

            if (i == 0)
            {
                SharedPreUtils.getInstance().putString(RELATION_NUMBER, list[i]);
            }
            else
            {
                sosSharedPreferences.edit().putString(SOS_NUM_PREFS_ + i, list[i]);
            }
        }

        //SharedPreUtils.getInstance().putInt(RELATION_NUMBER_COUNT, count);
    }

    /**
    *  author:  hefeng
    *  created: 18-7-31 下午2:32
    *  desc:    获取孝老平台号码和亲情号码名称
    *  param:
    *  return:
    */
    public String getRelationNumberNames()
    {
        StringBuilder numberStringNames = new StringBuilder();
        String[]  names = XLJGpsApplication.getAppContext().getResources().getStringArray(R.array.relation_item_name);
        int count = SOS_NUM_COUNT + 1;//getRelationNumberCount();

        for (int i=0; i<count; ++i)
        {
            if (i == 0)
            {
                numberStringNames.append(names);
            }
            else
            {
                String itemName = XLJGpsApplication.getAppContext().getString(R.string.sos_number) + i;
                numberStringNames.append(",");
                numberStringNames.append(itemName);
            }
        }

        Log.d(TAG, "getRelationNumberNames: = " + numberStringNames.toString());
        return numberStringNames.toString();
    }

    /**
     *  author:  hefeng
     *  created: 18-7-31 下午2:32
     *  desc:    获取孝老平台号码和亲情号码名称后设置到本地, 无用, 因为名称是固定的
     *  param:
     *  return:
     */
    @Deprecated
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

    @TargetApi(19)
    public String getCurrentBatteryCapacity()
    {
        int percent = 0;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {

            File localFile = new File(BATTERY_CAPACITY_FILE_PATH);

            try {
                FileReader localFileReader = new FileReader(localFile);
                char[] arrayOfChar = new char[30];
                try {
                    String[] arrayOfString = new String(arrayOfChar, 0, localFileReader.read(arrayOfChar)).trim().split("\n");
                    Log.i(TAG, "getCurrentBatteryCapacity: percent = " + arrayOfString[0]);

                    //can do run time test only when battery percent >= 40%
                    percent = Integer.parseInt(arrayOfString[0]);
                } catch (IOException localIOException) {
                    Log.e(TAG, "getCurrentBatteryCapacity: read battery-capacity file err!");
                }
            } catch (FileNotFoundException localFileNotFoundException) {
                Log.e(TAG, "getCurrentBatteryCapacity: get battery capacity file err!");        // the path is correct, Maybe no system permission
            }
        }
        else {
            //BatteryManager batteryManager = (BatteryManager)XLJGpsApplication.getAppContext().getSystemService(BATTERY_SERVICE);
            //percent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }

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
