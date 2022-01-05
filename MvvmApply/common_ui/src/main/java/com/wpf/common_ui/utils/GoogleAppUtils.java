//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.wpf.common_ui.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;



/**
 * Created by asus on 2018/3/9.
 */

public class GoogleAppUtils {

    public interface GotoUrlListener {

        void gotoAppUrl(String eventTag);

        void gotoWebUrl(String eventTag);

    }


    public static void gotoGooglePlaySoundcore(Activity context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            String url ="https://play.google.com/store/apps/details?id=" + context.getPackageName();
            openUrlByBrowser(context, url);
        }
    }
    public static void openUrlByBrowser(Context context, String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if(context.getPackageManager().resolveActivity(intent,0)!=null){
            context.startActivity(intent);
        }else{
            LogUtil.e("-------------openUrlByBrowser error");
        }

    }
    public static final String ALEXA_APP_PKG = "com.amazon.dee.app";
    public static final String SPOTIFY_APP_PKG = "com.spotify.music";
    public static void openAppByMarket(Context context, String appPkg, String marketPkg) {
        if (TextUtils.isEmpty(appPkg)){
            return;
        }
        Uri uri = Uri.parse("market://details?id=" + appPkg);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        if (!TextUtils.isEmpty(marketPkg)) {
            goToMarket.setPackage(marketPkg);
        }
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            String url ="https://play.google.com/store/apps/details?id=" + appPkg;
            openUrlByBrowser(context, url);
        }

    }
    public static void launcherAppOrOpenByMarket(Context context, String appPkg, String marketPkg){
        if(AppUtil.isAppInstalled(context,appPkg)) {
            AppUtil.startAppForPackage(context, appPkg);
        }else {
            openAppByMarket(context, appPkg, marketPkg);
        }
    }

    /**
     *
     * @param context
     * @param appUrl 不为空且已安装了对应app时，使用app打开对应的页面
     * @param webUrl 不满足上面条件时，使用web 打开连接
     * @param shareUrl 不需要时传 null
     * @param eventTag 用来做事件的标记
     * @param listener 接口回调，不需要的时候传null
     */
    public static void gotoAppOrWebUrl(Context context, String appUrl, String webUrl,
                                       String shareUrl, String eventTag, GotoUrlListener listener) {
        try {
            Uri uri = Uri.parse(appUrl);
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            if (listener != null) {
                LogUtil.v("--gotoAppUrl--");
                listener.gotoAppUrl(eventTag);
            }
        } catch (Exception e) {
            LogUtil.v("--empty webUrl to show--");
        }
    }

}
