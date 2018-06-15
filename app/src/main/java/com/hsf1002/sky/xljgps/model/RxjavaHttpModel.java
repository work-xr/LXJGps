package com.hsf1002.sky.xljgps.model;

import android.util.Log;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.CommonObserver;
import com.hsf1002.sky.xljgps.baidu.BaiduGpsApp;
import com.hsf1002.sky.xljgps.bean.BaiduGpsMsgBean;
import com.hsf1002.sky.xljgps.bean.BookBean;
import com.hsf1002.sky.xljgps.bean.ReceiveMsgBean;
import com.hsf1002.sky.xljgps.bean.ReceiveParamBean;
import com.hsf1002.sky.xljgps.bean.ReportMsgBean;
import com.hsf1002.sky.xljgps.bean.ReportParamBean;
import com.hsf1002.sky.xljgps.bean.SendMsgBean;
import com.hsf1002.sky.xljgps.bean.SendParamBean;
import com.hsf1002.sky.xljgps.http.ApiService;
import com.hsf1002.sky.xljgps.presenter.RxjavaHttpPresenter;
import com.hsf1002.sky.xljgps.util.MD5Utils;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.hsf1002.sky.xljgps.util.Const.RXJAVAHTTP_BASE_URL_DOUBAN;
import static com.hsf1002.sky.xljgps.util.Const.RXJAVAHTTP_BASE_URL_TEST;
import static com.hsf1002.sky.xljgps.util.Const.RXJAVAHTTP_COMPANY;
import static com.hsf1002.sky.xljgps.util.Const.RXJAVAHTTP_ENCODE_TYPE;
import static com.hsf1002.sky.xljgps.util.Const.RXJAVAHTTP_SECRET_CODE;
import static com.hsf1002.sky.xljgps.util.Const.RXJAVAHTTP_TYPE_DOWNLOAD;
import static com.hsf1002.sky.xljgps.util.Const.RXJAVAHTTP_TYPE_REPORT;
import static com.hsf1002.sky.xljgps.util.Const.RXJAVAHTTP_TYPE_UPLOAD;

/**
 * Created by hefeng on 18-6-8.
 */

public class RxjavaHttpModel implements BaseModel {
    private static final String TAG = "RxjavaHttpModel";

