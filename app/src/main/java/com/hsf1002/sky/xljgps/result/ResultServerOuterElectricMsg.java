package com.hsf1002.sky.xljgps.result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hsf1002.sky.xljgps.util.SprdCommonUtils;

import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_TYPE_OUTER_ELECTRIC_BAR;

/**
 * Created by hefeng on 18-8-8.
 */

public class ResultServerOuterElectricMsg extends ResultServerMsg{
    private String imei;
    private String time;
    private int type;

    public ResultServerOuterElectricMsg() {
        this.imei = SprdCommonUtils.getInstance().getIMEI();
        this.time = SprdCommonUtils.getInstance().getFormatCurrentTime();;
        this.type = RXJAVAHTTP_TYPE_OUTER_ELECTRIC_BAR;
    }

    public static String getResultServerOuterElectricMsgGson(ResultServerOuterElectricMsg param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, ResultServerOuterElectricMsg.class);
    }
}
