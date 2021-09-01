package com.bo.bonews.base.http.core;

import android.content.Context;

import com.bo.bonews.base.http.mode.CookiesStore;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * @Description: Cookie
 * @date: 16/12/31 21:05.
 */
public class ApiCookie implements CookieJar {

    private CookiesStore cookieStore;

    public ApiCookie(Context context) {
        if (cookieStore == null) {
            cookieStore = new CookiesStore(context);
        }
    }

    @Override
    public void saveFromResponse( HttpUrl url,  List<Cookie> cookies) {
        if (cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest( HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }

}
