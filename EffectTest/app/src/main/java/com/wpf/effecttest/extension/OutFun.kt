package com.wpf.effecttest.extension

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

inline fun <reified T:AppCompatActivity> startAct(context: Context){
    var intent = Intent(context,T::class.java)
    context.startActivity(intent)
}