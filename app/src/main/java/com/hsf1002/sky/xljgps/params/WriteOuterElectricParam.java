package com.hsf1002.sky.xljgps.params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by hefeng on 18-7-30.
 */

public class WriteOuterElectricParam {
    protected String imei;
    protected String time;
    protected int type;      // 107
    protected int success;


    public WriteOuterElectricParam(String imei, String time, int type, int success) {
        this.imei = imei;
        this.time = time;
        this.type = type;
        this.success = success;
    }

    public static String getReportParamGson(WriteOuterElectricParam param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, WriteOuterElectricParam.class);
    }
}
