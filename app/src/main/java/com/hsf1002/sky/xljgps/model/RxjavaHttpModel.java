package com.hsf1002.sky.xljgps.model;

import android.text.TextUtils;
import android.util.Log;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.CommonObserver;
import com.hsf1002.sky.xljgps.baidu.BaiduGpsApp;
import com.hsf1002.sky.xljgps.http.ApiService;
import com.hsf1002.sky.xljgps.params.BaiduGpsParam;
import com.hsf1002.sky.xljgps.params.DownloadRelationNumberParam;
import com.hsf1002.sky.xljgps.params.GetStatusInfoParam;
import com.hsf1002.sky.xljgps.params.ModifyIntervalParam;
import com.hsf1002.sky.xljgps.params.OuterElectricBarParam;
import com.hsf1002.sky.xljgps.params.SosPositionParam;
import com.hsf1002.sky.xljgps.params.UploadRelationNumberParam;
import com.hsf1002.sky.xljgps.presenter.RxjavaHttpPresenter;
import com.hsf1002.sky.xljgps.result.RelationNumberMsg;
import com.hsf1002.sky.xljgps.result.ResultMsg;
import com.hsf1002.sky.xljgps.result.StatusInfoSendMsg;
import com.hsf1002.sky.xljgps.util.MD5Utils;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_BASE_URL_TEST;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_COMPANY;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_ENCODE_TYPE;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_SECRET_CODE;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_DOWNLOAD;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_GET_STATUS_INFO;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_INTERVAL;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_OUTER_ELECTRIC_BAR;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_SOS;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_UPLOAD;

/**
 * Created by hefeng on 18-6-8.
 */

public class RxjavaHttpModel implements BaseModel {
    private static final String TAG = "RxjavaHttpModel";


    /**
    *  author:  hefeng
    *  created: 18-7-30 下午2:37
    *  desc:    客户端同步孝老平台的亲情号码到本地, 孝老平台中心号码必须是第四个, 前三个是亲情号码
    *  param:   
    *  return:  
    */
    @Override
    public void downloadRelationNumber(final RxjavaHttpPresenter.OnDownloadListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        //String manufactory = SprdCommonUtils.getInstance().getManufactory();
        //String model = SprdCommonUtils.getInstance().getModel();
        String data = null;
        String sign = null;

        DownloadRelationNumberParam receiveParam = new DownloadRelationNumberParam(
                imei,
                //manufactory,
                //model,
                //RXJAVAHTTP_COMPANY,
                RXJAVAHTTP_TYPE_DOWNLOAD,
                time);
        String gsonString = DownloadRelationNumberParam.getReceiveParamGson(receiveParam);
        Log.d(TAG, "downloadRelationNumber: imei = " + imei + ", time = " + time + ", gson = " + gsonString);
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
        Log.d(TAG, "downloadRelationNumber: data = " + data + ", sign = " + sign);

        RxHttpUtils.getSInstance()
                .baseUrl(RXJAVAHTTP_BASE_URL_TEST)
                .createSApi(ApiService.class)
                .downloadRelationNumber(
                        RXJAVAHTTP_COMPANY,
                        data,
                        sign)
                .compose(Transformer.<ResultMsg<RelationNumberMsg>>switchSchedulers())
                .subscribe(new CommonObserver<ResultMsg<RelationNumberMsg>>() {
                    @Override
                    protected void onError(String s) {
                        Log.d(TAG, "downloadRelationNumber onError: s = " + s);
                        if (listener != null) {
                            listener.downloadRelationNumberFailed(s);
                        }
                    }

                    @Override
                    protected void onSuccess(ResultMsg<RelationNumberMsg> receiveMsg) {
                        Log.d(TAG, "downloadRelationNumber onSuccess: receiveMsg = " + receiveMsg);
                        if (listener != null) {
                            listener.downloadRelationNumberSuccess(receiveMsg);
                        }
                    }
                });
    }

