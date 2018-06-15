package com.hsf1002.sky.xljgps.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by hefeng on 18-6-15.
 */

public class ReceiveParamBean {
    private static final String JSON_IMEI = "imei";
    private static final String JSON_COMPANY = "company";
    private static final String JSON_TYPE = "type";
    private static final String JSON_TIME = "time";

    private String imei;
    private String company;
    private String type;
    private String time;

    public ReceiveParamBean(String imei, String company, String type, String time) {
        this.imei = imei;
        this.company = company;
        this.type = type;
        this.time = time;
    }

    public static String getReceiveParamGson(ReceiveParamBean param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, ReceiveParamBean.class);
    }
}
