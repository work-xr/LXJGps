package com.hsf1002.sky.xljgps.model;

import android.util.Log;
import android.widget.Toast;

import com.hsf1002.sky.xljgps.R;
import com.hsf1002.sky.xljgps.app.GpsApplication;
import com.hsf1002.sky.xljgps.baidu.BaiduGpsApp;
import com.hsf1002.sky.xljgps.params.BaiduGpsParam;
import com.hsf1002.sky.xljgps.params.BeatHeartParam;
import com.hsf1002.sky.xljgps.params.SosPositionParam;
import com.hsf1002.sky.xljgps.params.UploadNumberParam;
import com.hsf1002.sky.xljgps.presenter.NetworkPresenter;
import com.hsf1002.sky.xljgps.service.SocketService;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_COMPANY;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_BEATHEART;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_TIMING;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_UPLOAD;
import static com.hsf1002.sky.xljgps.util.Constant.THREAD_KEEP_ALIVE_TIMEOUT;

/**
 * Created by hefeng on 18-8-9.
 */

/**
*  author:  hefeng
*  created: 18-8-10 上午8:48
*  desc:    基于TCP协议的Socket通信
*/
public class SocketModel implements BaseModel {
    private static final String TAG = "SocketModel";
    private static ThreadPoolExecutor sThreadPool;
    private SocketService socketService = new SocketService();

    /**
    *  author:  hefeng
    *  created: 18-8-10 下午4:36
    *  desc:    初始化一个只有一个线程的线程池
    *  param:
    *  return:
    */
    public SocketModel() {
        sThreadPool = new ThreadPoolExecutor(
                1,
                1,
                THREAD_KEEP_ALIVE_TIMEOUT,
                 TimeUnit.SECONDS,
                 new LinkedBlockingDeque<Runnable>(),
                 new ThreadPoolExecutor.AbortPolicy());
    }

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
    *  created: 18-8-10 下午4:42
    *  desc:    在单独的线程去操作
    *  param:
    *  return:
    */
    private void postDataToServer(final String gsonString)
    {
        sThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                // 由于开机会同时开启Socket服务, 并上报开机信息到服务器(会调用此方法), 如果不加此延迟, 可能Socket的三个线程还没有开始运行
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                // 先等待socket连接成功, 再进行读写操作
                socketService.waitConnectThread();
                // 上传信息到服务器
                socketService.writeDataToServer(gsonString);
            }
        });
    }

    /**
    *  author:  hefeng
    *  created: 18-8-10 上午9:21
    *  desc:    上报心跳,默认10分钟一次(BEATHEART_SERVICE_INTERVAL)
    *  param:
    *  return:
    */
    public void reportBeatHeart()
    {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        BeatHeartParam beatHeartParam = new BeatHeartParam(imei, SOCKET_COMPANY, SOCKET_TYPE_BEATHEART);
        final String gsonString = BeatHeartParam.getBeatHeartParamGson(beatHeartParam);
        Log.i(TAG, "reportBeatHeart: gsonString = " + gsonString);

        postDataToServer(gsonString);
    }

    /**
    *  author:  hefeng
    *  created: 18-8-9 下午3:37
    *  desc:    上传亲情号码到服务器端
    *  param:
    *  return:  从服务器返回: {"success":1,"time":"20180810085825","command":203}
    */
    @Override
    public void uploadRelationNumber(NetworkPresenter.OnUploadListener listener) {
        String imei = SprdCommonUtils.getInstance().getIMEI();
        String time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        String sosPhones = SprdCommonUtils.getInstance().getRelationNumber();
        String sosPhoneNames = SprdCommonUtils.getInstance().getRelationNames();

        UploadNumberParam sendParam = new UploadNumberParam(
                imei,
                SOCKET_COMPANY,
                SOCKET_TYPE_UPLOAD,
                sosPhones,
                sosPhoneNames,
                time);
        final String gsonString = UploadNumberParam.getSendParamGson(sendParam);
        Log.i(TAG, "uploadRelationNumber: imei = " + imei + ", time = " + time + ", sosPhone = " + sosPhones + ", sosPhoneNames = " + sosPhoneNames+ ", gson = " + gsonString);

        postDataToServer(gsonString);

        Toast.makeText(GpsApplication.getAppContext(), GpsApplication.getAppContext().getResources().getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
    }

    /**
    *  author:  hefeng
    *  created: 18-8-9 下午3:39
    *  desc:    上报定位信息到服务器端
    *  param:
    *  return:  从服务器返回: {"success":1,"imei":"867400020316620","time":"20180810104857","command":202}
    */
    @Override
    public void reportPosition(int type, NetworkPresenter.OnReportListener listener) {
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
                SOCKET_COMPANY,
                type,
                positionType,
                time,
                locType,
                longitude,
                latitude,
                capacity
        );

        final String gsonString = SosPositionParam.getReportParamGson(reportParamBean);
        Log.i(TAG, "reportPosition: imei = " + imei + ", time = " + time + ", capacity = " + capacity + ", gson = " + gsonString);

        postDataToServer(gsonString);

        if (type == SOCKET_TYPE_TIMING) {
            Toast.makeText(GpsApplication.getAppContext(), GpsApplication.getAppContext().getResources().getString(R.string.report_success), Toast.LENGTH_SHORT).show();
        }
    }
}
