package com.wpf.myviewtest.utils

import android.content.Context
import android.content.Intent

/**
 *  Author: feipeng.wang
 *  Time:   2021/6/23
 *  Description : 扩展功能
 */
inline fun <reified T> startAct(context: Context){
    var intent = Intent(context,T::class.java)
    context.startActivity(intent)
}

inline fun <reified T> startAct(context: Context,block: Intent.()->Unit){
    var intent = Intent(context,T::class.java)
    intent.block()
    context.startActivity(intent)
}