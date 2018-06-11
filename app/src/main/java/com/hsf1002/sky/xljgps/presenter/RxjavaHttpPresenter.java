package com.hsf1002.sky.xljgps.presenter;

import com.hsf1002.sky.xljgps.bean.ResultMsg;

/**
 * Created by hefeng on 18-6-8.
 */

public interface RxjavaHttpPresenter {
    interface OnDownloadListener
    {
        void downloadInfo();
        void downloadInfoSuccess(ResultMsg resultMsg);
        void downloadInfoFailed(ResultMsg resultMsg);
    }

    interface OnUploadListener
    {
        void uploadInfo();
        void uploadInfoSuccess(ResultMsg resultMsg);
        void uploadInfoFailed(ResultMsg resultMsg);
    }

    interface OnReportListener
    {
        void reportInfo();
        void reportInfoSuccess(ResultMsg resultMsg);
        void reportInfoFailed(ResultMsg resultMsg);
    }
}
