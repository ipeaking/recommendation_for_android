package com.bo.bonews.base.http.core;

import com.bo.bonews.base.http.config.HttpGlobalConfig;
import com.bo.bonews.base.http.func.ApiRetryFunc;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @Description: 转换器
 * @date: 17/7/22 22:11.
 */
public class ApiTransformer {

    public static <T> ObservableTransformer<T, T> norTransformer() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retryWhen(new ApiRetryFunc(HttpGlobalConfig.getInstance().getRetryCount(),
                                HttpGlobalConfig.getInstance().getRetryDelayMillis()));
            }
        };
    }

    public static <T> ObservableTransformer<T, T> norTransformer(final int retryCount, final int retryDelayMillis) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retryWhen(new ApiRetryFunc(retryCount, retryDelayMillis));
            }
        };
    }

}
