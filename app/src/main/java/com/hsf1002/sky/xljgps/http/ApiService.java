package com.hsf1002.sky.xljgps.http;

import com.hsf1002.sky.xljgps.bean.BookBean;
import com.hsf1002.sky.xljgps.bean.ReceiveMsgBean;
import com.hsf1002.sky.xljgps.bean.ReportMsgBean;
import com.hsf1002.sky.xljgps.bean.SendMsgBean;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

import static com.hsf1002.sky.xljgps.util.Const.RXJAVAHTTP_DOWNLOAD_FROM_PLATFORM;
import static com.hsf1002.sky.xljgps.util.Const.RXJAVAHTTP_REPORT_SOSPOSITION;
import static com.hsf1002.sky.xljgps.util.Const.RXJAVAHTTP_UPDATE_TO_PLATFORM;

/**
 * Created by hefeng on 18-6-8.
 */

public interface ApiService {
    @GET("v2/book/1220562")
    Observable<BookBean> getBook();

    //http://healthdata.4000300659.com:8088/api/xiaobawangtest/sosPosition
    //http://healthdata.4000300659.com:8088/api/xiaobawang/sosPosition
    @POST(RXJAVAHTTP_REPORT_SOSPOSITION)
    @FormUrlEncoded
    Observable<ReportMsgBean> reportInfo(
            @Field("imei") String imei,
            @Field("company") String company,
            @Field("type") String type,
            @Field("position_type") String positionType,
            @Field("time") String time,
            @Field("loc_type") String locType,
            @Field("lon") String longitude,
            @Field("lat") String latitude,
            @Field("power") String power,
            @Field("data") String data,
            @Field("sign") String sign
    );

    //http://healthdata.4000300659.com:8088/api/xiaobawangtest/updatePhone
    //http://healthdata.4000300659.com:8088/api/xiaobawang/updatePhone
    @POST(RXJAVAHTTP_UPDATE_TO_PLATFORM)
    @FormUrlEncoded
    Observable<SendMsgBean> uploadInfo(
            @Field("imei") String imei,
            @Field("company") String company,
            @Field("type") String type,
            @Field("sos_phone") String sosPhone,
            @Field("name") String name,
            @Field("time") String time,
            @Field("data") String data,
            @Field("sign") String sign
    );

    //http://healthdata.4000300659.com:8088/api/xiaobawangtest/familyNumber
    //http://healthdata.4000300659.com:8088/api/xiaobawang/familyNumber
    @POST(RXJAVAHTTP_DOWNLOAD_FROM_PLATFORM)
    @FormUrlEncoded
    Observable<ReceiveMsgBean> downloadInfo(
            @Field("imei") String imei,
            @Field("company") String company,
            @Field("type") String type,
            @Field("time") String time,
            @Field("data") String data,
            @Field("sign") String sign
            );

    //http://healthdata.4000300659.com:8088/api/xiaobawangtest/position
    //http://healthdata.4000300659.com:8088/api/xiaobawang/position
    @POST(RXJAVAHTTP_REPORT_SOSPOSITION)
    @FormUrlEncoded
    Observable<ReportMsgBean> reportPosition(
            @Field("imei") String imei,
            @Field("company") String company,
            @Field("type") String type,
            @Field("position_type") String positionType,
            @Field("time") String time,
            @Field("loc_type") String locType,
            @Field("lon") String longitude,
            @Field("lat") String lantitude,
            @Field("power") String power,
            @Field("data") String data,
            @Field("sign") String sign
    );
}
