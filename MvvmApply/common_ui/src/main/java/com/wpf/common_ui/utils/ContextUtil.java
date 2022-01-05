package com.wpf.common_ui.utils;

import android.content.Context;

/**
 * Author: feipeng.wang
 * Time:   2021/7/7
 * Description : This is description.
 */
public class ContextUtil {
    private static Context c;
    public static void initContext(Context context){
        c = context;
    }
    public static Context getContext() {
        return c;
    }
}
