package com.wpf.myviewtest.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wpf.myviewtest.R
import com.wpf.myviewtest.ScrollingActivity
import com.wpf.myviewtest.ui.aqs.MyLock
import com.wpf.myviewtest.utils.startAct
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.thread


/**
 *  Author: feipeng.wang
 *  Time:   2021/6/23
 *  Description : This is description.
 */
class MainAct :AppCompatActivity() {
    private lateinit var context: Context

    private var lockPoint = 0;
//    private val myLock:MyLock by lazy { MyLock() }//独占锁
    private val myReadWriteLock:ReentrantReadWriteLock by lazy { ReentrantReadWriteLock(false) }

    var queue: SynchronousQueue<String> = SynchronousQueue()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_act_lay)
        context = this
        val goEqText = findViewById<TextView>(R.id.goToEqView)
        goEqText.setOnClickListener {
            startAct<com.wpf.equalizerview.EqTestAct>(context)
        }
        val goEditText = findViewById<TextView>(R.id.goToClickEdiet)
        goEditText.setOnClickListener {
//            startAct<ScrollingActivity>(context)
            var data:Int = 0
            thread {
                while (data<10){
                    queue.put("${data++}")
                }
            }
        }
  /*      try {
            queue.take()
        }catch (e:Exception){

        }
*/
        //测试独占锁
        thread {
            addLockPoint()
        }

        thread {
            addLockPoint()
        }

    }
    private fun addLockPoint(){
        try {
//            myLock.lock()
            myReadWriteLock.readLock().lock()
            for (i in 0..100){
                lockPoint++
            }
            Log.e("wpf","同步锁结果：${lockPoint}")
        }catch (e:java.lang.Exception){

        }finally {
//            myLock.unlock()
            myReadWriteLock.readLock().unlock()
        }
    }
}