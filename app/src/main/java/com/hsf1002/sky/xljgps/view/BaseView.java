package com.hsf1002.sky.xljgps.view;

import com.hsf1002.sky.xljgps.bean.ResultMsg;

/**
 * Created by hefeng on 18-6-8.
 */

public interface BaseView {
    void uploadSuccess(ResultMsg resultMsg);
    void uploadFailed(ResultMsg resultMsg);

    void downloadSuccess(ResultMsg resultMsg);
    void downloadFailed(ResultMsg resultMsg);

    void reportSuccess(ResultMsg resultMsg);
    void reportFailed(ResultMsg resultMsg);
}
