package com.hsf1002.sky.xljgps.params;

import com.google.gson.Gson;

/**
 * Created by hefeng on 18-6-15.
 * desc: 不管Rxjava HTTP还是Socket TCP都共用此类, 其中type的类型不一样
 */

public class UploadNumberParam {
    private String imei;
    //private String manufactory;
    //private String model;
    private String company;
    private int type;               // 203
    private String sos_phone;
    private String name;
    private String time;

    public UploadNumberParam(String imei, /*String manufactory, String model,*/ String company, int type, String sos_phone, String name, String time) {
        this.imei = imei;
        //this.manufactory = manufactory;
        //this.model = model;
        this.company = company;
        this.type = type;
        this.sos_phone = sos_phone;
        this.name = name;
        this.time = time;
    }

    public static String getSendParamGson(UploadNumberParam param)
    {
        Gson gson = new Gson();

        return gson.toJson(param, UploadNumberParam.class);
    }
}
