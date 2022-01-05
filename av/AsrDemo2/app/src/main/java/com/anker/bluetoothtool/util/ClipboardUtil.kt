package com.anker.bluetoothtool.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * Created by anker on 2020/12/23.
 *
 *
 * 剪贴板相关工具类
 *
 * @author nina.ma
 */
object ClipboardUtil {
    /**
     * 将内容复制到剪贴板
     *
     * @param activity activity
     * @param strCopy  剪贴内容
     */
    fun copy(activity: Activity, strCopy: CharSequence?, block: () -> Unit) {
        val manager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val data = ClipData.newPlainText(null, strCopy)
        if (manager != null) {
            manager.setPrimaryClip(data)
            block()
        }
    }

    /**
     * 将内容从剪贴板拿出来
     *
     * @param activity activity
     */
    fun paste(activity: Activity): String {
        val manager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        if (manager != null) {
            val data = manager.primaryClip
            if (data != null && data.itemCount > 0) {
                return data.getItemAt(0).text.toString()
            }
        }
        return ""
    }

    /**
     * 将内容拿至剪贴板的具体实现，因需求不同可能处理方式不同
     * 且复制粘贴不一定同时使用，两个接口分开
     */
    interface ClipboardCopyListener {
        fun copySuccess()
    }

    interface ClipboardPasteListener {
        fun pasteContent(pasteStr: String?)
    }
}