package com.bo.bonews.utils;

import android.content.Context;

/**
 */
public class DensityUtil {
    public static int dip2px(Context c, float dpValue) {
        final float scale = c.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
