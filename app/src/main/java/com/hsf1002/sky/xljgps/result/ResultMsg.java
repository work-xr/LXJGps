package com.hsf1002.sky.xljgps.result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.hsf1002.sky.xljgps.util.Constant.RESULT_MSG_FAILING;
import static com.hsf1002.sky.xljgps.util.Constant.RESULT_SUCCESS_0;

/**
 * Created by hefeng on 18-6-8.
 * desc: Rxjava HTTP方式访问服务器的返回值, 各个接口都统一用这个
 */

@Deprecated
public class ResultMsg<T> {
    protected int success;
    protected String message;
    protected T data;

    public ResultMsg() {
        this.success = RESULT_SUCCESS_0;
        this.message = RESULT_MSG_FAILING;
        this.data = null;
    }

    public static String getResultMsgGson(ResultMsg param)
    {
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.serializeNulls().create();

        return gson.toJson(param, ResultMsg.class);
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultMsg{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
