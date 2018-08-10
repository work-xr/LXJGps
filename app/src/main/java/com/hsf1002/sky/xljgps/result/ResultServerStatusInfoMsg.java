package com.hsf1002.sky.xljgps.result;

/**
 * Created by hefeng on 18-7-30.
 */

import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import static com.hsf1002.sky.xljgps.util.Constant.RESULT_MSG_FAILING;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_STATUS_POWEROFF;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_GET_STATUS_INFO;

/**
*  author:  hefeng
*  created: 18-7-30 下午6:50
*  desc: 根据平台发送的指令把设备状态上报到平台
 *
*/
public class ResultServerStatusInfoMsg{
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
        this.type = RXJAVAHTTP_TYPE_GET_STATUS_INFO;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
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
