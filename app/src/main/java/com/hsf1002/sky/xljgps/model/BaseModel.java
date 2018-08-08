package com.hsf1002.sky.xljgps.model;

import com.hsf1002.sky.xljgps.presenter.RxjavaHttpPresenter;

/**
 * Created by hefeng on 18-6-8.
 */

public interface BaseModel {
    void downloadRelationNumber(RxjavaHttpPresenter.OnDownloadListener listener);
    void uploadRelationNumber(RxjavaHttpPresenter.OnUploadListener listener);
    void reportSosPosition(RxjavaHttpPresenter.OnReportListener listener);
    //void reportGsonInfo(RxjavaHttpPresenter.OnReportListener listener);
    void reportPosition(int type, RxjavaHttpPresenter.OnReportListener listener);

    void reportModifyInterval(RxjavaHttpPresenter.OnIntervalListener listener);
    void notifyOuterElectricBar(RxjavaHttpPresenter.OnOuterBarListener listener);
    void getStatusInfo(RxjavaHttpPresenter.OnStatusListener listener);
}
