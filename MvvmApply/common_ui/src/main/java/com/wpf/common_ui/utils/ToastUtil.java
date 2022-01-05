package com.wpf.common_ui.utils;

import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by anker on 2021/1/14.
 *
 * @author nina.ma
 */
public class ToastUtil {

    private static Toast toast;

    public static void showToast(String content) {
        if (toast == null) {
            toast = Toast.makeText(ContextUtil.getContext(), content, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 200);
        } else {
            toast.cancel();
            toast = Toast.makeText(ContextUtil.getContext(), content, Toast.LENGTH_LONG);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

}
