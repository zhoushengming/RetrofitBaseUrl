package com.ericcode.retrofitbaseurl.api;

import android.database.Observable;

import com.ericcode.BaseUrl;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static com.ericcode.retrofitbaseurl.api.Api.APP_GANK_DOMAIN;

/**
 * Created by jess on 19/07/2017 11:50
 * Contact with jess.yan.effort@gmail.com
 */

public interface TwoApiService {
    @BaseUrl(APP_GANK_DOMAIN)
    @GET("/api/data/Android/{size}/{page}")// 可以通过在注解里给全路径达到使用不同的 BaseUrl ,但是这样无法在 App 运行时动态切换 BaseUrl
    Observable<ResponseBody> getData(@Path("size") int size, @Path("page") int page);
}
