package com.wpf.equalizerview

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.wpf.equalizerview.my.VerticalSeekBar
import com.wpf.equalizerview.my.VerticalSeekBar2


/**
 *  Author: feipeng.wang
 *  Time:   2021/6/23
 *  Description : This is description.
 */
class EqTestAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eq_lay)

        /*val seekBar = findViewById<VerticalSeekBar>(R.id.sec)
        seekBar.max = 100
        seekBar.progress = 20
        seekBar.setOnSeekBarChangeListener(object :VerticalSeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(
                VerticalSeekBar: VerticalSeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                Log.e("wpf","seekBar:::$progress,$fromUser")
            }

            override fun onStartTrackingTouch(VerticalSeekBar: VerticalSeekBar?) {

            }

            override fun onStopTrackingTouch(VerticalSeekBar: VerticalSeekBar?) {

            }
        })*/

/*        val seekBar = findViewById<VerticalSeekBar2>(R.id.sec)
        seekBar.max = 100
        seekBar.progress = 20
        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.e("wpf","seekBar:::$progress,$fromUser")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })*/

    }
}