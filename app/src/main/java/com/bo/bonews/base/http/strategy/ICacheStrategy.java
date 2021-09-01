package com.bo.bonews.base.http.strategy;

import com.bo.bonews.base.http.core.ApiCache;
import com.bo.bonews.base.http.mode.CacheResult;

import java.lang.reflect.Type;

import io.reactivex.Observable;

/**
 * @Description: 缓存策略接口
 *
 * @date: 16/12/31 14:21.
 */
public interface ICacheStrategy<T> {
    <T> Observable<CacheResult<T>> execute(ApiCache apiCache, String cacheKey, Observable<T> source, Type type);
}
