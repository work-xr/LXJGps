package com.hsf1002.sky.xljgps.view;

import com.hsf1002.sky.xljgps.ReturnMsg.ReceiveMsgBean;
import com.hsf1002.sky.xljgps.ReturnMsg.ResultMsg;

/**
 * Created by hefeng on 18-6-8.
 */

public interface BaseView {
    void uploadSuccess(ResultMsg resultMsg);
    void uploadFailed(String resultMsg);

    void downloadSuccess(ResultMsg<ReceiveMsgBean> resultMsg);
    void downloadFailed(String resultMsg);

    void reportSuccess(ResultMsg resultMsg);
    void reportFailed(String resultMsg);
}
