package com.hsf1002.sky.xljgps.presenter;

import com.hsf1002.sky.xljgps.bean.ResultMsg;
import com.hsf1002.sky.xljgps.model.BaseModel;
import com.hsf1002.sky.xljgps.model.RxjavaHttpModel;
import com.hsf1002.sky.xljgps.view.BaseView;

/**
 * Created by hefeng on 18-6-8.
 */

public class RxjavaHttpPresenterImpl implements RxjavaHttpPresenter.OnDownloadListener,
                                                RxjavaHttpPresenter.OnUploadListener,
                                                RxjavaHttpPresenter.OnReportListener
{
    private static final String TAG = "RxjavaHttpPresenterImpl";
    private BaseView view;
    private BaseModel model = new RxjavaHttpModel();

    public RxjavaHttpPresenterImpl(BaseView baseView)
    {
        view = baseView;
    }

    @Override
    public void downloadInfo() {
        model.downloadInfo(this);
    }

    @Override
    public void downloadInfoSuccess(ResultMsg resultMsg) {
        view.downloadSuccess(resultMsg);
    }

    @Override
    public void downloadInfoFailed(ResultMsg resultMsg) {
        view.downloadFailed(resultMsg);
    }

    @Override
    public void uploadInfo() {
        model.uploadInfo(this);
    }

    @Override
    public void uploadInfoSuccess(ResultMsg resultMsg) {
        view.uploadSuccess(resultMsg);
    }

    @Override
    public void uploadInfoFailed(ResultMsg resultMsg) {
        view.uploadFailed(resultMsg);
    }

    @Override
    public void reportInfo() {
        model.reportInfo(this);
    }

    @Override
    public void reportInfoSuccess(ResultMsg resultMsg) {
        view.reportSuccess(resultMsg);
    }

    @Override
    public void reportInfoFailed(ResultMsg resultMsg) {
        view.reportFailed(resultMsg);
    }
}
