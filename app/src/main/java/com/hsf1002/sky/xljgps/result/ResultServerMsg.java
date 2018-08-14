package com.hsf1002.sky.xljgps.result;

import static com.hsf1002.sky.xljgps.util.Constant.RESULT_SUCCESS_0;

/**
 * Created by hefeng on 18-8-9.
 * desc: 这是从服务器端接收到的指令参数, 每个指令的参数都不一样, 没有做到统一
 */

public class ResultServerMsg{
    protected int success;

    public ResultServerMsg() {
        this.success = RESULT_SUCCESS_0;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
