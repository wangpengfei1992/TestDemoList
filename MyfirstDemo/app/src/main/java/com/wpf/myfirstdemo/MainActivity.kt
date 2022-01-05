package com.wpf.myfirstdemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.wpf.myfirstdemo.main.TestActivity
import com.wpf.myfirstdemo.test_thread.CountDownLatchTest
import com.wpf.myfirstdemo.test_thread.ReetrantLockTest
import com.wpf.myfirstdemo.util.startAct

class MainActivity : AppCompatActivity() {
    lateinit var contenxt:Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contenxt = this
        setContentView(R.layout.activity_main)
        var hello_world:TextView = findViewById(R.id.hello_world)
        hello_world.setOnClickListener {
            startAct<TestActivity>(contenxt){
                putExtra("key1","test")
            }
        }
        //ReetrantLock测试
//        ReetrantLockTest.getInstance().test1()
        //CountDownLatch测试
//        CountDownLatchTest.getInstance().test1()
        CountDownLatchTest.getInstance().test2()
    }
}
