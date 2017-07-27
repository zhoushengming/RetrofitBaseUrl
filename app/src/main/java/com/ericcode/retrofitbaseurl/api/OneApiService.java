package com.ericcode.retrofitbaseurl.api;


import com.ericcode.BaseUrl;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.ericcode.retrofitbaseurl.api.Api.APP_GETHUB_DOMAIN;

/**
 * Created by jess on 19/07/2017 11:49
 * Contact with jess.yan.effort@gmail.com
 */

public interface OneApiService {
    @BaseUrl(APP_GETHUB_DOMAIN)
    @GET("/users") // 可以通过在注解里给全路径达到使用不同的 BaseUrl ,但是这样无法在 App 运行时动态切换 BaseUrl
    Observable<ResponseBody> getUsers(@Query("since") int lastIdQueried, @Query("per_page") int perPage);

    // 切换 Url 的优先级: DomainHeader 中的将覆盖全局的 BaseUrl
    // 这里不配置 DomainHeader，将只受到设置的全局 BaseUrl 的影响,没有全局 BaseUrl 将请求原始的 BaseUrl
    // 当你项目中只有一个 BaseUrl ,但需要动态改变,全局 BaseUrl 显得非常方便
    @GET("/okhttp-progress")// 这里随便写一个，just for test
    Observable<ResponseBody> requestDefault();
}