    /**
    *  author:  hefeng
    *  created: 18-7-30 下午2:38
    *  desc:    客户端修改亲情号码后同步至孝老平台, 孝老平台中心号码必须是第四个, 前三个是亲情号码
    *  param:
    *  return:
    */
    @Override
    public void uploadRelationNumber(final RxjavaHttpPresenter.OnUploadListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        //String manufactory = SprdCommonUtils.getInstance().getManufactory();
        //String model = SprdCommonUtils.getInstance().getModel();
        String sosPhones = SprdCommonUtils.getInstance().getRelationNumber();
        String sosPhoneNames = SprdCommonUtils.getInstance().getRelationNumberNames();
        String encodedSosPhoneNames = null;
        String data = null;
        String sign = null;

        UploadRelationNumberParam sendParam = new UploadRelationNumberParam(
                imei,
                //manufactory,
                //model,
                RXJAVAHTTP_COMPANY,
                RXJAVAHTTP_TYPE_UPLOAD,
                sosPhones,
                sosPhoneNames,
                time);
        String gsonString = UploadRelationNumberParam.getSendParamGson(sendParam);
        Log.d(TAG, "uploadRelationNumber: imei = " + imei + ", time = " + time + ", sosPhone = " + sosPhones + ", sosPhoneNames = " + sosPhoneNames+ ", gson = " + gsonString);
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
        Log.d(TAG, "uploadRelationNumber: data = " + data + ", encodedSosPhoneNames = " + encodedSosPhoneNames + ", sign = " + sign);

        RxHttpUtils.getSInstance()
                .baseUrl(RXJAVAHTTP_BASE_URL_TEST)
                .createSApi(ApiService.class)
                .uploadRelationNumber(
                        RXJAVAHTTP_COMPANY,
                        data,
                        sign)
                .compose(Transformer.<ResultMsg>switchSchedulers())
                .subscribe(new CommonObserver<ResultMsg>() {
                    @Override
                    protected void onError(String s) {
                        Log.d(TAG, "uploadRelationNumber onError: s = " + s);
                        if (listener != null) {
                            listener.uploadRelationNumberFailed(s);
                        }
                    }

                    @Override
                    protected void onSuccess(ResultMsg resultMsg) {
                        Log.d(TAG, "uploadRelationNumber onSuccess: resultMsg= " + resultMsg);
                        if (listener != null) {
                            listener.uploadRelationNumberSuccess(resultMsg);
                        }
                    }
                });
    }

    /**
    *  author:  hefeng
    *  created: 18-7-30 下午2:39
    *  desc:    上报sos-position信息到孝老平台, 只有当用户手动按SOS按键才会触发
    *  param:
    *  return:
    */
    @Override
    public void reportSosPosition(final RxjavaHttpPresenter.OnReportListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        //String manufactory = SprdCommonUtils.getInstance().getManufactory();
        //String model = SprdCommonUtils.getInstance().getModel();
        String capacity = SprdCommonUtils.getInstance().getCurrentBatteryCapacity();
        String data = null;
        String sign = null;

        BaiduGpsParam baiduGpsMsgBean = BaiduGpsApp.getInstance().getBaiduGpsStatus();
        String positionType = baiduGpsMsgBean.getPosition_type();
        String locType = baiduGpsMsgBean.getLoc_type();
        String latitude = baiduGpsMsgBean.getLatitude();
        String longitude = baiduGpsMsgBean.getLongitude();

        SosPositionParam reportParamBean = new SosPositionParam(imei,
                //manufactory,
                //model,
                RXJAVAHTTP_COMPANY,
                RXJAVAHTTP_TYPE_SOS,
                positionType,
                time,
                locType,
                longitude,
                latitude,
                capacity
                );

        String gsonString = SosPositionParam.getReportParamGson(reportParamBean);
        Log.d(TAG, "reportSosPosition: imei = " + imei + ", time = " + time + ", capacity = " + capacity + ", gson = " + gsonString);
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
        Log.d(TAG, "reportSosPosition: data = " + data + ", sign = " + sign);

        RxHttpUtils.getSInstance()
                .baseUrl(RXJAVAHTTP_BASE_URL_TEST)
                .createSApi(ApiService.class)
                .reportSosPosition(
                        RXJAVAHTTP_COMPANY,
                        data,
                        sign)
                .compose(Transformer.<ResultMsg>switchSchedulers())
                .subscribe(new CommonObserver<ResultMsg>() {
                    @Override
                    protected void onError(String s) {
                        Log.d(TAG, "reportSosPosition onError: s = " + s);
                        if (listener != null) {
                            listener.reportSosPositionFailed(s);
                        }
                    }

                    @Override
                    protected void onSuccess(ResultMsg resultMsg) {
                        Log.d(TAG, "reportSosPosition onSuccess: resultMsg = " + resultMsg);
                        if (listener != null) {
                            listener.reportSosPositionSuccess(resultMsg);
                        }
                    }
                });
    }

