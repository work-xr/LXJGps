package com.hsf1002.sky.xljgps.presenter;

import com.hsf1002.sky.xljgps.ReturnMsg.ReceiveMsgBean;
import com.hsf1002.sky.xljgps.ReturnMsg.ResultMsg;

/**
 * Created by hefeng on 18-6-8.
 */

public interface RxjavaHttpPresenter {
    interface OnDownloadListener
    {
        void downloadInfo();
        void downloadInfoSuccess(ResultMsg<ReceiveMsgBean> resultMsg);
        void downloadInfoFailed(String failedMsg);
    }

    interface OnUploadListener
    {
        void uploadInfo();
        void uploadInfoSuccess(ResultMsg resultMsg);
        void uploadInfoFailed(String failedMsg);
    }

    interface OnReportListener
    {
        void reportInfo();
        void reportInfoSuccess(ResultMsg resultMsg);
        void reportInfoFailed(String failedMsg);
    }
}
