//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not
// limited to reproduction, retransmission, communication, display, mirror, download,
// modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.wpf.common_ui.utils;

import android.content.Context;
import android.content.SharedPreferences;




import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPUtil {

    public static final String FILE_NAME = "share_data";
    public static final String FILE_BLE_HISTORY = "ble_history_file";
    public static final String FILE_CONNECTED_DEVICE = "connected_device_file";
    private final static String KEY_CONNECTED_DEVICE = "keyConnectedDevice";// 存储用户连接成功的设备
    private final static String KEY_LOCAL_PRODUCT = "keyLocalProduct";// 存储用户连接成功的设备
    private final static String CURRENT_PRODUCT_CODE = "currentProductCode";// 目前连接的设备
    private final static String CURRENT_MAC_ADDRESS = "currentMacAddress";// 目前连接的设备的mac地址
    private final static String DEVICE_IS_CONNECTED = "deviceIsConnected";// 是否连接

    //gdpr
    public final static String KEY_POLICY_UPDATE_TIME = "keyPolicyUpdateTime";
    public final static String KEY_SHOW_POLICY = "keyShowPolicy";

    //setting
    public final static String KEY_MENU_SETTING_PUSH = "keyMenuSettingPush";  //push开关是否打开
    public final static String KEY_MENU_SETTING_EMAIL = "keyMenuSettingEmail";  //email开关是否打开

    //language update
    public final static String KEY_LANGUAGE_NEED_UPDATE = "keyLanguageNeedUpdate";
    //build Flavors
    public final static String KEY_BUILD_FLAVORS = "key_build_flavors";

    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static SharedPreferences.Editor getSharePrefrence(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        return editor;
    }

    public static void putString(Context context, HashMap<String, String> map) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        SharedPreferencesCompat.apply(editor);
    }

    public static void putInt(Context context, String key, int value) {

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static void putInt(Context context, HashMap<String, Integer> map) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer values = entry.getValue();
            if (values == null) {
                continue;
            }
            editor.putInt(key, values.intValue());
        }
        SharedPreferencesCompat.apply(editor);
    }

    public static void putLong(Context context, String key, long value) {

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static void putFloat(Context context, String key, float value) {

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static void putBoolean(Context context, String key, boolean value) {

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static void putBoolean(Context context, String fileName, String key, boolean value) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static void putBoolean(Context context, List<String> keyArr, List<Boolean> valueArr) {
        if (keyArr == null || valueArr == null || keyArr.size() != valueArr.size()) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for (int i = 0; i < keyArr.size(); i++) {
            editor.putBoolean(keyArr.get(i), valueArr.get(i));
        }
        SharedPreferencesCompat.apply(editor);
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);

    }

    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getFloat(key, defaultValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    public static boolean getBoolean(Context context, String fileName, String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    /**
     * compat
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

    public static final String FIRMWARE_FILE_NAME = "firmware_file";

    public static void putFwDialogShownBoolean(Context context, String productCode, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(FIRMWARE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(productCode.concat("_has_shown_update"), value);
        SharedPreferencesCompat.apply(editor);
    }

    public static boolean getFwDialogShownBoolean(Context context, String productCode) {
        SharedPreferences sp = context.getSharedPreferences(FIRMWARE_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(productCode.concat("_has_shown_update"), false);
    }

    public static void clear(String fileName, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    public static void putBleHistoryString(Context context, String key, String value) {

        SharedPreferences sp = context.getSharedPreferences(FILE_BLE_HISTORY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);

        SharedPreferencesCompat.apply(editor);
    }

    public static String getBleHistoryString(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(FILE_BLE_HISTORY, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static void putConnectedDevices(String value) {
        SharedPreferences sp = ContextUtil.getContext().getSharedPreferences(FILE_CONNECTED_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_CONNECTED_DEVICE, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static String getConnectedDevices() {
        SharedPreferences sp = ContextUtil.getContext().getSharedPreferences(FILE_CONNECTED_DEVICE, Context.MODE_PRIVATE);
        return sp.getString(KEY_CONNECTED_DEVICE, "");
    }

    public static void putCurrentProductCode(String value) {
        SharedPreferences sp = ContextUtil.getContext().getSharedPreferences(FILE_CONNECTED_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CURRENT_PRODUCT_CODE, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static String getCurrentProductCode() {
        SharedPreferences sp = ContextUtil.getContext().getSharedPreferences(FILE_CONNECTED_DEVICE, Context.MODE_PRIVATE);
        return sp.getString(CURRENT_PRODUCT_CODE, "");
    }

    public static void putCurrentMacAddress(String value) {
        SharedPreferences sp = ContextUtil.getContext().getSharedPreferences(FILE_CONNECTED_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CURRENT_MAC_ADDRESS, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static String getCurrentMacAddress() {
        SharedPreferences sp = ContextUtil.getContext().getSharedPreferences(FILE_CONNECTED_DEVICE, Context.MODE_PRIVATE);
        return sp.getString(CURRENT_MAC_ADDRESS, "");
    }

//    public static void putIsConnected(Boolean value) {
//        SharedPreferences sp = ContextUtil.getContext().getSharedPreferences(FILE_CONNECTED_DEVICE, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putBoolean(DEVICE_IS_CONNECTED, value);
//        SharedPreferencesCompat.apply(editor);
//    }
//
//    public static Boolean getIsConnected() {
//        SharedPreferences sp = ContextUtil.getContext().getSharedPreferences(FILE_CONNECTED_DEVICE, Context.MODE_PRIVATE);
//        return sp.getBoolean(DEVICE_IS_CONNECTED, false);
//    }

    public static void putLocalProduct(String value) {
        SharedPreferences sp = ContextUtil.getContext().getSharedPreferences(FILE_CONNECTED_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_LOCAL_PRODUCT, value);
        SharedPreferencesCompat.apply(editor);
    }

    private static final String DEF_PRODUCT = "[\n" +
//            "  {\n" +
//            "    \"id\": \"065127c3-5ffc-4c6b-98b1-32d1b8840d0f\",\n" +
//            "    \"product_category_id\": \"\",\n" +
//            "    \"index\": 3,\n" +
//            "    \"product_code\": \"A3305\",\n" +
//            "    \"name\": \"PowerConf S500\",\n" +
//            "    \"icon\": \"https://ankerwork.s3-us-west-2.amazonaws.com/online_product/img_3305.png\"\n" +
//            "  },\n" +
            "  {\n" +
            "    \"id\": \"6fedb285-59df-45ff-991d-a719679e73e4\",\n" +
            "    \"product_category_id\": \"\",\n" +
            "    \"index\": 2,\n" +
            "    \"product_code\": \"A3302\",\n" +
            "    \"name\": \"PowerConf S3\",\n" +
            "    \"icon\": \"https://ankerwork.s3-us-west-2.amazonaws.com/online_product/img_3302.png\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": \"745985cf-368d-41b6-8241-99fa0d0ab813\",\n" +
            "    \"product_category_id\": \"\",\n" +
            "    \"index\": 1,\n" +
            "    \"product_code\": \"A3301\",\n" +
            "    \"name\": \"PowerConf\",\n" +
            "    \"icon\": \"https://ankerwork.s3-us-west-2.amazonaws.com/online_product/img_3301.png\"\n" +
            "  }\n" +
            "]";

    public static String getLocalProduct() {
        SharedPreferences sp = ContextUtil.getContext().getSharedPreferences(FILE_CONNECTED_DEVICE, Context.MODE_PRIVATE);
        return sp.getString(KEY_LOCAL_PRODUCT, DEF_PRODUCT);
    }

    /**
     * @param context
     * @param fileName A3163FUPresenter.SPFILE_NAME
     * @return
     */

    public static Map<String, String> getAll(Context context, String fileName) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return (Map<String, String>) sp.getAll();
    }

    public static void clearAll(Context context, String fileName) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    public static String getString(Context context, String fileName, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static void putString(Context context, String fileName, String key, String value) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static int getInt(Context context, String fileName, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    public static void putInt(Context context, String fileName, String key, int value) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static void removeString(Context context, String fileName, String key) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 保存List
     */
    public static <T> void setDataList(Context context, String key, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;

        //转换成json数据，再保存
        String strJson = JsonUtil.toJson(datalist);
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, strJson);
        editor.commit();

    }

    /**
     * 获取List
     */
    public static <T> List<T> getDataList(Context context, String key, Class<T[]> cls) {
        List<T> datalist = new ArrayList<T>();
        String strJson = getString(context, key, null);
        if (null == strJson) {
            return datalist;
        }
        //datalist = gson.fromJson(strJson,list.getClass());
        T[] arr = JsonUtil.fromJsonToObject(strJson, cls);
        datalist = Arrays.asList(arr);
        return new ArrayList<>(datalist);

    }

}  