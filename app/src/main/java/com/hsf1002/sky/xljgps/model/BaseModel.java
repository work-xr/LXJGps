package com.hsf1002.sky.xljgps.model;

import com.hsf1002.sky.xljgps.presenter.NetworkPresenter;

/**
 * Created by hefeng on 18-6-8.
 * desc: 不管Rxjava HTTP还是Socket TCP都共用此接口
 */

public interface BaseModel {
    void uploadRelationNumber(NetworkPresenter.OnUploadListener listener);
    void reportPosition(int type, NetworkPresenter.OnReportListener listener);
}