    /**
    *  author:  hefeng
    *  created: 18-7-30 下午2:40
    *  desc:    上报position信息到孝老平台
    *  param:
    *  return:
    */
    @Override
    public void reportPosition(String type, final RxjavaHttpPresenter.OnReportListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        //String manufactory = SprdCommonUtils.getInstance().getManufactory();
        //String model = SprdCommonUtils.getInstance().getModel();
        String capacity = SprdCommonUtils.getInstance().getCurrentBatteryCapacity();
        String data = null;
        String sign = null;

        BaiduGpsParam baiduGpsMsgBean = BaiduGpsApp.getInstance().getBaiduGpsStatus();
        String positionType = baiduGpsMsgBean.getPosition_type();
        String locType = baiduGpsMsgBean.getLoc_type();
        String latitude = baiduGpsMsgBean.getLatitude();
        String longitude = baiduGpsMsgBean.getLongitude();

        SosPositionParam reportParamBean = new SosPositionParam(
                imei,
                //manufactory,
                //model,
                RXJAVAHTTP_COMPANY,
                type,
                positionType,
                time,
                locType,
                longitude,
                latitude,
                capacity
        );

        String gsonString = SosPositionParam.getReportParamGson(reportParamBean);
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
                .reportPosition(RXJAVAHTTP_COMPANY,
                        data,
                        sign)
                .compose(Transformer.<ResultMsg>switchSchedulers())
                .subscribe(new CommonObserver<ResultMsg>() {
                    @Override
                    protected void onError(String s) {
                        Log.d(TAG, "reportPosition onError: s = " + s);
                        if (listener != null) {
                            listener.reportSosPositionFailed(s);
                        }
                    }

                    @Override
                    protected void onSuccess(ResultMsg resultMsg) {
                        Log.d(TAG, "reportPosition onSuccess: resultMsg = " + resultMsg);
                        if (listener != null) {
                            listener.reportSosPositionSuccess(resultMsg);
                        }
                    }
                });
    }

    /**
    *  author:  hefeng
    *  created: 18-7-30 下午3:22
    *  desc:    收到服务器下发的修改上报信息频率的指令后,修改定时服务的时间间隔
    *  param:
    *  return:
    */
    @Override
    public void reportModifyInterval(final RxjavaHttpPresenter.OnIntervalListener listener) {
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        String interval = "";
        String data = null;
        String sign = null;

        ModifyIntervalParam modifyIntervalParam = new ModifyIntervalParam(
                interval,
                time,
                RXJAVAHTTP_TYPE_INTERVAL);

        String gsonString = ModifyIntervalParam.getReportParamGson(modifyIntervalParam);
        Log.d(TAG, "reportModifyInterval: time = " + time + ", interval = " + interval + ", gson = " + gsonString);
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
        Log.d(TAG, "reportModifyInterval: data = " + data + ", sign = " + sign);

        RxHttpUtils.getSInstance()
                .baseUrl(RXJAVAHTTP_BASE_URL_TEST)
                .createSApi(ApiService.class)
                .reportModifyInterval(
                        RXJAVAHTTP_COMPANY,
                        data,
                        sign)
                .compose(Transformer.<ResultMsg>switchSchedulers())
                .subscribe(new CommonObserver<ResultMsg>() {
                    @Override
                    protected void onError(String s) {
                        Log.d(TAG, "reportModifyInterval onError: s = " + s);
                        if (listener != null) {
                            listener.reportModifyIntervalFailed(s);
                        }
                    }

                    @Override
                    protected void onSuccess(ResultMsg resultMsg) {
                        Log.d(TAG, "reportModifyInterval onSuccess: resultMsg = " + resultMsg);
                        if (listener != null) {
                            listener.reportModifyIntervalSuccess(resultMsg);
                        }
                    }
                });
    }

