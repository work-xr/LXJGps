package com.hsf1002.sky.xljgps.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.SystemProperties;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.hsf1002.sky.xljgps.R;
import com.hsf1002.sky.xljgps.app.GpsApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static android.content.Context.BATTERY_SERVICE;
import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;
import static com.hsf1002.sky.xljgps.util.Constant.ACTION_SET_RELATION_NUMBER;
import static com.hsf1002.sky.xljgps.util.Constant.ACTION_SOS_SEND_MSG;
import static com.hsf1002.sky.xljgps.util.Constant.RELATION_NUMBER_COUNT;
import static com.hsf1002.sky.xljgps.util.Constant.SET_RELATION_NUMBER;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_EMERGENCY_NUM_PROPERTY;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NAME_INVALID_VALUE;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NAME_PROPERTY_1;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NAME_PROPERTY_2;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NAME_PROPERTY_3;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NUM_INVALID_VALUE;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NUM_PROPERTY_1;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NUM_PROPERTY_2;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_NUM_PROPERTY_3;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_SMS_INVALID_VALUE;
import static com.hsf1002.sky.xljgps.util.Constant.SOS_SMS_PROPERTY_MSG;

/**
 * Created by hefeng on 18-6-11.
 */

public class SprdCommonUtils {
    private static final String TAG = "SprdCommonUtils";
    private static final String BATTERY_CAPACITY_FILE_PATH = "/sys/class/power_supply/battery/capacity";
    private static Context sAppContext = null;

    public SprdCommonUtils() {
        sAppContext = GpsApplication.getAppContext();
    }

    /**
    *  author:  hefeng
    *  created: 18-8-13 下午7:36
    *  desc:    创建单例模式
    *  param:
    *  return:
    */
    public static SprdCommonUtils getInstance()
    {
        return Holder.instance;
    }

    private static class Holder
    {
        private static final SprdCommonUtils instance = new SprdCommonUtils();
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
        TelephonyManager telephonyManager = (TelephonyManager) sAppContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = null;
        //int phoneCount = telephonyManager.getPhoneCount();    // Android4.4 不支持此方法

        //Log.i(TAG, "getIMEI: start " );

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
               // Log.i(TAG, "getIMEI: imei = " + deviceId );
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
        //String platformCenterNumberStr = SharedPreUtils.getInstance().getString(RELATION_NUMBER, RELATION_NUMBER_DEFAULT);
        ArrayList<String> list = new ArrayList<String>();

        //setSosContext();

        /*for (int i=0; i<SOS_NUM_COUNT + 1; ++i)
        {
            if (i == SOS_NUM_COUNT)
            {
                numberString.append(platformCenterNumberStr.trim());
            }
            else
            {
                String relationNumberStr = sosNumSharedPreferences.getString(SOS_NUM_PREFS_ + (i+1), "");
                Log.i(TAG, "getRelationNumber  relationNumber[" + i + "] = " + relationNumberStr);

                //if (!TextUtils.isEmpty(relationNumberStr))
                {
                    numberString.append(relationNumberStr.trim());
                    numberString.append(",");
                }
            }
        }*/
        verifyPropertyNameNumber(list, false);

        numberString.
                append(list.get(0))
                .append(",")
                .append(list.get(1))
                .append(",")
                .append(list.get(2))
                .append(",")
                .append(list.get(3));

        Log.i(TAG, "getRelationNumber: numberString = " + numberString.toString());

        return numberString.toString();
    }

    /**
    *  author:  hefeng
    *  created: 18-7-31 下午2:32
    *  desc:    从平台下载的号码设置到本地, 平台中心号码直接设置, 亲情号码发送广播到SOS去设置
    *  param:   {"imei":"869426020023138","command":103, "e_order":"老妈，老弟，老姐, 养老服务中心号码"，"sos_phone":",13555555553, 13555555553 , 13555555555，059612349","time":"20170504113555"}
     *  前三个号码是亲情号码, 前三个名称是亲情号码对应的通讯录中的姓名
     *  第四个号码是紧急呼叫号码1, 默认的养老平台号码, 对应的名字,固定是孝老平台中心号码
     *  此广播发送后, SimpleHome模块收到后,会设置更新前三个亲情号码和名字, SOS模块收到后会更新紧急呼叫号码1
    *  return:
    */
    public void setRelationNumber(String relationNumberName)
    {
        String[] list = relationNumberName.split("#");
        String[] listNumber = list[0].split(",");
        String[] listName = list[1].split(",");

        Log.i(TAG, "setRelationNumber: listNumber = " + Arrays.toString(listNumber));
        Log.i(TAG, "setRelationNumber: listName = " + Arrays.toString(listName));

        Intent intent = new Intent();
        intent.setAction(ACTION_SET_RELATION_NUMBER);
        intent.putExtra(SET_RELATION_NUMBER, relationNumberName);

        Log.i(TAG, "setRelationNumber: relationNumberName = " + relationNumberName);
        sAppContext.sendBroadcast(intent);
    }

    /**
    *  author:  hefeng
    *  created: 18-8-22 下午7:18
    *  desc:
    *  param:
    *  return:
    */
    public void sendSosSmsBroadcast()
    {
        Intent intent = new Intent();
        intent.setAction(ACTION_SOS_SEND_MSG);

        Log.i(TAG, "sendSosSmsBroadcast: ");
        sAppContext.sendBroadcast(intent);
    }

