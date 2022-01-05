package com.wpf.bluetooth.common.util

import android.content.Context
import android.content.Intent


inline fun <reified T>  startAct(context: Context){
    var intent: Intent = Intent(context,T::class.java)
    context.startActivity(intent)
}