package com.hsf1002.sky.xljgps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hsf1002.sky.xljgps.model.SocketModel;

import static com.hsf1002.sky.xljgps.util.Constant.ACTION_SOS_REPORT_NUMBER;

/**
 * Created by hefeng on 18-8-6.
 */

public class ReportNumberReceiver extends BroadcastReceiver {
    private static final String TAG = "ReportNumberReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.d(TAG, "onReceive: action = " + action);

        if (action.equals(ACTION_SOS_REPORT_NUMBER))
        {
            SocketModel.getInstance().uploadRelationNumber(null);
        }
    }
}
