/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.kxy.deerthrift;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.tencent.bugly.Bugly;


/**
 * Created by penglei on 2018/12/25.
 */
public class App extends Application {

    public static final String APP_BUGLY_ID = "6b97539496"; // bugly上注册的appid
    private static final String TAG = "App";

    private static App appInstance;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);
        mContext = getApplicationContext();
        appInstance = this;

        // 腾讯bugly
        initBugly();
    }

    public static Context getContext() {
        return mContext;
    }

    public static App getAppInstance(){
        return appInstance;
    }


    public static App getIntstance() {
        if (appInstance == null) {
            appInstance = new App();
        }
        return appInstance;
    }

    /**
     * 初始化bugly
     */
    private void initBugly() {
        // 这里实现SDK初始化，appid换成自己在Bugly上申请的APPID；调试中将第三个参数改为true
        Bugly.init(getApplicationContext(), APP_BUGLY_ID, true);
    }

}
