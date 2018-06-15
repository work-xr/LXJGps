package com.hsf1002.sky.xljgps.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hefeng on 18-6-7.
 */

public class ReceiveMsgBean {

/*
    成功：
    {"success": 1,"message": "SUCCEED","relationship": "亲情号码1，亲情号码2，亲情号码3","phone": "10086,1008611,10010"}
    失败：
    { "success ": 0, " message": "failing"}
*/
    //private static final String JSON_ID = "id";
    private static final String JSON_SUCCESS = "success";
    private static final String JSON_MESSAGE = "message";
    private static final String JSON_RELATIONSHIP = "relationship";
    private static final String JSON_PHONE = "phone";

    //private static final String JSON_DATA = "data";
    //private static final String JSON_SIGN = "sign";
    //private static final String JSON_COMPANY = "company";

    private int success;
    private String message;
    List<String> relationship;
    List<String> phone;

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getRelationship() {
        return relationship;
    }

    public List<String> getPhone() {
        return phone;
    }


    public ReceiveMsgBean(Gson gson ) throws JSONException{
        success = Integer.valueOf(gson.fromJson(JSON_SUCCESS, String.class));
        message = gson.fromJson(JSON_MESSAGE, String.class);
        relationship = gson.fromJson(JSON_RELATIONSHIP, new TypeToken<List<String>>(){}.getType());
        phone = gson.fromJson(JSON_PHONE, new TypeToken<List<String>>(){}.getType());
    }

    public Gson ReceiveMsgToGson()
    {
        Gson gson = null;
        GsonBuilder gsonBuilder = new GsonBuilder();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(JSON_SUCCESS, success);
        hashMap.put(JSON_MESSAGE, message);
        hashMap.put(JSON_RELATIONSHIP, relationship);
        hashMap.put(JSON_PHONE, phone);

        gson = gsonBuilder.serializeNulls().create();
        gson.toJson(hashMap);

        return gson;
    }

    @Override
    public String toString() {
        String msg = "success = " + success + ", message = " + message;

        if (relationship != null && !relationship.isEmpty())
        {
            msg += ", relationship = " + relationship.toArray().toString();
        }

        if (phone != null && !phone.isEmpty())
        {
            msg += ", phone = " + phone.toArray().toString();
        }

        return  msg;
    }
}
