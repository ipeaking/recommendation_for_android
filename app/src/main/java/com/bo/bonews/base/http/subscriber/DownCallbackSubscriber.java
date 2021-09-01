package com.bo.bonews.base.http.subscriber;

import com.bo.bonews.base.http.callback.ACallback;

/**
 * @Description: 包含下载进度回调的订阅者
 *
 * @date: 17/6/7 23:45.
 */
public class DownCallbackSubscriber<T> extends ApiCallbackSubscriber<T> {
    public DownCallbackSubscriber(ACallback<T> callBack) {
        super(callBack);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        callBack.onSuccess(super.data);
    }
}
