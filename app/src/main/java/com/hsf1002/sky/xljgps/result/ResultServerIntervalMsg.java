package com.hsf1002.sky.xljgps.result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_COMPANY;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_INTERVAL;

/**
 * Created by hefeng on 18-8-8.
 */

public class ResultServerIntervalMsg extends ResultServerMsg {
    private String company;
    private String time;
    private int type;

    public ResultServerIntervalMsg() {
        this.company = SOCKET_COMPANY;
        this.time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        this.type = SOCKET_TYPE_INTERVAL;
    }

    public static String getResultServerIntervalMsgGson(ResultServerIntervalMsg param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, ResultServerIntervalMsg.class);
    }
}
