package com.hsf1002.sky.xljgps.model;

import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

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
        String sortedGsonString = getSortedParam(gsonString);

        try
        {
            data = URLEncoder.encode(sortedGsonString, RXJAVAHTTP_ENCODE_TYPE);
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
        String sortedGsonString = getSortedParam(gsonString);

        try
        {
            data = URLEncoder.encode(sortedGsonString, RXJAVAHTTP_ENCODE_TYPE);
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
        Log.d(TAG, "reportInfo: imei = " + imei + ", time = " + time + ", capacity = " + capacity + ", gson = " + gsonString);
        String sortedGsonString = getSortedParam(gsonString);

        try
        {
            data = URLEncoder.encode(sortedGsonString, RXJAVAHTTP_ENCODE_TYPE);
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

    @Override
    public void reportGsonInfo(final RxjavaHttpPresenter.OnReportListener listener) {
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
        Log.d(TAG, "reportGsonInfo: imei = " + imei + ", time = " + time + ", capacity = " + capacity + ", gson = " + gsonString);
        String sortedGsonString = getSortedParam(gsonString);

        try
        {
            data = URLEncoder.encode(sortedGsonString, RXJAVAHTTP_ENCODE_TYPE);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        Log.d(TAG, "reportGsonInfo: encode data = " + data);
        sign = MD5Utils.encrypt(data + RXJAVAHTTP_SECRET_CODE);
        Log.d(TAG, "reportGsonInfo: sign = " + sign);
        String sign1 = MD5Utils.encrypt("1234");
        Log.d(TAG, "reportGsonInfo: sign1 = " + sign1);

        //RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), data.toString());

        RxHttpUtils.getSInstance()
                .baseUrl(RXJAVAHTTP_BASE_URL_TEST)
                .createSApi(ApiService.class)
                .reportGsonInfo(
                        data,
                        sign,
                        RXJAVAHTTP_COMPANY)
                .compose(Transformer.<ReportMsgBean>switchSchedulers())
                .subscribe(new CommonObserver<ReportMsgBean>() {
                    @Override
                    protected void onError(String s) {
                        Log.d(TAG, "reportGsonInfo onError: s = " + s);
                        listener.reportInfoFailed(s);
                    }

                    @Override
                    protected void onSuccess(ReportMsgBean reportMsgBean) {
                        Log.d(TAG, "reportGsonInfo onSuccess: reportMsgBean = " + reportMsgBean);
                        listener.reportInfoSuccess(reportMsgBean);
                    }
                });
    }

    @Override
    public void reportPosition(String type, final RxjavaHttpPresenter.OnReportListener listener) {
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
                type,
                positionType,
                time,
                locType,
                longitude,
                latitude,
                capacity
        );

        String gsonString = ReportParamBean.getReportParamGson(reportParamBean);
        Log.d(TAG, "reportPosition: imei = " + imei + ", time = " + time + ", capacity = " + capacity + ", gson = " + gsonString);
        String sortedGsonString = getSortedParam(gsonString);

        try
        {
            data = URLEncoder.encode(sortedGsonString, RXJAVAHTTP_ENCODE_TYPE);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        sign = MD5Utils.encrypt(data + RXJAVAHTTP_SECRET_CODE);
        Log.d(TAG, "reportPosition: data = " + data + ", sign = " + sign);

        RxHttpUtils.getSInstance()
                .baseUrl(RXJAVAHTTP_BASE_URL_TEST)
                .createSApi(ApiService.class)
                .reportInfo(imei,
                        RXJAVAHTTP_COMPANY,
                        type,
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
                        Log.d(TAG, "reportPosition onError: s = " + s);
                        listener.reportInfoFailed(s);
                    }

                    @Override
                    protected void onSuccess(ReportMsgBean reportMsgBean) {
                        Log.d(TAG, "reportPosition onSuccess: reportMsgBean = " + reportMsgBean);
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

    private String geSortedParam(String param)
    {
        StringBuilder sortedParam = new StringBuilder();
        List<String> listString = new ArrayList<>();
        int strSplitsLength = 0;

        if (TextUtils.isEmpty(param) || param.length() == 0)
        {
            return null;
        }

        String[] strSplits = param.split(",");
        strSplitsLength = strSplits.length;
        Log.d(TAG, "geSortedParam: strSplits.length = " + strSplits.length);
/*
        //Map<String, String> map = new HashMap<>();

        for (int i=0; i<strSplits.length; ++i)
        {
            String[] strOne = strSplits[i].split(":");

            String key = strOne[0];
            String value = strOne[1];

            // key cannot be empty
            if (TextUtils.isEmpty(key) || key.length() == 0)
            {
                break;
            }

            int keySize = key.length();
            int valueSize = value.length();

            key = key.substring(1, keySize - 2);
            value = value.substring(1, valueSize - 2);
            Log.d(TAG, "getOrderedParam: key = " + key + ", value = " + value);
            map.put(key, value);
        }

        // sort the param 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());

        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>()
        {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2)
            {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });*/
        strSplits[0] = strSplits[0].substring(1);
        strSplits[strSplitsLength-1] = strSplits[strSplitsLength-1].substring(0, strSplits[strSplitsLength-1].length() - 1);

        for (int i=0; i<strSplitsLength; ++i)
        {
            listString.add(strSplits[i]);
        }

        Collections.sort(listString, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                String str1=(String) o1;
                String str2=(String) o2;
                if (str1.compareToIgnoreCase(str2)<0){
                    return -1;
                }
                return 1;
            }
        });

        sortedParam.append("{");

        for (int i=0; i<strSplitsLength; ++i)
        {
            //Log.d(TAG, "geSortedParam: listString[" + i + "]" + listString.get(i).toString());
            sortedParam.append(listString.get(i).toString());

            if (i != strSplitsLength - 1)
            {
                sortedParam.append(",");
            }
        }
        sortedParam.append("}");

        Log.d(TAG, "geSortedParam: sortedParam = " + sortedParam);

        return sortedParam.toString();
    }

    private String getSortedParam(String params)
    {
        StringBuilder sortedParam = new StringBuilder();
        List<String> listString = new ArrayList<>();
        int length = 0;
        int paramLen = 0;

        if (TextUtils.isEmpty(params) || params.length() == 0)
        {
            return null;
        }

        params = params.substring(1);  // delete {
        String[] strSplits = params.split(":");
        length = strSplits.length - 1;
        Log.d(TAG, "getSortedParam: before sorted paramNumber = " + length + ", params = " + params);

        for (int i=0; i<length; ++i)
        {
            String param = getOneParam(params, paramLen + i);
            paramLen += param.length();
            //Log.d(TAG, "getSortedParam: param = " + param + ", param.length() = " + param.length());
            listString.add(param);
        }

        Collections.sort(listString, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                String str1=(String) o1;
                String str2=(String) o2;
                if (str1.compareToIgnoreCase(str2)<0){
                    return -1;
                }
                return 1;
            }
        });

        sortedParam.append("{");

        for (int i=0; i<length; ++i)
        {
            //Log.d(TAG, "geSortedParam: listString[" + i + "]" + listString.get(i).toString());
            sortedParam.append(listString.get(i).toString());

            if (i != length - 1)
            {
                sortedParam.append(",");
            }
        }
        sortedParam.append("}");

        Log.d(TAG, "getSortedParam: after sorted sortedParam = " + sortedParam);

        return sortedParam.toString();
    }

    private String getOneParam(String params, int position)
    {
        String param = null;
        int startPos = 0;
        int endPos = 0;

        startPos = params.indexOf("\"", position);
        endPos = params.indexOf("\"", startPos + 1);
        endPos = params.indexOf("\"", endPos + 1 );
        endPos = params.indexOf("\"", endPos + 1);

        //Log.d(TAG, "getOneParam: startPos = " + startPos + ", endPos = " + endPos);
        try {
            param = params.substring(startPos, endPos + 1);
        }
        catch (StringIndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }

        return param;
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
