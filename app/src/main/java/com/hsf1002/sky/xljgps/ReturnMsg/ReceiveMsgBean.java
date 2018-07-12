package com.hsf1002.sky.xljgps.ReturnMsg;

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

    //private static final String JSON_ID = "id";
    private static final String JSON_SUCCESS = "success";
    private static final String JSON_MESSAGE = "message";
    private static final String JSON_RELATIONSHIP = "relationship";
    private static final String JSON_PHONE = "phone";*/

    //private static final String JSON_DATA = "data";
    //private static final String JSON_SIGN = "sign";
    //private static final String JSON_COMPANY = "company";

    private List<String> relationship;
    private List<String> phone;


    public List<String> getRelationship() {
        return relationship;
    }

    public List<String> getPhone() {
        return phone;
    }

/*
    public ReceiveMsgBean(Gson gson ) throws JSONException{
        relationship = gson.fromJson(JSON_RELATIONSHIP, new TypeToken<List<String>>(){}.getType());
        phone = gson.fromJson(JSON_PHONE, new TypeToken<List<String>>(){}.getType());
    }

    public Gson ReceiveMsgToGson()
    {
        Gson gson = null;
        GsonBuilder gsonBuilder = new GsonBuilder();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(JSON_RELATIONSHIP, relationship);
        hashMap.put(JSON_PHONE, phone);

        gson = gsonBuilder.serializeNulls().create();
        gson.toJson(hashMap);

        return gson;
    }*/
}
