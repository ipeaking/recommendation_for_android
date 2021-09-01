package com.bo.bonews.base.http.callback;

/**
 * @Description: 请求接口回调
 * @date: 17/5/15 10:54.
 */
public abstract class ACallback<T> {
    public abstract void onSuccess(T data);

    public abstract void onFail(int errCode, String errMsg);
}
