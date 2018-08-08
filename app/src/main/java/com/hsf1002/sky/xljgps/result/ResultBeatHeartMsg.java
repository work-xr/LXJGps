package com.hsf1002.sky.xljgps.result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hsf1002.sky.xljgps.params.BeatHeartParam;

/**
 * Created by hefeng on 18-8-8.
 */

public class ResultBeatHeartMsg {
    private int success;        // 0 fail, 1 success
    private int command;        // 301
    private String company;

    public ResultBeatHeartMsg(int success, int command, String company) {
        this.success = success;
        this.command = command;
        this.company = company;
    }

    public static String getResultBeatHeartMsgGson(ResultBeatHeartMsg param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, ResultBeatHeartMsg.class);
    }

    @Override
    public String toString() {
        return "ResultBeatHeartMsg{" +
                "success=" + success +
                ", command=" + command +
                ", company='" + company + '\'' +
                '}';
    }
}
