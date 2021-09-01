package com.bo.bonews.base.http.callback;

/**
 * @Description: 上传进度回调
 *
 * @date: 17/5/15 10:58.
 */
public interface UCallback {
    void onProgress(long currentLength, long totalLength, float percent);

    void onFail(int errCode, String errMsg);
}
