package com.hsf1002.sky.xljgps.baidu;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.hsf1002.sky.xljgps.app.GpsApplication;

/**
 * Created by hefeng on 18-9-8.
 */

@Deprecated
public class NetworkApp {
    public static final String TAG = "NetworkApp";

    private static final class Holder
    {
        private static final NetworkApp sInstance = new NetworkApp();
    }

    public static NetworkApp getInstance()
    {
        return Holder.sInstance;
    }

    /* 基站信息结构体 */
    public class SCell{
        public int MCC;
        public int MNC;
        public int LAC;
        public int CID;

        @Override
        public String toString() {
            return "SCell{" +
                    "MCC=" + MCC +
                    ", MNC=" + MNC +
                    ", LAC=" + LAC +
                    ", CID=" + CID +
                    '}';
        }
    }

    /* 经纬度信息结构体 */
    public class SItude{
        public String latitude;
        public String longitude;
    }

    /* 获取具体位置信息 */
    public void getLocationNetwork() {

        try {
            /** 获取基站数据 */
            SCell cell = getCellInfo();

            /** 根据基站数据获取经纬度 */
            //SItude itude = getItude(cell);

            /** 获取地理位置 */
            //String location = getLocation(itude);

            Log.d(TAG, "getCellLocation: cell = " + cell /*+ ", location = " + location*/);

        } catch (Exception e) {
            Log.d(TAG, "getCellLocation: error.msg = " + e.getMessage());
        }
    }

    /**
     * 获取基站信息
     *
     * @throws Exception
     */
    private SCell getCellInfo() throws Exception {
        SCell cell = new SCell();

        TelephonyManager mTelNet = (TelephonyManager) GpsApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation location = null;

        try
        {
            location = (GsmCellLocation) mTelNet.getCellLocation();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }

        String operator = mTelNet.getNetworkOperator();
        int mcc = Integer.parseInt(operator.substring(0, 3));
        int mnc = Integer.parseInt(operator.substring(3));
        int cid = location.getCid();
        int lac = location.getLac();

        cell.MCC = mcc;
        cell.MNC = mnc;
        cell.LAC = lac;
        cell.CID = cid;

        return cell;
    }

    public void getAllCellInfo()
    {
        TelephonyManager mTelNet = (TelephonyManager) GpsApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        ConnectivityManager ns = (ConnectivityManager) GpsApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            Log.d(TAG," getAllCellInfo = " + mTelNet.getAllCellInfo() + " \n " + " ns = " + ns.getActiveNetworkInfo());
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }

}
