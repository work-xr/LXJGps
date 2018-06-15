package com.hsf1002.sky.xljgps.bean;

import com.google.gson.Gson;

/**
 * Created by hefeng on 18-6-15.
 */

public class SendParamBean {

    private String imei;
    private String company;
    private String type;
    private String sos_phone;
    private String name;
    private String time;

    public SendParamBean(String imei, String company, String type, String sos_phone, String name, String time) {
        this.imei = imei;
        this.company = company;
        this.type = type;
        this.sos_phone = sos_phone;
        this.name = name;
        this.time = time;
    }

    public static String getSendParamGson(SendParamBean param)
    {
        Gson gson = new Gson();

        return gson.toJson(param, SendParamBean.class);
    }
}