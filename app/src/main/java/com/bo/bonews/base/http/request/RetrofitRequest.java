package com.bo.bonews.base.http.request;

/**
 * @Description: 传入自定义Retrofit接口的请求类型
 *
 * @date: 17/7/22 15:11.
 */
public class RetrofitRequest extends BaseRequest<RetrofitRequest> {

    public RetrofitRequest() {

    }

    public <T> T create(Class<T> cls) {
        generateGlobalConfig();
        generateLocalConfig();
        return retrofit.create(cls);
    }

}
