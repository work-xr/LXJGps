package com.hsf1002.sky.xljgps.params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by hefeng on 18-8-8.
 */

public class BeatHeartParam {
    private String imei;
    private String company;
    private int type;           // 301

    public BeatHeartParam(String imei, String company, int type) {
        this.imei = imei;
        this.company = company;
        this.type = type;
    }

    public static String getBeatHeartParamGson(BeatHeartParam param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, BeatHeartParam.class);
    }

    @Override
    public String toString() {
        return "BeatHeartParam{" +
                "imei='" + imei + '\'' +
                ", company='" + company + '\'' +
                ", type=" + type +
                '}';
    }
}
