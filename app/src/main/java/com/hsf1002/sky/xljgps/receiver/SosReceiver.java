package com.hsf1002.sky.xljgps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hsf1002.sky.xljgps.model.SocketModel;

import static com.hsf1002.sky.xljgps.util.Constant.ANCTION_SOS_REPORT_POSITION;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_SOS;

/**
 * Created by hefeng on 18-7-30.
 * desc: 用户按了SOS键之后, 会发送此广播, 本应用收到广播后上报SOS信息
 */

public class SosReceiver extends BroadcastReceiver {
    private static final String TAG = "SosReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.d(TAG, "onReceive: action = " + action);

        if (action.equals(ANCTION_SOS_REPORT_POSITION))
        {
            SocketModel.getInstance().reportPosition(SOCKET_TYPE_SOS, null);
        }
    }
}
