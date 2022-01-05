package com.wpf.darkmodetest

import android.content.Context
import android.content.Intent

/**
 *  Author: feipeng.wang
 *  Time:   2021/6/22
 *  Description : This is description.
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