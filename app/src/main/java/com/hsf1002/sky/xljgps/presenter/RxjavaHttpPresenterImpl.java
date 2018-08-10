package com.hsf1002.sky.xljgps.presenter;

import com.hsf1002.sky.xljgps.model.SocketModel;
import com.hsf1002.sky.xljgps.result.RelationNumberMsg;
import com.hsf1002.sky.xljgps.result.ResultMsg;
import com.hsf1002.sky.xljgps.model.BaseModel;
import com.hsf1002.sky.xljgps.model.RxjavaHttpModel;
import com.hsf1002.sky.xljgps.view.BaseView;

import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_CURRENT;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_POWERON;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_TIMING;

/**
 * Created by hefeng on 18-6-8.
 */

public class RxjavaHttpPresenterImpl implements
                                                RxjavaHttpPresenter.OnUploadListener,
                                                RxjavaHttpPresenter.OnReportListener
{
    private static final String TAG = "RxjavaHttpPresenterImpl";
    private BaseView view;
    //private BaseModel model = new RxjavaHttpModel();
    private BaseModel model = new SocketModel();

    public RxjavaHttpPresenterImpl(BaseView baseView)
    {
        view = baseView;
    }
    @Override
    public void uploadRelationNumber() {
        model.uploadRelationNumber(this);
    }

    @Override
    public void uploadRelationNumberSuccess(ResultMsg resultMsg) {
        view.uploadSuccess(resultMsg);
    }

    @Override
    public void uploadRelationNumberFailed(String failedMsg) {
        view.uploadFailed(failedMsg);
    }

    @Override
    public void reportSosPosition() {
        model.reportPosition(RXJAVAHTTP_TYPE_TIMING, this);
    }

    @Override
    public void reportSosPositionSuccess(ResultMsg resultMsg) {
        view.reportSuccess(resultMsg);
    }

    @Override
    public void reportSosPositionFailed(String failedMsg) {
        view.reportFailed(failedMsg);
    }
}
