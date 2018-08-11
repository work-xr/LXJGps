package com.hsf1002.sky.xljgps.result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_COMPANY;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_DOWNLOAD;

/**
 * Created by hefeng on 18-8-8.
 */

public class ResultServerDownloadNumberMsg extends ResultServerMsg{
    private String imei;
    private String company;
    private int type;       // 103
    private String time;

    public ResultServerDownloadNumberMsg() {
        this.imei = SprdCommonUtils.getInstance().getIMEI();
        this.company = RXJAVAHTTP_COMPANY;
        this.time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        this.type = RXJAVAHTTP_TYPE_DOWNLOAD;
    }

    public static String getResultServerDownloadNumberMsgGson(ResultServerDownloadNumberMsg param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, ResultServerDownloadNumberMsg.class);
    }
}
