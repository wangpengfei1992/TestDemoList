//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.wpf.common_ui.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.os.ConfigurationCompat;
import androidx.core.os.LocaleListCompat;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

public class SystemUtil {

    private static final String TAG = "SystemUtil";

    public static final int READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 3001;

    public static final int ANDROID_Q_BUILD_VALUE = 29;


    public static String getCurLanguage() {
        Context context = ContextUtil.getContext();
        String chooseLanguage;
//        if (SystemUtil.getSystemLanguage().equals("zh") && SystemUtil.getSysCountry().equals("TW")) {
//            chooseLanguage = SPUtil.getString(context, SwitchLanguageActivity.LANGUAGE_KEY, getSysLangAndCty());
//        } else {
//            chooseLanguage = SPUtil.getString(context, SwitchLanguageActivity.LANGUAGE_KEY, SystemUtil.getSystemLanguage());
//        }
        chooseLanguage = SystemUtil.getSystemLanguage();
        return chooseLanguage;
    }

    public static String getSysLangAndCty() {
        return SystemUtil.getSystemLanguage() + "-" + SystemUtil.getSysCountry();
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        String language="";
        LocaleListCompat listCompat= ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration());
        if(listCompat!=null && listCompat.size()>0){
            language = listCompat.get(0).getLanguage();
        }
        return language;
    }
    public static String getSysCountry(){
        String country="";
        LocaleListCompat listCompat= ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration());
        if(listCompat!=null && listCompat.size()>0){
            country = listCompat.get(0).getCountry();
        }
        return country;
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return "Android " + android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }
    public static final String SYS_EMUI = "sys_emui";
    public static final String SYS_MIUI = "sys_miui";
    public static final String SYS_FLYME = "sys_flyme";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_VERSION = "ro.build.version.emui";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";

    public static String getSystem(){
        String SYS ="Others";
        try {
            Properties prop= new Properties();
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            if(prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null){
                SYS = SYS_MIUI;//小米
            }else if(prop.getProperty(KEY_EMUI_API_LEVEL, null) != null
                    ||prop.getProperty(KEY_EMUI_VERSION, null) != null
                    ||prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null){
                SYS = SYS_EMUI;//华为
            }else if(getMeizuFlymeOSFlag().toLowerCase().contains("flyme")){
                SYS = SYS_FLYME;//魅族
            }else{
                SYS = "Others";
            }
        } catch (IOException e){
            e.printStackTrace();
            return SYS;
        }
        return SYS;
    }

    public static String getMeizuFlymeOSFlag() {
        return getSystemProperty("ro.build.display.id", "");
    }

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String)get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    /*获取手机名称*/
    public static String getPhoneName(Context context) {
        return Settings.Global.getString(context.getContentResolver(), Settings.Global.DEVICE_NAME);
    }
    /**
     * 获取设备ID
     *
     * @return 设备ID
     */
    public static String getDeviceId() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return android.os.Build.SERIAL;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < ANDROID_Q_BUILD_VALUE) {
            try {
                return Build.getSerial();
            } catch (SecurityException e) {
                return "";
            }
        } else {
            return getUUID();
        }
    }

    /**
     * 获取手机的Sn号
     *
     * @return 手机的Sn号
     */
    public static String getSerialNumber1() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return android.os.Build.SERIAL;
        }

        try {
            return Build.getSerial();
        } catch (SecurityException e) {
            return "";
        }
    }

    /**
     * 检查Sn是否OK
     *
     * @return Sn是否OK
     */
    public static boolean checkSnOK() {
        String user = getDeviceId();
        if (TextUtils.isEmpty(user) || user.equals("unknown")) {
            return false;
        }

        return true;
    }

    /**
     * 检查获取手机信息权限
     *
     * @param activity 当前activity
     * @return 当前是否拥有获取手机信息权限
     */
    public static boolean checkReadPhoneStatePermission(Activity activity) {
        if (activity == null) {
            LogUtil.e(TAG, "checkReadPhoneStatePermission failed, because activity is null !");
            return false;
        }

        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.READ_PHONE_STATE};
            ActivityCompat.requestPermissions(activity, permissions, READ_PHONE_STATE_PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    /***
     * 将系统版本号转成4位整数，少于4位前面补0
     * */
    public static String getSystemVersionToIntegerStr() {
        StringBuilder version = new StringBuilder(android.os.Build.VERSION.RELEASE);
        try {
            if (!TextUtils.isEmpty(version.toString())) {
                String[] versions = version.toString().split("[.]");
                int i = 0;
                version = new StringBuilder();
                for (i = 0; i < versions.length; i++) {
                    version.append(versions[i]);
                }
                i = version.length();
                if (i==2){
                    version.append("0");
                }
                for (i = version.length(); i < 4; i++) {
                    version.insert(0, "0");
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version.toString();
    }

    public static String getUUID() {
        String serial = null;
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = android.os.Build.getSerial();
            } else {
                serial = Build.SERIAL;
            }
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
}
