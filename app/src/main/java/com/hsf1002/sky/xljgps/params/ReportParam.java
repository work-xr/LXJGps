package com.hsf1002.sky.xljgps.params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by hefeng on 18-6-15.
 */

public class ReportParam extends BaiduGpsParam {
    private String imei;
    private String manufactory;
    private String model;
    private String company;
    private String type;
    private String time;
    private String power;

    public ReportParam(String imei, String manufactory, String model, String company, String type, String position_type, String time, String loc_type, String longitude, String lantitude, String power) {
        super(position_type, loc_type, longitude, lantitude);

        this.imei = imei;
        this.manufactory = manufactory;
        this.model = model;
        this.company = company;
        this.type = type;
        this.time = time;
        this.power = power;
    }

    public static String getReportParamGson(ReportParam param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, ReportParam.class);
    }
}
