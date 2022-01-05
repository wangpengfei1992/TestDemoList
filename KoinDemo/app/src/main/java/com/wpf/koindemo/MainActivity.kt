package com.wpf.koindemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.wpf.koindemo.demo.HelloViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named


class MainActivity : AppCompatActivity() {
    private var key = ""
    init {
        key = "vm1"
    }
    private val mainViewModel:HelloViewModel by viewModel(named(key))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("wpf",mainViewModel.sayHello())
        val gotoTest = findViewById<TextView>(R.id.goto_testviewmodel)
        gotoTest.setOnClickListener {
            startActivity(
                    Intent(this, TestViewModelDataAct::class.java).apply {
                        putExtra("KEY", "From MainActivity")
                    }
            )
        }
    }
}