package com.hsf1002.sky.xljgps.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.allen.library.RxHttpUtils;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_BASE_URL_TEST;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_CONNCET_TIMEOUT;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_READ_TIMEOUT;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_WRITE_TIMEOUT;

/**
 * Created by hefeng on 18-6-6.
 */

public class XLJGpsApplication extends Application {
    private static final String TAG = "XLJGpsApplication";
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: ");
        sContext = getApplicationContext();
        //BaiduGpsApp.getInstance().initBaiduSDK(sContext);
        //XLJGpsService.setServiceAlarm(getApplicationContext(), true);
        //startService(new Intent(this, GpsService.class));
        //GpsService.setServiceAlarm(getApplicationContext(), true);
        SprdCommonUtils.getInstance().init(sContext);
        rxjavaHttpInit();

        //RxjavaHttpModel.getInstance().getUserInfo();
        //RxjavaHttpModel.getInstance().getPersonList();
        //RxjavaHttpModel.getInstance().getPersonById(2);
        //RxjavaHttpModel.getInstance().addPerson("lili", 19);
    }

    private void rxjavaHttpInit()
    {
        RxHttpUtils.init(this);
        RxHttpUtils.getInstance()
                .config()
                .setBaseUrl(RXJAVAHTTP_BASE_URL_TEST)
                .setCookie(false)
                .setSslSocketFactory()
                .setReadTimeout(RXJAVAHTTP_READ_TIMEOUT)
                .setWriteTimeout(RXJAVAHTTP_WRITE_TIMEOUT)
                .setConnectTimeout(RXJAVAHTTP_CONNCET_TIMEOUT)
                .setLog(true);
    }

    public static Context getAppContext()
    {
        return sContext;
    }
}
