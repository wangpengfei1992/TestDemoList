package com.wpf.common_ui.utils;

/**
 * Created by anker on 2020/12/30.
 * <p>
 * 防止快速点击
 *
 * @author nina.ma
 */
public class NoFastClickUtils {
    /**
     * 上次点击的时间
     */
    private static long lastClickTime = 0;
    /**
     * //时间间隔
     */
    private static int spaceTime = 300;

    /**
     * 点击按键前先判断
     *
     * @return true 允许执行点击动作
     */
    public static boolean isFastClick() {
        long currentTime = System.currentTimeMillis();
        boolean isFast;
        if (currentTime - lastClickTime > spaceTime) {
            isFast = false;
        } else {
            isFast = true;
        }
        lastClickTime = currentTime;
        return isFast;
    }


    public static boolean isFastClick(int sTime) {
        long currentTime = System.currentTimeMillis();
        boolean isFast;
        if (currentTime - lastClickTime > sTime) {
            isFast = false;
            lastClickTime = currentTime;
        } else {
            isFast = true;
        }
        return isFast;
    }
}
