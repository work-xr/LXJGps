package com.hsf1002.sky.xljgps.model;

import com.hsf1002.sky.xljgps.presenter.RxjavaHttpPresenter;

/**
 * Created by hefeng on 18-6-8.
 */

public interface BaseModel {
    void uploadRelationNumber(RxjavaHttpPresenter.OnUploadListener listener);
    void reportPosition(int type, RxjavaHttpPresenter.OnReportListener listener);
}
