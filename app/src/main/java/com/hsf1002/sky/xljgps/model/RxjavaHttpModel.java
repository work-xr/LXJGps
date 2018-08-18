package com.hsf1002.sky.xljgps.model;

import android.text.TextUtils;
import android.util.Log;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.CommonObserver;
import com.hsf1002.sky.xljgps.baidu.BaiduGpsApp;
import com.hsf1002.sky.xljgps.http.ApiService;
import com.hsf1002.sky.xljgps.params.BaiduGpsParam;
import com.hsf1002.sky.xljgps.params.SosPositionParam;
import com.hsf1002.sky.xljgps.params.UploadNumberParam;
import com.hsf1002.sky.xljgps.presenter.NetworkPresenter;
import com.hsf1002.sky.xljgps.result.ResultMsg;
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
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_UPLOAD;

/**
 * Created by hefeng on 18-6-8.
 */

/**
*  author:  hefeng
*  created: 18-8-10 上午8:44
*  desc:    改为socket通信后,该文件没用了
*/
@Deprecated
public class RxjavaHttpModel implements BaseModel {
    private static final String TAG = "RxjavaHttpModel";

    /**
    *  author:  hefeng
    *  created: 18-7-30 下午2:38
    *  desc:    客户端修改亲情号码后同步至孝老平台, 孝老平台中心号码必须是第四个, 前三个是亲情号码
    *  param:
    *  return:
    */
    @Override
    @Deprecated
    public void uploadRelationNumber(final NetworkPresenter.OnUploadListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        //String manufactory = SprdCommonUtils.getInstance().getManufactory();
        //String model = SprdCommonUtils.getInstance().getModel();
        String sosPhones = SprdCommonUtils.getInstance().getRelationNumber();
        //String sosPhoneNames = SprdCommonUtils.getInstance().getRelationNumberNames();
        String encodedSosPhoneNames = null;
        String data = null;
        String sign = null;

        UploadNumberParam sendParam = new UploadNumberParam(
                imei,
                //manufactory,
                //model,
                RXJAVAHTTP_COMPANY,
                Integer.valueOf(RXJAVAHTTP_TYPE_UPLOAD),
                sosPhones,
                null,//sosPhoneNames,
                time);
        String gsonString = UploadNumberParam.getSendParamGson(sendParam);
        Log.i(TAG, "uploadRelationNumber: imei = " + imei + ", time = " + time + ", sosPhone = " + sosPhones + ", gson = " + gsonString);
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
        Log.i(TAG, "uploadRelationNumber: data = " + data + ", encodedSosPhoneNames = " + encodedSosPhoneNames + ", sign = " + sign);

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
                        Log.i(TAG, "uploadRelationNumber onError: s = " + s);
                        if (listener != null) {
                            listener.uploadRelationNumberFailed(s);
                        }
                    }

                    @Override
                    protected void onSuccess(ResultMsg resultMsg) {
                        Log.i(TAG, "uploadRelationNumber onSuccess: resultMsg= " + resultMsg);
                        if (listener != null) {
                            listener.uploadRelationNumberSuccess(resultMsg);
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
    @Deprecated
    public void reportPosition(int type, final NetworkPresenter.OnReportListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        //String manufactory = SprdCommonUtils.getInstance().getManufactory();
        //String model = SprdCommonUtils.getInstance().getModel();
        int capacity = SprdCommonUtils.getInstance().getCurrentBatteryCapacity();
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
        Log.i(TAG, "reportPosition: imei = " + imei + ", time = " + time + ", capacity = " + capacity + ", gson = " + gsonString);
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
        Log.i(TAG, "reportPosition: data = " + data + ", sign = " + sign);

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
                        Log.i(TAG, "reportPosition onError: s = " + s);
                        if (listener != null) {
                            listener.reportSosPositionFailed(s);
                        }
                    }

                    @Override
                    protected void onSuccess(ResultMsg resultMsg) {
                        Log.i(TAG, "reportPosition onSuccess: resultMsg = " + resultMsg);
                        if (listener != null) {
                            listener.reportSosPositionSuccess(resultMsg);
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
            //Log.i(TAG, "getSortedParam: param = " + param + ", param.length() = " + param.length());
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
            //Log.i(TAG, "geSortedParam: listString[" + i + "]" + listString.get(i).toString());
            sortedParam.append(listString.get(i).toString());

            if (i != length - 1)
            {
                sortedParam.append(",");
            }
        }
        sortedParam.append("}");

        Log.i(TAG, "getSortedParam: after sorted sortedParam = " + sortedParam);

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

        //Log.i(TAG, "getOneParam: startPos = " + startPos + ", endPos = " + endPos);
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

        /*Log.i(TAG, "getOneParam: key = " + key + ", value = " + value);
        Log.i(TAG, "getOneParam: key = " + hashMap.keySet() + ", value = " + hashMap.get(key));
        Log.i(TAG, "getOneParam: hashMap = " + hashMap);
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

}
