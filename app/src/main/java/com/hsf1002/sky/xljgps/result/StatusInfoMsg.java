package com.hsf1002.sky.xljgps.result;

/**
 * Created by hefeng on 18-7-30.
 */

/**
*  author:  hefeng
*  created: 18-7-30 下午6:50
*  desc: 向平台请求设备状态的返回结果
*/
public class StatusInfoMsg {
    private String imei;
    private String status;      // 1代表开机、0代表关机
    private String power;
    private String time;

    public String getImei() {
        return imei;
    }

    public String getStatus() {
        return status;
    }

    public String getPower() {
        return power;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "StatusInfoMsg{" +
                "imei='" + imei + '\'' +
                ", status='" + status + '\'' +
                ", power='" + power + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}