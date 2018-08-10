package com.hsf1002.sky.xljgps.http;

import com.hsf1002.sky.xljgps.result.ResultMsg;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_REPORT_POSITION;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_REPORT_SOSPOSITION;
import static com.hsf1002.sky.xljgps.util.Constant.RXJAVAHTTP_UPDATE_TO_PLATFORM;

/**
 * Created by hefeng on 18-6-8.
 */

/**
*  author:  hefeng
*  created: 18-8-10 上午8:45
*  desc:    改为socket通信后,该文件没用了
*/
@Deprecated
public interface ApiService {
    @POST(RXJAVAHTTP_REPORT_SOSPOSITION)
    @FormUrlEncoded
    Observable<ResultMsg> reportSosPosition(
            @Field("company") String company,
            @Field("data") String data,
            @Field("sign") String sign
    );

    @POST(RXJAVAHTTP_UPDATE_TO_PLATFORM)
    @FormUrlEncoded
    Observable<ResultMsg> uploadRelationNumber(
            @Field("company") String company,
            @Field("data") String data,
            @Field("sign") String sign
    );


    @POST(RXJAVAHTTP_REPORT_POSITION)
    @FormUrlEncoded
    Observable<ResultMsg> reportPosition(
            @Field("company") String company,
            @Field("data") String data,
            @Field("sign") String sign
    );
}
