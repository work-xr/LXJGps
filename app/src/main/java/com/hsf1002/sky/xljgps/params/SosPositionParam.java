package com.hsf1002.sky.xljgps.params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by hefeng on 18-6-15.
 */

public class SosPositionParam extends BaiduGpsParam {
    private String imei;
    //private String manufactory;
    //private String model;
    private String company;
    private int type;       // 202
    private String time;
    private int power;

    public SosPositionParam(String imei, /*String manufactory, String model, */String company, int type, String position_type, String time, String loc_type, String longitude, String lantitude, int power) {
        super(position_type, loc_type, longitude, lantitude);

        this.imei = imei;
        //this.manufactory = manufactory;
        //this.model = model;
        this.company = company;
        this.type = type;
        this.time = time;
        this.power = power;
    }

    public static String getReportParamGson(SosPositionParam param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, SosPositionParam.class);
    }
}
