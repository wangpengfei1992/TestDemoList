package com.wpf.myfirstdemo.util

import android.content.Context
import android.content.Intent


inline fun <reified T> startAct(context: Context,block: Intent.()->Unit){
    var intent = Intent(context,T::class.java)
    intent.block()
    context.startActivity(intent)
}
