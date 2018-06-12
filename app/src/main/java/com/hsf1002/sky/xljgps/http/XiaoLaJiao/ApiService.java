package com.hsf1002.sky.xljgps.http.XiaoLaJiao;

import com.hsf1002.sky.xljgps.bean.BookBean;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by hefeng on 18-6-8.
 */

public interface ApiService {
    @GET("v2/book/1220562")
    Observable<BookBean> getBook();
}
