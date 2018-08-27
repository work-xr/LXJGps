package com.hsf1002.sky.xljgps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
