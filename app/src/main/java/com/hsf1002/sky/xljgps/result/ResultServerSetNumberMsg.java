package com.hsf1002.sky.xljgps.result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import static com.hsf1002.sky.xljgps.util.Constant.RESULT_MSG_FAILING;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_STATUS_POWEROFF;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_COMPANY;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_DOWNLOAD;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_GET_STATUS_INFO;

/**
 * Created by hefeng on 18-8-8.
 */

public class ResultServerSetNumberMsg {
    private String imei;
    private String company;
    private int type;       // 103
    private String time;

    public ResultServerSetNumberMsg() {
        this.imei = SprdCommonUtils.getInstance().getIMEI();
        this.company = RXJAVAHTTP_COMPANY;
        this.time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        this.type = RXJAVAHTTP_TYPE_DOWNLOAD;
    }
}
