package com.bo.bonews.base.http.interceptor;



import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description: 无缓存拦截
 * @date: 16/12/31 21:17.
 */
public class NoCacheInterceptor implements Interceptor {

    @Override
    public Response intercept( Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder().header("Cache-Control", "no-cache").build();
        Response originalResponse = chain.proceed(request);
        originalResponse = originalResponse.newBuilder().header("Cache-Control", "no-cache").build();
        return originalResponse;
    }
}
