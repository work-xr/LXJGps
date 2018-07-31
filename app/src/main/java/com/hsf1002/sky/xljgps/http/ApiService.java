package com.hsf1002.sky.xljgps.http;

import com.hsf1002.sky.xljgps.result.RelationNumberMsg;
import com.hsf1002.sky.xljgps.result.ResultMsg;
import com.hsf1002.sky.xljgps.result.StatusInfoMsg;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_DOWNLOAD_FROM_PLATFORM;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_REPORT_POSITION;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_REPORT_SOSPOSITION;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_UPDATE_TO_PLATFORM;

/**
 * Created by hefeng on 18-6-8.
 */

public interface ApiService {
    @POST(RXJAVAHTTP_REPORT_SOSPOSITION)
    @FormUrlEncoded
    Observable<ResultMsg> reportSosPosition(/*
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
            @Field("company") String company,
            @Field("data") String data,
            @Field("sign") String sign
    );

    @POST(RXJAVAHTTP_UPDATE_TO_PLATFORM)
    @FormUrlEncoded
    Observable<ResultMsg> uploadRelationNumber(/*
            @Field("imei") String imei,
            @Field("company") String company,
            @Field("type") String type,
            @Field("sos_phone") String sosPhone,
            @Field("name") String name,
            @Field("time") String time,*/
            @Field("company") String company,
            @Field("data") String data,
            @Field("sign") String sign
    );

    @GET(RXJAVAHTTP_DOWNLOAD_FROM_PLATFORM)
    Observable<ResultMsg<RelationNumberMsg>> downloadRelationNumber(/*
            @Field("imei") String imei,
            @Field("company") String company,
            @Field("type") String type,
            @Field("time") String time,*/
            @Field("company") String company,
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

    @POST
    @FormUrlEncoded
    Observable<ResultMsg> reportModifyInterval(
            @Field("company") String company,
            @Field("data") String data,
            @Field("sign") String sign
    );

    @POST
    @FormUrlEncoded
    Observable<ResultMsg> notifyOuterElectricBar(
            @Field("company") String company,
            @Field("data") String data,
            @Field("sign") String sign
    );

    @POST
    @FormUrlEncoded
    Observable<ResultMsg<StatusInfoMsg>> getStatusInfo(
            @Field("company") String company,
            @Field("data") String data,
            @Field("sign") String sign
    );
}
