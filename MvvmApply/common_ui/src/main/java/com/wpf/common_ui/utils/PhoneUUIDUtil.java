package com.wpf.common_ui.utils;

import android.content.Context;
import android.os.Build;

import java.util.Random;
import java.util.UUID;

/**
 * Created by anker on 2020/11/10.
 * get device unique id
 *
 * @author nina.ma
 */
public class PhoneUUIDUtil {
    private final static String TAG = "PhoneUUIDUtil";
    private final static int UUID_RANDOM_MIN = 0;
    private final static int UUID_RANDOM_MAX = 100;
    private final static String CREATE_UUID_KEY = "create_uuid_key";
    private final static String CREATE_UUID_DEFAULT = "empty";

    /**
     * 生成随机数
     *
     * @param min 随机数最小值
     * @param max 随机数最大值
     * @return 返回生成的随机数
     */
    public static Random random;

    public static int createRandomInt(int min, int max) {
        if (random == null) random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 生成设备唯一标识符
     *
     * @return
     */
    private static String createPhoneUUID() {
        //first step: hardware info
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
        //second step: create timeStamp
        int stamp = (int) (System.currentTimeMillis() / 1000);
        String stampStr = String.valueOf(stamp);
        //third step: create randomInt
        int random = createRandomInt(UUID_RANDOM_MIN, UUID_RANDOM_MAX);
        String randomStr = String.valueOf(random);
        //Joining together the results
        String secondStr = stampStr + randomStr;
        String result = new UUID(m_szDevIDShort.hashCode(), secondStr.hashCode()).toString();
        LogUtil.d(TAG, "result : " + result);
        return result;
    }

    /**
     * 该APP是否创建过UUID
     *
     * @param context 上下文
     * @return 是否创建: true -> 创建过 ; false -> 未创建过
     */
    private static boolean isCreatedUUID(Context context) {
        return !getUUIDWithoutCreate(context).equals(CREATE_UUID_DEFAULT);
    }

    /**
     * 获取UUID
     *
     * @param context 上下文
     * @return
     */
    private static String getUUIDWithoutCreate(Context context) {
        return SPUtil.getString(context, CREATE_UUID_KEY, CREATE_UUID_DEFAULT);
    }

    /**
     * 获取手机设备唯一标识符
     *
     * @param context 上下文
     * @return UUID
     */
    public static String getDeviceUUID(Context context) {
        if (isCreatedUUID(context)) {
            return SPUtil.getString(context, CREATE_UUID_KEY, CREATE_UUID_DEFAULT);
        } else {
            String uuid = createPhoneUUID();
            putUUID(context, uuid);
            return uuid;
        }
    }

    /**
     * 存储UUID
     *
     * @param context 上下文
     * @param value   UUID
     */
    private static void putUUID(Context context, String value) {
        SPUtil.putString(context, CREATE_UUID_KEY, value);
    }
}
