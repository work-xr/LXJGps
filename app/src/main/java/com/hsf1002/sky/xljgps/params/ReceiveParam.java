package com.hsf1002.sky.xljgps.params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by hefeng on 18-6-15.
 */

public class ReceiveParam {
    private static final String JSON_IMEI = "imei";
    private static final String JSON_COMPANY = "company";
    private static final String JSON_TYPE = "type";
    private static final String JSON_TIME = "time";

    private String imei;
    private String manufactory;
    private String model;
    private String company;
    private String type;
    private String time;

    public ReceiveParam(String imei, String manufactory, String model, String company, String type, String time) {
        this.imei = imei;
        this.manufactory = manufactory;
        this.model = model;
        this.company = company;
        this.type = type;
        this.time = time;
    }

    public static String getReceiveParamGson(ReceiveParam param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, ReceiveParam.class);
    }
}
