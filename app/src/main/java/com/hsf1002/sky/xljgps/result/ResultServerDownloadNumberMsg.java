package com.hsf1002.sky.xljgps.result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_COMPANY;
import static com.hsf1002.sky.xljgps.util.Constant.SOCKET_TYPE_DOWNLOAD;

/**
 * Created by hefeng on 18-8-8.
 * desc: 这是从服务器端接收到的指令参数, 根据平台发送的指令把从服务器读到的亲情号码, 设置到本地(覆盖本地三个紧急呼叫号码)
 */

public class ResultServerDownloadNumberMsg extends ResultServerMsg{
    private String imei;
    private String company;
    private int type;       // 103
    private String time;

    public ResultServerDownloadNumberMsg() {
        this.imei = SprdCommonUtils.getInstance().getIMEI();
        this.company = SOCKET_COMPANY;
        this.time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        this.type = SOCKET_TYPE_DOWNLOAD;
    }

    public static String getResultServerDownloadNumberMsgGson(ResultServerDownloadNumberMsg param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, ResultServerDownloadNumberMsg.class);
    }
}
