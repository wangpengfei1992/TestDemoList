//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not
// limited to reproduction, retransmission, communication, display, mirror, download,
// modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.anker.bluetoothtool.deviceExport.util;

import android.util.Log;

/**
 * Create by Arrietty on 2020/11/9
 */
public class LogDeviceE {
    private static boolean SHOW_LOG = true;
    private static final String TAG = "DeviceE";
    private LogDeviceE() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static void setShowLog(boolean debug) {
        SHOW_LOG = debug;
    }
    // below is default tag
    public static void i(String msg) {
        if (SHOW_LOG) {
            Log.i(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (SHOW_LOG) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (SHOW_LOG) {
            Log.e(TAG, msg);
        }
    }

    public static void v(String msg) {
        if (SHOW_LOG) {
            Log.v(TAG, msg);
        }
    }

    // below is custom tag
    public static void i(String tag, String msg) {
        if (SHOW_LOG) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (SHOW_LOG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (SHOW_LOG) {
            Log.e(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (SHOW_LOG) {
            Log.v(tag, msg);
        }
    }
}
