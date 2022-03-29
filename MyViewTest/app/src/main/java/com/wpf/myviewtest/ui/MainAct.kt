package com.wpf.myviewtest.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.wpf.myviewtest.R
import com.wpf.myviewtest.ui.chart.CombinedChartUtils
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
        testChat()
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

    private fun testChat() {
        val chat = findViewById<CombinedChart>(R.id.chart1)
        val lineEntry: MutableList<Entry> = ArrayList()
        lineEntry.add(Entry(0F, 3F))
        lineEntry.add(Entry(1F, 5F))
        lineEntry.add(Entry(2F, 6F))
        lineEntry.add(Entry(3F, 5F))
        lineEntry.add(Entry(4F, 2F))
        lineEntry.add(Entry(5F, 8F))

        val barEntry: MutableList<CandleEntry> = ArrayList()
        barEntry.add(CandleEntry(1F, 8F, 1F, 3f, 6f))
        barEntry.add(CandleEntry(2F, 8F, 1F, 8f, 2f))
        barEntry.add(CandleEntry(3F, 3F, 2F, 2f, 2f))
        barEntry.add(CandleEntry(4F, 5F, 1F, 4f, 2f))
        barEntry.add(CandleEntry(5F, 6F, 2F, 3f, 3f))
        barEntry.add(CandleEntry(6F, 8F, 2F, 3f, 6f))
        barEntry.add(CandleEntry(7F, 7F, 3F, 3f, 6f))
        barEntry.add(CandleEntry(8F, 7F, 2F, 6f, 2f))
        barEntry.add(CandleEntry(9F, 6F, 2F, 2f, 6f))
        barEntry.add(CandleEntry(10F, 9F, 5F, 6f, 7f))
        barEntry.add(CandleEntry(11F, 5F, 1F, 3f, 4f))
        var combinedChartUtils = CombinedChartUtils(chat)
        combinedChartUtils.setSingleCombinedData(lineEntry,barEntry); //单条直线图的K线图
    }

    private fun addLockPoint(){
        try {
//            myLock.lock()
            myReadWriteLock.readLock().lock()
            for (i in 0..100){
                lockPoint++
            }
            Log.e("wpf", "同步锁结果：${lockPoint}")
        }catch (e: java.lang.Exception){

        }finally {
//            myLock.unlock()
            myReadWriteLock.readLock().unlock()
        }
    }
}