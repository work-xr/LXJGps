package com.hsf1002.sky.xljgps.bean;

/**
 * Created by hefeng on 18-6-7.
 */

public class ReportMsg {
    private String imei;
    private String company;
    private String type;
    private String time;
    private String position_type;
    private String loc_type;
    private String lon;
    private String lat;
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

    public String getPosition_type() {
        return position_type;
    }

    public String getLoc_type() {
        return loc_type;
    }

    public String getLon() {
        return lon;
    }

    public String getLat() {
        return lat;
    }

    public RetMsg getRetMsg() {
        return retMsg;
    }
}
