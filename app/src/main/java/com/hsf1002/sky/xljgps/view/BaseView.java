package com.hsf1002.sky.xljgps.view;

import com.hsf1002.sky.xljgps.bean.ReceiveMsgBean;
import com.hsf1002.sky.xljgps.bean.ReportMsgBean;
import com.hsf1002.sky.xljgps.bean.ResultMsgBean;
import com.hsf1002.sky.xljgps.bean.SendMsgBean;

/**
 * Created by hefeng on 18-6-8.
 */

public interface BaseView {
    void uploadSuccess(SendMsgBean resultMsg);
    void uploadFailed(String resultMsg);

    void downloadSuccess(ReceiveMsgBean resultMsg);
    void downloadFailed(String resultMsg);

    void reportSuccess(ReportMsgBean resultMsg);
    void reportFailed(String resultMsg);
}
