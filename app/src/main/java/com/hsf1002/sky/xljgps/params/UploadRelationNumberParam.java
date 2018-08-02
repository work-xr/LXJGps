package com.hsf1002.sky.xljgps.params;

import com.google.gson.Gson;

/**
 * Created by hefeng on 18-6-15.
 */

public class UploadRelationNumberParam {
    private String imei;
    //private String manufactory;
    //private String model;
    private String company;
    private String type;
    private String sos_phone;
    private String name;
    private String time;

    public UploadRelationNumberParam(String imei, /*String manufactory, String model,*/ String company, String type, String sos_phone, String name, String time) {
        this.imei = imei;
        //this.manufactory = manufactory;
        //this.model = model;
        this.company = company;
        this.type = type;
        this.sos_phone = sos_phone;
        this.name = name;
        this.time = time;
    }

    public static String getSendParamGson(UploadRelationNumberParam param)
    {
        Gson gson = new Gson();

        return gson.toJson(param, UploadRelationNumberParam.class);
    }
}
