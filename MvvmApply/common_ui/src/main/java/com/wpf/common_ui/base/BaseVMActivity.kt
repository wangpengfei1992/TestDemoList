package com.wpf.common_ui.base

import android.os.Bundle
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.anker.common.utils.WeakHandler

/**
 *  Author: feipeng.wang
 *  Time:   2021/7/7
 *  Description : This is description.
 */
abstract class BaseVMActivity<VM:BaseViewModel,VB : ViewBinding> : BaseActivity<VB>() {

}