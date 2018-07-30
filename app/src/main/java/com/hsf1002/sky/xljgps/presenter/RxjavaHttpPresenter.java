package com.hsf1002.sky.xljgps.presenter;

import com.hsf1002.sky.xljgps.result.RelationNumberMsg;
import com.hsf1002.sky.xljgps.result.ResultMsg;
import com.hsf1002.sky.xljgps.result.StatusInfoMsg;

/**
 * Created by hefeng on 18-6-8.
 */

public interface RxjavaHttpPresenter {
    interface OnDownloadListener
    {
        void downloadRelationNumber();
        void downloadRelationNumberSuccess(ResultMsg<RelationNumberMsg> resultMsg);
        void downloadRelationNumberFailed(String failedMsg);
    }

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

    interface OnIntervalListener
    {
        void reportModifyInterval();
        void reportModifyIntervalSuccess(ResultMsg resultMsg);
        void reportModifyIntervalFailed(String failedMsg);
    }

    interface OnOuterBarListener
    {
        void notifyOuterElectricBar();
        void notifyOuterElectricBarSuccess(ResultMsg resultMsg);
        void notifyOuterElectricBarFailed(String failedMsg);
    }

    interface OnStatusListener
    {
        void getStatusInfo();
        void getStatusInfoSuccess(ResultMsg<StatusInfoMsg> resultMsg);
        void getStatusInfoFailed(String failedMsg);
    }
}
