package com.wpf.common_ui.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by anker on 2020/12/23.
 * <p>
 * 剪贴板相关工具类
 *
 * @author nina.ma
 */
public class ClipboardUtil {

    /**
     * 将内容复制到剪贴板
     *
     * @param activity activity
     * @param strCopy  剪贴内容
     */
    public static void copy(Activity activity, CharSequence strCopy, ClipboardCopyListener listener) {
        ClipboardManager manager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText(null, strCopy);
        if (manager != null && listener != null) {
            manager.setPrimaryClip(data);
            listener.copySuccess();
        }
    }

    /**
     * 将内容从剪贴板拿出来
     *
     * @param activity activity
     */
    public static void paste(Activity activity, ClipboardPasteListener listener) {
        ClipboardManager manager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            ClipData data = manager.getPrimaryClip();
            if (data != null && data.getItemCount() > 0) {
                CharSequence pasteStr = data.getItemAt(0).getText();
                if (listener != null) {
                    listener.pasteContent(pasteStr.toString());
                }
            }
        }
    }

    /**
     * 将内容拿至剪贴板的具体实现，因需求不同可能处理方式不同
     * 且复制粘贴不一定同时使用，两个接口分开
     */
    public interface ClipboardCopyListener {
        void copySuccess();
    }

    public interface ClipboardPasteListener {
        void pasteContent(String pasteStr);
    }

}
