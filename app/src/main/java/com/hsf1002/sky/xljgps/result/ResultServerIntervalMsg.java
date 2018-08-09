package com.hsf1002.sky.xljgps.result;

import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_COMPANY;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_INTERVAL;

/**
 * Created by hefeng on 18-8-8.
 */

public class ResultServerIntervalMsg {
    private String company;
    private String time;
    private int type;

    public ResultServerIntervalMsg() {
        this.company = RXJAVAHTTP_COMPANY;
        this.time = SprdCommonUtils.getInstance().getFormatCurrentTime();
        this.type = RXJAVAHTTP_TYPE_INTERVAL;
    }
}
