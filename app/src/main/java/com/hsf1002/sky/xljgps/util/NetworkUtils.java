package com.hsf1002.sky.xljgps.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.hsf1002.sky.xljgps.app.GpsApplication;

/**
 * Created by hefeng on 18-8-27.
 */

public class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    public static boolean isNetworkAvailable()
    {
        ConnectivityManager manager = (ConnectivityManager) GpsApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        Boolean isMobileConn = networkInfo.isConnected();

        Log.i(TAG, "isNetworkAvailable: " + isMobileConn);

        return isMobileConn;
    }
}
