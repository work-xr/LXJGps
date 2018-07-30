package com.hsf1002.sky.xljgps.params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by hefeng on 18-7-30.
 */

public class OuterElectricBarParam {
    protected String imei;
    protected String time;
    protected String type;

    public OuterElectricBarParam(String imei, String time, String type) {
        this.imei = imei;
        this.time = time;
        this.type = type;
    }

    public static String getReportParamGson(OuterElectricBarParam param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, OuterElectricBarParam.class);
    }
}
