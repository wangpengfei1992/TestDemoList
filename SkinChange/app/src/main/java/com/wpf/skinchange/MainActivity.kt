package com.wpf.skinchange

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.wpf.skin.skin.QMUISkinManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        QMUISkinManager.defaultInstance(this).register(this)
        val autoTextView = findViewById<TextView>(R.id.tv_change_auto)
        autoTextView.setOnClickListener {
            QDSkinManager.changeSkin(1)
        }
        val darkTextView = findViewById<TextView>(R.id.tv_change_dark)
        darkTextView.setOnClickListener {
            QDSkinManager.changeSkin(2)
        }
        val lightTextView = findViewById<TextView>(R.id.tv_change_light)
        lightTextView.setOnClickListener {
            QDSkinManager.changeSkin(3)
        }
    }

    override fun onStart() {
        super.onStart()

    }
}