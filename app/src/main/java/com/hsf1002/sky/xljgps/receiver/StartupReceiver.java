package com.hsf1002.sky.xljgps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hsf1002.sky.xljgps.baidu.BaiduGpsApp;
import com.hsf1002.sky.xljgps.model.SocketModel;
import com.hsf1002.sky.xljgps.service.SocketService;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.hsf1002.sky.xljgps.util.Constant.ACTION_POWER_ON;
import static com.hsf1002.sky.xljgps.util.Constant.POWERON_REPORT_POSITION_SLEEP;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_POWERON;
import static com.hsf1002.sky.xljgps.util.Constant.THREAD_KEEP_ALIVE_TIMEOUT;

/**
 * Created by hefeng on 18-8-27.
 */

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";
    private ThreadPoolExecutor threadPoolExecutor = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.d(TAG, "onReceive: action = " + action);

        if (action.equals(ACTION_POWER_ON))
        {
            reportPosition();
        }
    }

    /**
    *  author:  hefeng
    *  created: 18-8-27 下午8:25
    *  desc:    收到开机广播, 等待30秒, 如果socket已经连接, 就上报开机定位信息, 如果socket没有连接, 就不上报了
     *  08-28 12:19:47.827 2828-2828/com.hsf1002.sky.xljgps D/StartupReceiver: onReceive: action = android.intent.action.BOOT_COMPLETED
     *  08-28 12:19:54.667 2828-2828/com.hsf1002.sky.xljgps D/NetworkReceiver: onReceive: NetworkInfo.State.CONNECTED info.type = TYPE_MOBILE
        08-28 12:19:57.807 2828-2828/com.hsf1002.sky.xljgps I/BaiduGpsApp: onReceiveLocation: locType = 161, latitude = 22.537829, longitude = 113.957068, address = 中国深圳市南山区科技南十二路, locationType = lbs, getLocType = 161
        08-28 12:20:07.054 2828-3114/com.hsf1002.sky.xljgps I/SocketService: ConnectServerThread: success^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        08-28 12:20:17.953 2828-2843/com.hsf1002.sky.xljgps I/SocketModel: reportPosition: imei = 864727030038001, time = 20180828122017, capacity = 100, gson = {"company":"xiaolajiao","imei":"864727030038001","power":100,"time":"20180828122017","type":100,"lat":"22.537829","loc_type":"1","lon":"113.957068","position_type":"lbs"}
     *  param:
    *  return:
    */
    private void reportPosition()
    {
        if (threadPoolExecutor == null)
        {
            threadPoolExecutor = new ThreadPoolExecutor(1,
                    1,
                    THREAD_KEEP_ALIVE_TIMEOUT,
                    TimeUnit.SECONDS,
                    new LinkedBlockingDeque<Runnable>(),
                    new ThreadPoolExecutor.AbortPolicy()
                    );

            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(POWERON_REPORT_POSITION_SLEEP);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    SocketService socketService = new SocketService();

                    if (socketService.isSocketConnected())
                    {
                        SocketModel.getInstance().reportPosition(SOCKET_TYPE_POWERON, null);
                    }
                    else
                    {
                        Log.i(TAG, "run: socket connected fail, cannot report position");
                    }
                }
            });
        }
    }
}
