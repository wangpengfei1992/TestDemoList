package com.wpf.cbasestudy;

import android.app.Application;

import androidx.multidex.MultiDex;

/**
 * Author: feipeng.wang
 * Time:   2023/5/9
 * Description : This is description.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
