package com.hsf1002.sky.xljgps.model;

import com.hsf1002.sky.xljgps.presenter.RxjavaHttpPresenter;

/**
 * Created by hefeng on 18-6-8.
 */

public interface BaseModel {
    void downloadInfo(RxjavaHttpPresenter.OnDownloadListener listener);
    void uploadInfo(RxjavaHttpPresenter.OnUploadListener listener);
    void reportInfo(RxjavaHttpPresenter.OnReportListener listener);
}
