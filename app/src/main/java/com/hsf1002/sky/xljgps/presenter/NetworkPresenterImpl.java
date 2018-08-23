package com.hsf1002.sky.xljgps.presenter;

import android.content.Context;
import android.widget.Toast;

import com.hsf1002.sky.xljgps.R;
import com.hsf1002.sky.xljgps.app.GpsApplication;
import com.hsf1002.sky.xljgps.model.BaseModel;
import com.hsf1002.sky.xljgps.model.SocketModel;
import com.hsf1002.sky.xljgps.result.ResultMsg;
import com.hsf1002.sky.xljgps.service.SocketService;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;
import com.hsf1002.sky.xljgps.view.BaseView;

import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_TIMING;

/**
 * Created by hefeng on 18-6-8.
 * desc: 不管Rxjava HTTP还是Socket TCP都共用此类
 */

public class NetworkPresenterImpl implements
                                                NetworkPresenter.OnUploadListener,
                                                NetworkPresenter.OnReportListener
{
    private BaseView view;
    //private BaseModel model = new RxjavaHttpModel();
    private BaseModel model = new SocketModel();
    private Context context = null;
    private static int count = 0;

    public NetworkPresenterImpl(BaseView baseView)
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
        model.reportPosition(SOCKET_TYPE_TIMING, this);
        context = GpsApplication.getAppContext();
        Toast.makeText(context, context.getResources().getString(R.string.report_success), Toast.LENGTH_SHORT).show();
/*
        switch (count) {
            case 0:
                SprdCommonUtils.getInstance().sendSosSmsBroadcast();
                break;
            case 1:
                SocketService.getInstance().parseServerMsg("{\"imei\":\"867400020316620\",\"time\":\"20180810155626\",\"command\":101}");
                break;
            case 2:
                SocketService.getInstance().parseServerMsg("{\"interval\":\"3600\",\"command\":106,\"time\":\"20170102302022\"}");
                break;
            case 3:
                SocketService.getInstance().parseServerMsg("{\"imei \":\"869938027477745\",\"command\":107,\"time\":\"20170102302022\"}");
                break;
            case 4:
                SocketService.getInstance().parseServerMsg("{\"imei\":\"869938027477745\",\"command\":108,\"time\":\"20170102302022\"}");
                break;
            case 5:
                SocketService.getInstance().parseServerMsg("{\"sos_phone\":\"10086,12345,19968867878,059212349\",\"name\":\"亲1,亲2,亲3,养老服务中心号码\",\"imei\":\"867400020316620\",\"time\":\"20180811123608\",\"command\":103}");
                break;
            default:
                break;
        }
        count++;

        if (count == 6)
        {
            count = 0;
        }
*/
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
