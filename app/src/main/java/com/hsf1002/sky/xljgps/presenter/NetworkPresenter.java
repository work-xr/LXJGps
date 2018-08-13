package com.hsf1002.sky.xljgps.presenter;

import com.hsf1002.sky.xljgps.result.ResultMsg;

/**
 * Created by hefeng on 18-6-8.
 * desc: 不管Rxjava HTTP还是Socket TCP都共用此接口
 */


public interface NetworkPresenter {

    interface OnUploadListener
    {
        void uploadRelationNumber();
        void uploadRelationNumberSuccess(ResultMsg resultMsg);
        void uploadRelationNumberFailed(String failedMsg);
    }

    interface OnReportListener
    {
        void reportSosPosition();
        void reportSosPositionSuccess(ResultMsg resultMsg);
        void reportSosPositionFailed(String failedMsg);
    }
}
