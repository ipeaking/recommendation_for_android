package com.bo.bonews.base.common;

import com.google.gson.Gson;

/**
 * @Description: Gson单例操作
 *
 * @date: 17/1/7 19:20.
 */
public class GsonUtil {
    private static Gson gson;

    public static Gson gson() {
        if (gson == null) {
            synchronized (Gson.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }
}
