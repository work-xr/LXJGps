package com.hsf1002.sky.xljgps.result;

/**
 * Created by hefeng on 18-7-30.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import static com.hsf1002.sky.xljgps.util.Constant.RESULT_MSG_FAILING;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_STATUS_POWEROFF;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_GET_STATUS_INFO;

/**
*  author:  hefeng
*  created: 18-7-30 下午6:50
*  desc: 这是从服务器端接收到的指令参数, 根据平台发送的指令把设备状态上报到平台
 *
*/
public class ResultServerStatusInfoMsg extends ResultServerMsg{
    private String message;         // success or failing
    private String imei;
    private int status;             // 1代表开机、0代表关机
    private int power;
    private String time;
    private int type;               // 108

    public ResultServerStatusInfoMsg() {
        this.message = RESULT_MSG_FAILING;
        this.imei = SprdCommonUtils.getInstance().getIMEI();
        this.status = RESULT_STATUS_POWEROFF;
        this.power = SprdCommonUtils.getInstance().getCurrentBatteryCapacity();
        this.time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        this.type = SOCKET_TYPE_GET_STATUS_INFO;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static String getResultServerStatusInfoMsgGson(ResultServerStatusInfoMsg param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, ResultServerStatusInfoMsg.class);
    }

    @Override
    public String toString() {
        return "ResultServerStatusInfoMsg{" +
                ", imei='" + imei + '\'' +
                ", status='" + status + '\'' +
                ", power='" + power + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
