package com.hsf1002.sky.xljgps.http;

import com.hsf1002.sky.xljgps.ReturnMsg.ResultMsg;
import com.hsf1002.sky.xljgps.params.BookBean;
import com.hsf1002.sky.xljgps.params.PersonBean;
import com.hsf1002.sky.xljgps.ReturnMsg.ReceiveMsgBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_DOWNLOAD_FROM_PLATFORM;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_GPS;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_PERSON_ID;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_PERSON_LIST;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_REPORT_POSITION;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_UPDATE_TO_PLATFORM;

/**
 * Created by hefeng on 18-6-8.
 */

public interface ApiService {
    @POST(RXJAVAHTTP_GPS)
    @FormUrlEncoded
    Observable<ResultMsg> reportInfo(/*
            @Field("imei") String imei,
            @Field("manufactory") String manufactory,
            @Field("model") String model,
            @Field("company") String company,
            @Field("type") String type,
            @Field("position_type") String positionType,
            @Field("time") String time,
            @Field("loc_type") String locType,
            @Field("longitude") String longitude,
            @Field("latitude") String latitude,
            @Field("power") String power,*/
            @Field("data") String data,
            @Field("sign") String sign
    );

    @PUT(RXJAVAHTTP_UPDATE_TO_PLATFORM)
    @FormUrlEncoded
    Observable<ResultMsg> uploadInfo(/*
            @Field("imei") String imei,
            @Field("company") String company,
            @Field("type") String type,
            @Field("sos_phone") String sosPhone,
            @Field("name") String name,
            @Field("time") String time,*/
            @Field("data") String data,
            @Field("sign") String sign
    );

    @GET(RXJAVAHTTP_DOWNLOAD_FROM_PLATFORM)
    Observable<ResultMsg<ReceiveMsgBean>> downloadInfo(/*
            @Field("imei") String imei,
            @Field("company") String company,
            @Field("type") String type,
            @Field("time") String time,*/
            @Query("data") String data,
            @Query("sign") String sign
            );

    @POST(RXJAVAHTTP_REPORT_POSITION)
    @FormUrlEncoded
    Observable<ResultMsg> reportPosition(
            @Field("company") String company,
            @Field("data") String data,
            @Field("sign") String sign
    );

    @GET(RXJAVAHTTP_PERSON_LIST)
    Observable<List<PersonBean>> getPersonList(
    );

    @GET(RXJAVAHTTP_PERSON_ID)
    Observable<PersonBean> getPersonById(
            @Query("id") Integer id
    );

    @POST(RXJAVAHTTP_PERSON_LIST)
    @FormUrlEncoded
    Observable<PersonBean> addPerson(
            @Field("name") String name,
            @Field("age") Integer age
    );
}
