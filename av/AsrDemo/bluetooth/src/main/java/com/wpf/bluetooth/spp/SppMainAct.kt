package com.wpf.bluetooth.spp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wpf.bluetooth.R

/**
 *  Author: feipeng.wang
 *  Time:   2021/9/24
 *  Description : This is description.
 */
class SppMainAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spp_demo_act)
        val searchTv:TextView = findViewById(R.id.spp_search)
        searchTv.setOnClickListener {

        }
        val linkTv:TextView  = findViewById(R.id.spp_connect)
        linkTv.setOnClickListener {

        }
        val sendCommandTv:TextView  = findViewById(R.id.spp_send)
        sendCommandTv.setOnClickListener {

        }
    }
}