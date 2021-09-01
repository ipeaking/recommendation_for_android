package com.bo.bonews.base.http.mode;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.vise.log.ViseLog;
import com.vise.utils.convert.HexUtil;
import com.bo.bonews.base.common.ViseConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * @Description: Cookie仓库
 *
 * @date: 16/12/31 17:57.
 */
public class CookiesStore {
    private final Map<String, ConcurrentHashMap<String, Cookie>> cookies;
    private final SharedPreferences cookiePrefs;

    public CookiesStore(Context context) {
        cookiePrefs = context.getSharedPreferences(ViseConfig.COOKIE_PREFS, 0);
        cookies = new HashMap<>();
        Map<String, ?> prefsMap = cookiePrefs.getAll();
        for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            String[] cookieNames = TextUtils.split((String) entry.getValue(), ",");
            for (String name : cookieNames) {
                String encodedCookie = cookiePrefs.getString(name, null);
                if (encodedCookie != null) {
                    Cookie decodedCookie = decodeCookie(encodedCookie);
                    if (decodedCookie != null) {
                        if (!cookies.containsKey(entry.getKey())) {
                            cookies.put(entry.getKey(), new ConcurrentHashMap<String, Cookie>());
                        }
                        cookies.get(entry.getKey()).put(name, decodedCookie);
                    }
                }
            }
        }
    }

    private String getCookieToken(Cookie cookie) {
        return cookie.name() + "@" + cookie.domain();
    }

    public void add(HttpUrl url, Cookie cookie) {
        String name = getCookieToken(cookie);

        if (!cookies.containsKey(url.host())) {
            cookies.put(url.host(), new ConcurrentHashMap<String, Cookie>());
        }

        //将cookies缓存到内存中 如果缓存过期 就重置此cookie
        boolean hasExpired = cookie.persistent() && ((cookie.expiresAt() - System.currentTimeMillis()) < 0);
        if (hasExpired) {//如果过期
            if (cookies.containsKey(url.host())) {
                cookies.get(url.host()).remove(name);//删除以前的缓存
            }
        } else {//如果没过期
            cookies.get(url.host()).put(name, cookie);
        }

        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        if (cookie.persistent()) {//需要持久化
            //将cookies持久化到本地
            prefsWriter.putString(url.host(), TextUtils.join(",", cookies.get(url.host()).keySet()));
            prefsWriter.putString(name, encodeCookie(new OkHttpCookies(cookie)));
            prefsWriter.apply();
        } else {//不需要持久化
            prefsWriter.remove(url.host());
            prefsWriter.remove(name);
            prefsWriter.apply();
        }
    }

    public List<Cookie> get(HttpUrl url) {
        ArrayList<Cookie> ret = new ArrayList<>();
        if (cookies.containsKey(url.host())) ret.addAll(cookies.get(url.host()).values());
        return ret;
    }

    public boolean removeAll() {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
        cookies.clear();
        return true;
    }

    public boolean remove(HttpUrl url, Cookie cookie) {
        String name = getCookieToken(cookie);

        if (cookies.containsKey(url.host()) && cookies.get(url.host()).containsKey(name)) {
            cookies.get(url.host()).remove(name);

            SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
            if (cookiePrefs.contains(name)) {
                prefsWriter.remove(name);
            }
            prefsWriter.putString(url.host(), TextUtils.join(",", cookies.get(url.host()).keySet()));
            prefsWriter.apply();

            return true;
        } else {
            return false;
        }
    }

    public List<Cookie> getCookies() {
        ArrayList<Cookie> ret = new ArrayList<>();
        for (String key : cookies.keySet())
            ret.addAll(cookies.get(key).values());

        return ret;
    }

    private String encodeCookie(OkHttpCookies cookie) {
        if (cookie == null) return null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (IOException e) {
            ViseLog.d("IOException in encodeCookie" + e.getMessage());
            return null;
        }

        return HexUtil.encodeHexStr(os.toByteArray());
    }

    private Cookie decodeCookie(String cookieString) {
        byte[] bytes = HexUtil.decodeHex(cookieString.toCharArray());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((OkHttpCookies) objectInputStream.readObject()).getCookies();
        } catch (IOException e) {
            ViseLog.e("IOException in decodeCookie" + e.getMessage());
        } catch (ClassNotFoundException e) {
            ViseLog.e("ClassNotFoundException in decodeCookie" + e.getMessage());
        }

        return cookie;
    }
}
