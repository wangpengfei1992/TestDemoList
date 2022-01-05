package com.wpf.studywork.extension

import android.content.Context
import android.content.Intent


inline fun <reified T>  startAct(context: Context){
    var intent: Intent = Intent(context,T::class.java)
    context.startActivity(intent)
}