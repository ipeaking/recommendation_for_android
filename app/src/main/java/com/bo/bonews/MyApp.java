package com.bo.bonews;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.bo.bonews.base.http.ViseHttp;
import com.bo.bonews.base.http.interceptor.HttpLogInterceptor;
import com.bo.bonews.i.HttpConfig;
import com.bo.bonews.service.NetworkService;
import com.bo.bonews.utils.LoginUtils;
import com.bo.bonews.utils.ThreadManager;

import me.yokeyword.fragmentation.Fragmentation;

public class MyApp extends MultiDexApplication {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    private static Handler handler;

    private static ThreadManager.ThreadPool mThreadPool;
    private static int mainThreadId;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mainThreadId = android.os.Process.myTid();
        mContext = getApplicationContext();
        handler = new Handler();
        mThreadPool = ThreadManager.getThreadPool();
        // 网络监听服务
        NetworkService.enqueueWork(this);
        Fragmentation.builder()
                // 显示悬浮球 ; 其他Mode:SHAKE: 摇一摇唤出   NONE：隐藏
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(BuildConfig.DEBUG)
                .install();
        initNet();
        initLogin();
    }

    public void initLogin() {
        LoginUtils.initLogin(this);
    }

    public static Context getContext() {
        return mContext;
    }

    private void initNet() {
        ViseHttp.init(this);
        ViseHttp.CONFIG()
                //配置请求主机地址
                .baseUrl(HttpConfig.BASE_URL)
                .setCookie(true)
                //配置日志拦截器
                .interceptor(new HttpLogInterceptor()
                        .setLevel(HttpLogInterceptor.Level.BODY));

    }

    public static int getMainThreadId() {
        return mainThreadId;
    }

    public static Handler getHandler() {
        return handler;
    }


    public static ThreadManager.ThreadPool getThreadPool() {
        return mThreadPool;
    }
}
