package com.bo.bonews.utils;

import android.content.Context;
import android.util.Log;

import com.bo.bonews.base.cache.SpCache;

public class LoginUtils {

    private static boolean bLoginStatus = false;

    private static long userId = -1;

    private static Context mContext;

    private static SpCache spCache = null;

    public static void initLogin(Context context) {
        spCache = new SpCache(context);
        mContext = context;
        bLoginStatus = spCache.get("app_login_status", false);
        userId = spCache.get("app_login_user_id", -1);
    }

    public static boolean getLoginStatus() {
        return bLoginStatus;
    }

    public static long getUserId() {
        return userId;
    }

    public static void setUserId(int id) {
        userId = id;
        spCache.put("app_login_user_id", id);
    }

    public static void setLoginStatus(boolean login) {
        bLoginStatus = login;
        spCache.put("app_login_status", login);
    }


}