    /**
     *  author:  hefeng
     *  created: 18-8-1 下午3:51
     *  desc:    发送短信给亲情号码
     *
     *  0822: 用户设置的短信可能很长, 编码之后的长度可能超过SystemPerperties能够设置的最大值: PROP_VALUE_MAX 93
     *        此处不再发送短信, 只发广播, 让SOS去发送短信
     *  param:
     *  return:
     */
    @Deprecated
    public void sendSosSms()
    {
        SmsManager manager = SmsManager.getDefault();
        String mSosMsg = null;
        ArrayList<String> mSosNum = new ArrayList<String>();

        Log.e(TAG,"sendSosSms manager = " + manager);

        verifyPropertyNameNumber(mSosNum, false);

        mSosMsg = SystemProperties.get(SOS_SMS_PROPERTY_MSG, "");

        if (mSosMsg.equals(SOS_SMS_INVALID_VALUE))
        {
            mSosMsg = sAppContext.getResources().getString(R.string.sos_sms, mSosNum.get(3));
        }
        Log.e(TAG,"sendSosSms mSosMsg = " + mSosMsg);

        if(manager != null)
        {
            ArrayList<String> list = manager.divideMessage(mSosMsg);

            for (String num : mSosNum)
            {
                if (!TextUtils.isEmpty(num) && num.length() > 10)
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
    *  created: 18-8-3 下午8:00
    *  desc:    先把从读取的数据判断一下,是不是无效值
    *  param:
    *  return:
    */
    private void verifyPropertyNameNumber(ArrayList<String> list, boolean isName)
    {
        if (isName)
        {
            String nameStr1 = SystemProperties.get(SOS_NAME_PROPERTY_1, "");
            String nameStr2 = SystemProperties.get(SOS_NAME_PROPERTY_2, "");
            String nameStr3 = SystemProperties.get(SOS_NAME_PROPERTY_3, "");
            String defaultStr = "";//GpsApplication.getAppContext().getResources().getString(R.string.sos_relation_number);

            Log.i(TAG, "verifyPropertyNumber: nameStr1 = " + nameStr1 + ", nameStr2 = " + nameStr2 + ", nameStr3 = " + nameStr3);

            if (nameStr1.equals(SOS_NAME_INVALID_VALUE)) {
                //list.add(defaultStr + 1);
                list.add(defaultStr);
            } else {
                list.add(nameStr1);
            }

            if (nameStr2.equals(SOS_NAME_INVALID_VALUE)) {
                //list.add(defaultStr + 2);
                list.add(defaultStr);
            } else {
                list.add(nameStr2);
            }

            if (nameStr3.equals(SOS_NAME_INVALID_VALUE)) {
                //list.add(defaultStr + 3);
                list.add(defaultStr);
            } else {
                //list.add(nameStr3);
            }
        }
        else {
            String numStr1 = SystemProperties.get(SOS_NUM_PROPERTY_1, "");
            String numStr2 = SystemProperties.get(SOS_NUM_PROPERTY_2, "");
            String numStr3 = SystemProperties.get(SOS_NUM_PROPERTY_3, "");
            String emergencyNumStr = SystemProperties.get(SOS_EMERGENCY_NUM_PROPERTY, "");

            Log.i(TAG, "verifyPropertyNumber: numStr1 = " + numStr1 + ", numStr2 = " + numStr2 + ", numStr3 = " + numStr3 + ", emergencyNumStr = " + emergencyNumStr);

            if (numStr1.equals(SOS_NUM_INVALID_VALUE)) {
                list.add("");
            } else {
                list.add(numStr1);
            }

            if (numStr2.equals(SOS_NUM_INVALID_VALUE)) {
                list.add("");
            } else {
                list.add(numStr2);
            }

            if (numStr3.equals(SOS_NUM_INVALID_VALUE)) {
                list.add("");
            } else {
                list.add(numStr3);
            }

            if (emergencyNumStr.equals(SOS_NUM_INVALID_VALUE)) {
                list.add("");
            } else {
                list.add(emergencyNumStr);
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
    public String getRelationNames()
    {
        StringBuilder nameString = new StringBuilder();
        String platformCenterNameStr = GpsApplication.getAppContext().getString(R.string.platform_center_number);
        ArrayList<String> list = new ArrayList<String>();

        verifyPropertyNameNumber(list, true);

        nameString.
                append(list.get(0))
                .append(",")
                .append(list.get(1))
                .append(",")
                .append(list.get(2))
                .append(",")
                .append(platformCenterNameStr);

        Log.i(TAG, "getRelationNames: numberString = " + nameString.toString());

        return nameString.toString();
    }

    /**
     *  author:  hefeng
     *  created: 18-7-31 下午2:32
     *  desc:    统一和号码一起设置
     *  param:
     *  return:
     */
    @Deprecated
    public void setRelationNames(String relationNames)
    {
    }

    //@TargetApi(19)
    public /*String */ int getCurrentBatteryCapacity()
    {
        int percent = 0;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {

            File localFile = new File(BATTERY_CAPACITY_FILE_PATH);

            try {
                FileReader localFileReader = new FileReader(localFile);
                char[] arrayOfChar = new char[30];
                try {
                    String[] arrayOfString = new String(arrayOfChar, 0, localFileReader.read(arrayOfChar)).trim().split("\n");
                    //Log.i(TAG, "getCurrentBatteryCapacity: percent = " + arrayOfString[0]);

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
            BatteryManager batteryManager = (BatteryManager)sAppContext.getSystemService(BATTERY_SERVICE);
            percent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }

        Log.i(TAG, "getCurrentBatteryCapacity: battery = " + percent);

        return /*String.valueOf(percent)*/ percent;
    }
}
