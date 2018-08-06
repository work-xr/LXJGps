package com.hsf1002.sky.xljgps.result;

/**
 * Created by hefeng on 18-7-30.
 */

import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import static com.hsf1002.sky.xljgps.util.Constant.RESULT_STATUS_POWEROFF;

/**
*  author:  hefeng
*  created: 18-7-30 下午6:50
*  desc: 根据平台发送的指令把设备状态上报到平台
 *
*/
public class StatusInfoSendMsg {
    private String imei;
    private String status;      // 1代表开机、0代表关机
    private String power;
    private String time;

    public StatusInfoSendMsg() {
        this.imei = SprdCommonUtils.getInstance().getIMEI();
        this.status = RESULT_STATUS_POWEROFF;
        this.power = SprdCommonUtils.getInstance().getCurrentBatteryCapacity();
        this.time = SprdCommonUtils.getInstance().getFormatCurrentTime();
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "StatusInfoSendMsg{" +
                ", imei='" + imei + '\'' +
                ", status='" + status + '\'' +
                ", power='" + power + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
