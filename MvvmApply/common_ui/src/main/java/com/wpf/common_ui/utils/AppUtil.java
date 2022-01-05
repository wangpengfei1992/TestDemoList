//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.wpf.common_ui.utils;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;


import java.util.List;

/**
 * Created by ocean on 2017/12/27.
 */

public class AppUtil {

    private AppUtil() {
    }

    /**
     * get app versionName
     *
     * @return versionName
     */
    public static String getVersionName(Context context) {

        String versionName = null;
        PackageManager packageManager = context.getPackageManager();

        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (Exception e) {
            versionName = "";
        }
        return versionName;
    }

    /**
     * get app versionName
     *
     * @return versionName
     */
    public static String getVersionName() {
        Context context = ContextUtil.getContext();
        String versionName = null;
        PackageManager packageManager = context.getPackageManager();

        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (Exception e) {
            versionName = "";
        }
        return versionName;
    }

    /**
     * get app version code
     *
     * @return version code
     */
    public static int getVersionCode(Context context) {

        int code = 0;
        PackageManager packageManager = context.getPackageManager();

        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            code = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return code;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        boolean retValue = false;
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                if (packageName.equals(pinfo.get(i).packageName)) {
                    retValue = true;
                    break;
                }
            }
        }
        return retValue;
    }

    public static void startAppForPackage(Context context, String packName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packName);
        try{
            context.startActivity(intent);
        }catch (ActivityNotFoundException e){
        }

    }

    /**
     * 获取指定包名的版本号
     *
     * @param context
     * @param packageName
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        String version = null;
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(packageName, 0);
            version = packInfo.versionName;
        } catch (Exception e) {
            version = "";
        }
        return version;
    }

    /**
     * 判断手机是否支持fast pair功能
     *
     * @return
     */
    public static boolean isSupportFastPair(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }

        String playServiceVersion = getVersionName(context, "com.google.android.gms");
        if (TextUtils.isEmpty(playServiceVersion)) {
            return false;
        }
        String[] versions = playServiceVersion.split("[.]");
        if (versions.length < 2) {
            return false;
        }
        boolean retValue;
        if (Integer.parseInt(versions[0]) < 11) {
            retValue = false;
        } else if (Integer.parseInt(versions[0]) == 11) {

            retValue = Integer.parseInt(versions[1]) >= 7;
        } else {
            retValue = true;
        }

        return retValue;
    }
    public static void openAppDetails(Context context){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }
    /**
     * APP是否处于前台唤醒状态
     *
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获得app名字
     * @param context
     * @return
     */
    public static String getAppName(Context context)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将app版本号转成4位整数，少于4位前面补0
     */
    public static String getAppVersionToIntegerStr(Context context) {

        StringBuilder version = new StringBuilder(getVersionName(context));
        try {
            if (!TextUtils.isEmpty(version.toString())) {
                String[] versions = version.toString().split("-");
                if (versions.length <= 0) {
                    return version.toString();
                }
                versions = versions[0].split("[.]");
                int i = 0;
                int length = versions.length > 3 ? 3 : versions.length;
                version = new StringBuilder();
                for (i = 0; i < length; i++) {
                    version.append(versions[i]);
                }
                i = version.length();
                for (int j = i; j < 4; j++) {
                    version.insert(0, "0");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version.toString();
    }

    public static String getVersionNameSubString(Context context) {
        String versionName = AppUtil.getVersionName(context.getApplicationContext());
        if (!TextUtils.isEmpty(versionName) && versionName.contains("-")) {
            int index = versionName.indexOf("-");
            return versionName.substring(0, index);
        }
        return versionName;
    }

    private boolean checkAppInstalled( Context context, String pkgName) {
        if (pkgName== null || pkgName.isEmpty()) {
            return false;
        }
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> info = packageManager.getInstalledPackages(0);
        if(info == null || info.isEmpty())
            return false;
        for ( int i = 0; i < info.size(); i++ ) {
            if(pkgName.equals(info.get(i).packageName)) {
                return true;
            }
        }
        return false;
    }
}
