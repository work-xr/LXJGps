package com.hsf1002.sky.xljgps.ReturnMsg;

/**
 * Created by hefeng on 18-6-8.
 */

public class ResultMsg<T> {
    protected int success;
    protected T message;

    public int getSuccess() {
        return success;
    }

    public T getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "success = " + success + ", message = " + message;
    }
}
