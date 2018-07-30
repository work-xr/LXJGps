package com.hsf1002.sky.xljgps.params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by hefeng on 18-7-30.
 */

public class ModifyIntervalParam {
    private String interval;
    private String time;
    private String type;

    public ModifyIntervalParam(String interval, String time, String type) {
        this.interval = interval;
        this.time = time;
        this.type = type;
    }

    public static String getReportParamGson(ModifyIntervalParam param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, ModifyIntervalParam.class);
    }
}
