package com.hsf1002.sky.xljgps.model;

import android.util.Log;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.CommonObserver;
import com.hsf1002.sky.xljgps.bean.BookBean;
import com.hsf1002.sky.xljgps.http.ApiService;
import com.hsf1002.sky.xljgps.presenter.RxjavaHttpPresenter;

import static com.hsf1002.sky.xljgps.util.Const.RXJAVAHTTP_BASE_URL;

/**
 * Created by hefeng on 18-6-8.
 */

public class RxjavaHttpModel implements BaseModel {
    private static final String TAG = "RxjavaHttpModel";

    @Override
    public void downloadInfo(RxjavaHttpPresenter.OnDownloadListener listener) {
       // listener.downloadInfoSuccess();
    }

    @Override
    public void uploadInfo(RxjavaHttpPresenter.OnUploadListener listener) {

    }

    @Override
    public void reportInfo(RxjavaHttpPresenter.OnReportListener listener) {

    }

    public void getUserInfo() {
        Log.d(TAG, "getUserInfo: ");

        RxHttpUtils.getSInstance()
                .baseUrl(RXJAVAHTTP_BASE_URL)
                .createSApi(ApiService.class)
                .getBook()
                .compose(Transformer.<BookBean>switchSchedulers())
                .subscribe(new CommonObserver<BookBean>() {
                    @Override
                    protected void onError(String s) {
                        Log.d(TAG, "onError: s = " + s);
                    }

                    @Override
                    protected void onSuccess(BookBean bookBean) {
                        Log.d(TAG, "onSuccess: book = " + bookBean);
                    }
                });

    }

    public static RxjavaHttpModel getInstance()
    {
        return Holder.instance;
    }

    private static class Holder
    {
        private static RxjavaHttpModel instance = new RxjavaHttpModel();
    }

}
