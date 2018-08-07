package com.hsf1002.sky.xljgps.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.allen.library.RxHttpUtils;
import com.hsf1002.sky.xljgps.baidu.BaiduGpsApp;
import com.hsf1002.sky.xljgps.service.GpsService;
import com.hsf1002.sky.xljgps.service.SocketService;

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

        Log.i(TAG, "onCreate: ...................................");
        sContext = getApplicationContext();

        BaiduGpsApp.getInstance().initBaiduSDK(sContext);

        //GpsService.setServiceAlarm(sContext, true);
        startService(new Intent(sContext, SocketService.class));

        rxjavaHttpInit();
    }

    /**
    *  author:  hefeng
    *  created: 18-8-3 上午11:40
    *  desc:    初始化RXJava配置
    *  param:
    *  return:
    */
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

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate: ...................................");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "onLowMemory: ...................................");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(TAG, "onTrimMemory: ..................................");
    }
}
