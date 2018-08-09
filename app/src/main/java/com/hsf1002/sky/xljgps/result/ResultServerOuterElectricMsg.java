package com.hsf1002.sky.xljgps.result;

import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_OUTER_ELECTRIC_BAR;

/**
 * Created by hefeng on 18-8-8.
 */

public class ResultServerOuterElectricMsg {
    private String imei;
    private String time;
    private int type;

    public ResultServerOuterElectricMsg() {
        this.imei = SprdCommonUtils.getInstance().getIMEI();
        this.time = SprdCommonUtils.getInstance().getFormatCurrentTime();;
        this.type = RXJAVAHTTP_TYPE_OUTER_ELECTRIC_BAR;
    }
}
