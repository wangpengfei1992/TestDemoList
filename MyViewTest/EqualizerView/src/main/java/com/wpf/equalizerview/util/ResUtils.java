package com.wpf.equalizerview.util;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.StringRes;

import java.lang.reflect.Method;

public final class ResUtils {

    private static Context context;
    private static Resources resources;

    private ResUtils() {

    }

    private static Context getContext() {
        if (context == null) {
            synchronized (ResUtils.class) {
                if (context == null) {
                    try {
                        Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
                        Method method = ActivityThread.getMethod("currentActivityThread");
                        Object currentActivityThread = method.invoke(ActivityThread);
                        Method method2 = currentActivityThread.getClass().getMethod("getApplication");
                        context = (Context) method2.invoke(currentActivityThread);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return context;
    }

    private static Resources getResources() {
        if (resources == null) {
            synchronized (ResUtils.class) {
                if (resources == null) {
                    resources = getContext().getResources();
                }
            }
        }
        return resources;
    }

    public static float getDimension(@DimenRes int id) {
        return getResources().getDimension(id);
    }

    public static int getColor(@ColorRes int id) {
        return getResources().getColor(id, null);
    }

    public static String getString(@StringRes int id) {
        return getResources().getString(id);
    }

    public static int[] getIntArray(@ArrayRes int id) {
        return getResources().getIntArray(id);
    }
}
