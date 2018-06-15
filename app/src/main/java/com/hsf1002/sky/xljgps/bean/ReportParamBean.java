package com.hsf1002.sky.xljgps.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by hefeng on 18-6-15.
 */

public class ReportParamBean extends BaiduGpsMsgBean{
    private String imei;
    private String company;
    private String type;
    private String time;
    private String power;

    public ReportParamBean(String imei, String company, String type, String position_type, String time, String loc_type, String longitude, String lantitude, String power) {
        super(position_type, loc_type, longitude, lantitude);

        this.imei = imei;
        this.company = company;
        this.type = type;
        this.time = time;
        this.power = power;
    }

    public static String getReportParamGson(ReportParamBean param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, ReportParamBean.class);
    }
}
