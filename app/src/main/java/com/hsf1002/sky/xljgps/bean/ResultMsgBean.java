package com.hsf1002.sky.xljgps.bean;

/**
 * Created by hefeng on 18-6-8.
 */

public class ResultMsgBean {
    protected int success;
    protected String message;

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "success = " + success + ", message = " + message;
    }
}