    /**
    *  author:  hefeng
    *  created: 18-7-30 下午3:39
    *  desc:    根据孝老平台设置的电子围栏，判断定位上传信息，如果超出电子围栏，并通知设备，设备接收到此消息后向亲情号码发送超出电子围栏手机信息
    *  param:
    *  return:  
    */
    @Override
    public void notifyOuterElectricBar(final RxjavaHttpPresenter.OnOuterBarListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        String data = null;
        String sign = null;

        OuterElectricBarParam outerElectricBarParam = new OuterElectricBarParam(
                imei,
                time,
                RXJAVAHTTP_TYPE_OUTER_ELECTRIC_BAR);

        String gsonString = OuterElectricBarParam.getReportParamGson(outerElectricBarParam);
        Log.d(TAG, "notifyOuterElectricBar: time = " + time + ", imei = " + imei + ", gson = " + gsonString);
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
        Log.d(TAG, "notifyOuterElectricBar: data = " + data + ", sign = " + sign);

        RxHttpUtils.getSInstance()
                .baseUrl(RXJAVAHTTP_BASE_URL_TEST)
                .createSApi(ApiService.class)
                .notifyOuterElectricBar(
                        RXJAVAHTTP_COMPANY,
                        data,
                        sign)
                .compose(Transformer.<ResultMsg>switchSchedulers())
                .subscribe(new CommonObserver<ResultMsg>() {
                    @Override
                    protected void onError(String s) {
                        Log.d(TAG, "notifyOuterElectricBar onError: s = " + s);
                        if (listener != null) {
                            listener.notifyOuterElectricBarFailed(s);
                        }
                    }

                    @Override
                    protected void onSuccess(ResultMsg resultMsg) {
                        Log.d(TAG, "notifyOuterElectricBar onSuccess: resultMsg = " + resultMsg);
                        if (listener != null) {
                            listener.notifyOuterElectricBarSuccess(resultMsg);
                        }

                        // 短信通知亲情号码
                        SprdCommonUtils.getInstance().sendSosSms();
                    }
                });
    }

    /**
    *  author:  hefeng
    *  created: 18-7-30 下午3:45
    *  desc:    孝老平台下发获取设备相关状态信息
    *  param:
    *  return:  
    */
    @Override
    public void getStatusInfo(final RxjavaHttpPresenter.OnStatusListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        String data = null;
        String sign = null;

        GetStatusInfoParam statusInfoParam = new GetStatusInfoParam(
                imei,
                time,
                RXJAVAHTTP_TYPE_GET_STATUS_INFO);
        String gsonString = GetStatusInfoParam.getReportParamGson(statusInfoParam);
        Log.d(TAG, "getStatusInfo: time = " + time + ", imei = " + imei + ", gson = " + gsonString);
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
        Log.d(TAG, "getStatusInfo: data = " + data + ", sign = " + sign);

        RxHttpUtils.getSInstance()
                .baseUrl(RXJAVAHTTP_BASE_URL_TEST)
                .createSApi(ApiService.class)
                .getStatusInfo(
                        RXJAVAHTTP_COMPANY,
                        data,
                        sign)
                .compose(Transformer.<ResultMsg<StatusInfoSendMsg>>switchSchedulers())
                .subscribe(new CommonObserver<ResultMsg<StatusInfoSendMsg>>() {
                    @Override
                    protected void onError(String s) {
                        Log.d(TAG, "getStatusInfo onError: s = " + s);
                        if (listener != null) {
                            listener.getStatusInfoFailed(s);
                        }
                    }

                    @Override
                    protected void onSuccess(ResultMsg<StatusInfoSendMsg> statusInfoMsgResultMsg) {
                        Log.d(TAG, "getStatusInfo onSuccess: result = " + statusInfoMsgResultMsg);
                        if (listener != null) {
                            listener.getStatusInfoSuccess(statusInfoMsgResultMsg);
                        }
                    }
                });
    }

