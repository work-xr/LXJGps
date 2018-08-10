package com.hsf1002.sky.xljgps.result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by hefeng on 18-8-9.
 */

public class ResultServerMsg<T> {
    private int success;
    private T data;

    public void setSuccess(int success) {
        this.success = success;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static String getResultServerMsgGson(ResultServerMsg param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, ResultServerMsg.class);
    }
}
