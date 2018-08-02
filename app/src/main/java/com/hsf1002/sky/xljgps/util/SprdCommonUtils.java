package com.hsf1002.sky.xljgps.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.hsf1002.sky.xljgps.R;
import com.hsf1002.sky.xljgps.app.XLJGpsApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NAME;
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
import static com.hsf1002.sky.xljgps.util.Constant.SOS_SMS_PREFS_MSG;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_SMS_PREFS_NAME;

/**
 * Created by hefeng on 18-6-11.
 */

public class SprdCommonUtils {
    private static final String TAG = "SprdCommonUtils";
    private static final String BATTERY_CAPACITY_FILE_PATH = "/sys/class/power_supply/battery/capacity";
    private SharedPreferences sosNumSharedPreferences = null;
    private SharedPreferences sosMsgSharedPreferences = null;

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
    *  created: 18-8-1 上午9:55
    *  desc:    
    *  param:
    *  return:
    */
    //private void setSosContext()
    {
        Context sosContext = null;

        try {
            sosContext = XLJGpsApplication.getAppContext().createPackageContext(SOS_PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
            Log.d(TAG, "instance initializer: get sos context");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        sosNumSharedPreferences = sosContext.getSharedPreferences(SOS_NUM_PREFS_NAME, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
        sosMsgSharedPreferences = sosContext.getSharedPreferences(SOS_SMS_PREFS_NAME, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
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

        //setSosContext();

        for (int i=0; i<count; ++i)
        {
            if (i == 0)
            {
                numberString.append(platformCenterNumberStr);
            }
            else
            {
                String relationNumberStr = sosNumSharedPreferences.getString(SOS_NUM_PREFS_ + i, "");
                Log.d(TAG, "getRelationNumber  relationNumber[" + i + "] = " + relationNumberStr);

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

        sendSosSms();

        setRelationNumber("5555, 9, 8, 7");
        Log.d(TAG, "getRelationNumber: numberString = " + numberString.toString());

        return numberString.toString();
    }

    /**
    *  author:  hefeng
    *  created: 18-7-31 下午2:32
    *  desc:    从平台下载的号码设置到本地, 平台中心号码直接设置, 亲情号码发送广播到SOS去设置
    *  param:
    *  return:
    */
    public void setRelationNumber(String relationNumber)
    {
        String[] list = relationNumber.split(",");

        try {
            if (list.length != SOS_NUM_COUNT + 1) {
                throw new Exception("relation number length error");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.d(TAG, "setRelationNumber: list[0] = " + list[0]);
        SharedPreUtils.getInstance().putString(RELATION_NUMBER, list[0]);

        for (int i=1; i<=SOS_NUM_COUNT; ++i)
        {
            sosNumSharedPreferences.edit().putString(SOS_NUM_PREFS_ + i, list[i]);
        }
        sosNumSharedPreferences.edit().commit();

        /*Intent intent = new Intent();
        relationNumber = relationNumber.substring(list[0].length() + 1);
        intent.setAction(ACTION_SET_RELATION_NUMBER);
        intent.putExtra(SET_RELATION_NUMBER, relationNumber);

        Log.d(TAG, "setRelationNumber: relationNumber = " + relationNumber);
        XLJGpsApplication.getAppContext().sendBroadcast(intent);
        */
    }

    /**
     *  author:  hefeng
     *  created: 18-8-1 下午3:51
     *  desc:    发送短信给亲情号码
     *  param:
     *  return:
     */
    public void sendSosSms()
    {
        SmsManager manager = SmsManager.getDefault();
        String mSosMsg = sosMsgSharedPreferences.getString(SOS_SMS_PREFS_MSG, "");
        ArrayList<String> mSosNum = new ArrayList<String>();

        Log.e(TAG,"sendSosSms manager = " +manager);

        mSosNum.add(sosNumSharedPreferences.getString(SOS_NUM_PREFS_1, ""));
        mSosNum.add(sosNumSharedPreferences.getString(SOS_NUM_PREFS_2, ""));
        mSosNum.add(sosNumSharedPreferences.getString(SOS_NUM_PREFS_3, ""));

        if(manager != null)
        {
            Log.e(TAG,"sendSosSms mSosMsg = " + mSosMsg);
            if (TextUtils.isEmpty(mSosMsg))
            {
                mSosMsg = XLJGpsApplication.getAppContext().getResources().getString(R.string.sos_sms);
            }
            Log.e(TAG,"sendSosSms mSosMsg = " + mSosMsg);
            ArrayList<String> list = manager.divideMessage(mSosMsg);

            for (String num : mSosNum)
            {
                if (TextUtils.isEmpty(num))
                {
                    for (String text : list)
                    {
                        manager.sendTextMessage(num, null, text, null, null);
                        Log.e(TAG,"sendSosSms  number = " + num + ", test = " + text);
                    }
                }
            }
        }
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
                numberStringNames.append(names[0]);
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

        //SharedPreUtils.getInstance().putInt(RELATION_NAME_COUNT, count);
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