    /**
    *  author:  hefeng
    *  created: 18-7-30 下午4:59
    *  desc:    对gson字符串按照键 key 的字典顺序进行排序
    *  param:   参数的值 value 可以是"", 但是不能是 null, 必须用""包裹起来
    *  return:
    */
    private String getSortedParam(String params)
    {
        StringBuilder sortedParam = new StringBuilder();
        List<String> listString = new ArrayList<String>();
        int length = 0;
        int paramLen = 0;

        if (TextUtils.isEmpty(params) || params.length() == 0)
        {
            return null;
        }

        params = params.substring(1);  // delete {
        String[] strSplits = params.split(":");
        length = strSplits.length - 1;
        Log.e(TAG, "getSortedParam: before sorted paramNumber = " + length + ", params = " + params);

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

    /**
    *  author:  hefeng
    *  created: 18-7-30 下午5:15
    *  desc:    截取单个的参数key-value
    *  param:
    *  return:
    */
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

        int keyStartPos = params.indexOf("\"", position);
        int keyEndPos = params.indexOf("\"", keyStartPos + 1);
        int valueStartPos = params.indexOf("\"", keyEndPos + 1);
        int valueEndPos = params.indexOf("\"", valueStartPos + 1);
        String key = null;
        String value = null;

        //Log.e(TAG, "getOneParam: keyStartPos = " + keyStartPos + ", keyEndPos = " + keyEndPos + ", valueStartPos = " + valueStartPos + ", valueEndPos = " + valueEndPos);

        key = params.substring(keyStartPos + 1, keyEndPos);
        value = params.substring(valueStartPos + 1, valueEndPos);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put(key, value);

        /*Log.d(TAG, "getOneParam: key = " + key + ", value = " + value);
        Log.d(TAG, "getOneParam: key = " + hashMap.keySet() + ", value = " + hashMap.get(key));
        Log.d(TAG, "getOneParam: hashMap = " + hashMap);
*/
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

    /*
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

        public void getPersonList() {
            Log.d(TAG, "getPersonList: ");

            RxHttpUtils.getSInstance()
                    .baseUrl(RXJAVAHTTP_BASE_PERSON_URL)
                    .createSApi(ApiService.class)
                    .getPersonList()
                    .compose(Transformer.<List<PersonBean>>switchSchedulers())
                    .subscribe(new CommonObserver<List<PersonBean>>() {
                        @Override
                        protected void onError(String s) {
                            Log.d(TAG, "onError: s = " + s);
                        }

                        @Override
                        protected void onSuccess(List<PersonBean> personBeans) {
                            Log.d(TAG, "onSuccess: personBeans = " + personBeans);
                        }
                    });
        }

        public void getPersonById(Integer id) {
            Log.d(TAG, "getPersonById: ");

            RxHttpUtils.getSInstance()
                    .baseUrl(RXJAVAHTTP_BASE_PERSON_URL)
                    .createSApi(ApiService.class)
                    .getPersonById(id)
                    .compose(Transformer.<PersonBean>switchSchedulers())
                    .subscribe(new CommonObserver<PersonBean>() {
                        @Override
                        protected void onError(String s) {
                            Log.d(TAG, "onError: s = " + s);
                        }

                        @Override
                        protected void onSuccess(PersonBean personBean) {
                            Log.d(TAG, "onSuccess: personBean = " + personBean);
                        }
                    });
        }

        public void addPerson(String name, Integer age) {
            Log.d(TAG, "addPerson: ");

            RxHttpUtils.getSInstance()
                    .baseUrl(RXJAVAHTTP_BASE_PERSON_URL)
                    .createSApi(ApiService.class)
                    .addPerson(name, age)
                    .compose(Transformer.<PersonBean>switchSchedulers())
                    .subscribe(new CommonObserver<PersonBean>() {
                        @Override
                        protected void onError(String s) {
                            Log.d(TAG, "onError: s = " + s);
                        }

                        @Override
                        protected void onSuccess(PersonBean personBean) {
                            Log.d(TAG, "onSuccess: personBean = " + personBean);
                        }
                    });
        }

    */
}
