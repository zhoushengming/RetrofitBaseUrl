package com.ericcode.retrofitbaseurl.api;

import android.database.Observable;

import com.ericcode.BaseUrl;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static com.ericcode.retrofitbaseurl.api.Api.APP_DOUBAN_DOMAIN;

/**
 * Created by jess on 19/07/2017 11:50
 * Contact with jess.yan.effort@gmail.com
 */

public interface ThreeApiService {
    @BaseUrl(APP_DOUBAN_DOMAIN)
    @GET("/v2/book/{id}")// 可以通过在注解里给全路径达到使用不同的 BaseUrl ,但是这样无法在 App 运行时动态切换 BaseUrl
    Observable<ResponseBody> getBook(@Path("id") int id);
}
