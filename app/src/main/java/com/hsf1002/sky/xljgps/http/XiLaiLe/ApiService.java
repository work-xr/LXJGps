package com.hsf1002.sky.xljgps.http.XiLaiLe;


import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by hefeng on 18-6-8.
 */

public interface ApiService {
    // http://api.cloud.site4test.com/thirdparty/mobile/location/list?imei=864648030095476&start=2017-10-25 17:00:00&lv=1&app_id=bc7007b968877d0d3eec4caa77127c99a96aeb2b&app_secret=9ff96155b3f42dc7a337aa59ca59578b
    @GET("http://api.cloud.site4test.com/thirdparty/mobile/location/list?imei=864648030095476&start=2017-10-25 17:00:00&lv=1&app_id=bc7007b968877d0d3eec4caa77127c99a96aeb2b&app_secret=9ff96155b3f42dc7a337aa59ca59578b")
    Observable<String> getGpsInfo();
}