    @Override
    public void downloadInfo(final RxjavaHttpPresenter.OnDownloadListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        String data = null;
        String sign = null;

        ReceiveParamBean receiveParam = new ReceiveParamBean(imei, RXJAVAHTTP_COMPANY, RXJAVAHTTP_TYPE_DOWNLOAD, time);
        String gsonString = ReceiveParamBean.getReceiveParamGson(receiveParam);
        Log.d(TAG, "downloadInfo: imei = " + imei + ", time = " + time + ", gson = " + gsonString);

        try
        {
            data = URLEncoder.encode(gsonString, RXJAVAHTTP_ENCODE_TYPE);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        sign = MD5Utils.encrypt(data + RXJAVAHTTP_SECRET_CODE);
        Log.d(TAG, "downloadInfo: data = " + data + ", sign = " + sign);

        RxHttpUtils.getSInstance()
                .baseUrl(RXJAVAHTTP_BASE_URL_TEST)
                .createSApi(ApiService.class)
                .downloadInfo(imei,
                        RXJAVAHTTP_COMPANY,
                        RXJAVAHTTP_TYPE_DOWNLOAD,
                        time,
                        data,
                        sign)
                .compose(Transformer.<ReceiveMsgBean>switchSchedulers())
                .subscribe(new CommonObserver<ReceiveMsgBean>() {
                    @Override
                    protected void onError(String s) {
                        Log.d(TAG, "downloadInfo onError: s = " + s);
                        listener.downloadInfoFailed(s);
                    }

                    @Override
                    protected void onSuccess(ReceiveMsgBean receiveMsg) {
                        Log.d(TAG, "downloadInfo onSuccess: receiveMsg = " + receiveMsg);
                        listener.downloadInfoSuccess(receiveMsg);
                    }
                });
    }

    @Override
    public void uploadInfo(final RxjavaHttpPresenter.OnUploadListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        String sosPhones = SprdCommonUtils.getInstance().getRelationNumber();
        String sosPhoneNames = SprdCommonUtils.getInstance().getRelationNumberNames();
        String encodedSosPhoneNames = null;
        String data = null;
        String sign = null;

        SendParamBean sendParam = new SendParamBean(imei, RXJAVAHTTP_COMPANY, RXJAVAHTTP_TYPE_UPLOAD, sosPhones, sosPhoneNames,  time);
        String gsonString = SendParamBean.getSendParamGson(sendParam);
        Log.d(TAG, "uploadInfo: imei = " + imei + ", time = " + time + ", sosPhone = " + sosPhones + ", sosPhoneNames = " + sosPhoneNames+ ", gson = " + gsonString);

        try
        {
            data = URLEncoder.encode(gsonString, RXJAVAHTTP_ENCODE_TYPE);
            encodedSosPhoneNames = URLEncoder.encode(sosPhoneNames, RXJAVAHTTP_ENCODE_TYPE);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        sign = MD5Utils.encrypt(data + RXJAVAHTTP_SECRET_CODE);
        Log.d(TAG, "uploadInfo: data = " + data + ", encodedSosPhoneNames = " + encodedSosPhoneNames + ", sign = " + sign);

        RxHttpUtils.getSInstance()
                .baseUrl(RXJAVAHTTP_BASE_URL_TEST)
                .createSApi(ApiService.class)
                .uploadInfo(imei,
                        RXJAVAHTTP_COMPANY,
                        RXJAVAHTTP_TYPE_UPLOAD,
                        sosPhones,
                        encodedSosPhoneNames,
                        time,
                        data,
                        sign)
                .compose(Transformer.<SendMsgBean>switchSchedulers())
                .subscribe(new CommonObserver<SendMsgBean>() {
                    @Override
                    protected void onError(String s) {
                        Log.d(TAG, "uploadInfo onError: s = " + s);
                        listener.uploadInfoFailed(s);
                    }

                    @Override
                    protected void onSuccess(SendMsgBean sendMsg) {
                        Log.d(TAG, "uploadInfo onSuccess: sendmsg= " + sendMsg);
                        listener.uploadInfoSuccess(sendMsg);
                    }
                });
    }

    @Override
    public void reportInfo(final RxjavaHttpPresenter.OnReportListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        String capacity = SprdCommonUtils.getInstance().getCurrentBatteryCapacity();
        String data = null;
        String sign = null;

        BaiduGpsMsgBean baiduGpsMsgBean = BaiduGpsApp.getInstance().getBaiduGpsStatus();
        String positionType = baiduGpsMsgBean.getPosition_type();
        String locType = baiduGpsMsgBean.getLoc_type();
        String latitude = baiduGpsMsgBean.getLatitude();
        String longitude = baiduGpsMsgBean.getLongitude();

        ReportParamBean reportParamBean = new ReportParamBean(imei,
                RXJAVAHTTP_COMPANY,
                RXJAVAHTTP_TYPE_REPORT,
                positionType,
                time,
                locType,
                longitude,
                latitude,
                capacity
                );

        String gsonString = ReportParamBean.getReportParamGson(reportParamBean);
        Log.d(TAG, "reportInfo: imei = " + imei + ", time = " + time + ", gson = " + gsonString);

        try
        {
            data = URLEncoder.encode(gsonString, RXJAVAHTTP_ENCODE_TYPE);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        sign = MD5Utils.encrypt(data + RXJAVAHTTP_SECRET_CODE);
        Log.d(TAG, "reportInfo: data = " + data + ", sign = " + sign);

        RxHttpUtils.getSInstance()
                .baseUrl(RXJAVAHTTP_BASE_URL_TEST)
                .createSApi(ApiService.class)
                .reportInfo(imei,
                        RXJAVAHTTP_COMPANY,
                        RXJAVAHTTP_TYPE_REPORT,
                        positionType,
                        time,
                        locType,
                        longitude,
                        latitude,
                        capacity,
                        data,
                        sign)
                .compose(Transformer.<ReportMsgBean>switchSchedulers())
                .subscribe(new CommonObserver<ReportMsgBean>() {
                    @Override
                    protected void onError(String s) {
                        Log.d(TAG, "reportInfo onError: s = " + s);
                        listener.reportInfoFailed(s);
                    }

                    @Override
                    protected void onSuccess(ReportMsgBean reportMsgBean) {
                        Log.d(TAG, "reportInfo onSuccess: reportMsgBean = " + reportMsgBean);
                        listener.reportInfoSuccess(reportMsgBean);
                    }
                });
    }

    public void getUserInfo() {
        Log.d(TAG, "getUserInfo: ");

        RxHttpUtils.getSInstance()
                .baseUrl(RXJAVAHTTP_BASE_URL_DOUBAN)
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
