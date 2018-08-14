package com.hsf1002.sky.xljgps.view;

import com.hsf1002.sky.xljgps.result.ResultMsg;

/**
 * Created by hefeng on 18-6-8.
 * desc: 不管Rxjava HTTP还是Socket TCP都共用此接口
 */

public interface BaseView {
    void uploadSuccess(ResultMsg resultMsg);
    void uploadFailed(String resultMsg);

    void reportSuccess(ResultMsg resultMsg);
    void reportFailed(String resultMsg);
}
