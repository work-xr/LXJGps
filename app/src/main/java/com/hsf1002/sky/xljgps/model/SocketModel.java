package com.hsf1002.sky.xljgps.model;

import android.util.Log;

import com.hsf1002.sky.xljgps.baidu.BaiduGpsApp;
import com.hsf1002.sky.xljgps.params.BaiduGpsParam;
import com.hsf1002.sky.xljgps.params.SosPositionParam;
import com.hsf1002.sky.xljgps.params.UploadRelationNumberParam;
import com.hsf1002.sky.xljgps.presenter.RxjavaHttpPresenter;
import com.hsf1002.sky.xljgps.service.SocketService;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_COMPANY;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_UPLOAD;

/**
 * Created by hefeng on 18-8-9.
 */

public class SocketModel implements BaseModel {
    private static final String TAG = "SocketModel";


    /**
    *  author:  hefeng
    *  created: 18-8-9 下午3:41
    *  desc:    创建单例模式, 开机模式, 定时模式都要调用 reportPosition
    *  param:
    *  return:
    */
    public static SocketModel getInstance()
    {
        return Holder.instance;
    }

    private static final class Holder
    {
        private static final SocketModel instance = new SocketModel();
    }

    /**
    *  author:  hefeng
    *  created: 18-8-9 下午3:37
    *  desc:    上传亲情号码到服务器端
    *  param:
    *  return:
    */
    @Override
    public void uploadRelationNumber(RxjavaHttpPresenter.OnUploadListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        String sosPhones = SprdCommonUtils.getInstance().getRelationNumber();
        String sosPhoneNames = SprdCommonUtils.getInstance().getRelationNumberNames();


        UploadRelationNumberParam sendParam = new UploadRelationNumberParam(
                imei,
                RXJAVAHTTP_COMPANY,
                RXJAVAHTTP_TYPE_UPLOAD,
                sosPhones,
                sosPhoneNames,
                time);
        String gsonString = UploadRelationNumberParam.getSendParamGson(sendParam);
        Log.i(TAG, "uploadRelationNumber: imei = " + imei + ", time = " + time + ", sosPhone = " + sosPhones + ", sosPhoneNames = " + sosPhoneNames+ ", gson = " + gsonString);

        SocketService.getInstance().writeDataToServer(gsonString);
    }

    /**
    *  author:  hefeng
    *  created: 18-8-9 下午3:39
    *  desc:    上报定位信息到服务器端
    *  param:
    *  return:
    */
    @Override
    public void reportPosition(int type, RxjavaHttpPresenter.OnReportListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        int capacity = SprdCommonUtils.getInstance().getCurrentBatteryCapacity();

        BaiduGpsParam baiduGpsMsgBean = BaiduGpsApp.getInstance().getBaiduGpsStatus();
        String positionType = baiduGpsMsgBean.getPosition_type();
        String locType = baiduGpsMsgBean.getLoc_type();
        String latitude = baiduGpsMsgBean.getLatitude();
        String longitude = baiduGpsMsgBean.getLongitude();

        SosPositionParam reportParamBean = new SosPositionParam(
                imei,
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

        SocketService.getInstance().writeDataToServer(gsonString);
    }
}
