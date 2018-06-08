package com.hsf1002.sky.xljgps.bean;

import java.util.List;

/**
 * Created by hefeng on 18-6-7.
 */

public class ReceiveMsg {
    private String imei;
    private String company;
    private String type;
    private String time;
    private RetMsg retMsg;

/*
    成功：
    {"success": 1,"message": "SUCCEED","relationship": "亲情号码1，亲情号码2，亲情号码3","phone": "10086,1008611,10010"}
    失败：
    { "success ": 0, " message": "failing"}
*/
    class RetMsg
    {
        private int success;
        private String message;
        List<String> relationship;
        List<String> phone;

        public int getSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<String> getRelationship() {
            return relationship;
        }

        public List<String> getPhone() {
            return phone;
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

    public RetMsg getRetMsg() {
        return retMsg;
    }
}
