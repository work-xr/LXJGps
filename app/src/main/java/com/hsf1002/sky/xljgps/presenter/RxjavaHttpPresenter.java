package com.hsf1002.sky.xljgps.presenter;

import com.hsf1002.sky.xljgps.bean.ReceiveMsgBean;
import com.hsf1002.sky.xljgps.bean.ReportMsgBean;
import com.hsf1002.sky.xljgps.bean.SendMsgBean;

/**
 * Created by hefeng on 18-6-8.
 */

public interface RxjavaHttpPresenter {
    interface OnDownloadListener
    {
        void downloadInfo();
        void downloadInfoSuccess(ReceiveMsgBean resultMsg);
        void downloadInfoFailed(String failedMsg);
    }

    interface OnUploadListener
    {
        void uploadInfo();
        void uploadInfoSuccess(SendMsgBean resultMsg);
        void uploadInfoFailed(String failedMsg);
    }

    interface OnReportListener
    {
        void reportInfo();
        void reportInfoSuccess(ReportMsgBean resultMsg);
        void reportInfoFailed(String failedMsg);
    }
}
