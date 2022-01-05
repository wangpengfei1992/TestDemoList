package com.anker.bluetoothtool

import android.app.Application
import android.content.Context
import com.anker.bluetoothtool.database.AnkerWorkDatabase

/**
 *  Author: anker
 *  Time:   2021/7/22
 *  Description : This is description.
 */
class MyApplication : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext

    }
}