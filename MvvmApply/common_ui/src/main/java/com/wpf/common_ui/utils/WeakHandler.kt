package com.anker.common.utils

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.wpf.common_ui.base.BaseActivity
import java.lang.ref.WeakReference


/**
 *  Author: feipeng.wang
 *  Time:   2021/6/29
 *  Description : This is description.
 */
open class WeakHandler(activity: BaseActivity<*>?,looper: Looper) : Handler(looper) {
    private val activityWeakReference: WeakReference<BaseActivity<*>>?
    init {
        activityWeakReference = WeakReference<BaseActivity<*>>(activity)
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        if (activityWeakReference == null) {
            return
        }
        val baseActivity: BaseActivity<*> = activityWeakReference.get() ?: return
        baseActivity.handleMessage(msg)
    }
}

