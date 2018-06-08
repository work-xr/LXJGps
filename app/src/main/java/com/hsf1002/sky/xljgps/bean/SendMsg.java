package com.hsf1002.sky.xljgps.bean;

/**
 * Created by hefeng on 18-6-7.
 */

public class SendMsg {
    private String imei;
    private String company;
    private String type;
    private String time;
    private String sos_phone;
    private String name;
    private RetMsg retMsg;
    /*
        成功：
        {"success": 1,"message": "SUCCEED"}
        失败：
        { "success ": 0, " message": "failing"}
    */
    class RetMsg
    {
        private int success;
        private String message;

        public int getSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    public String getImei() {
        return imei;
    }

    public String getCompany() {
        return company;
    }

    public String getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public String getSos_phone() {
        return sos_phone;
    }

    public String getName() {
        return name;
    }

    public RetMsg getRetMsg() {
        return retMsg;
    }
}
