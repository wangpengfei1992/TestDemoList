package com.anker.bluetoothtool.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.anker.bluetoothtool.R
import com.anker.bluetoothtool.util.Constant
import com.anker.bluetoothtool.util.FileUtil

/**
 *  Author: feipeng.wang
 *  Time:   2021/10/9
 *  Description : 对接收的数据进行处理
 */
class FileDealAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_deal)
        val fileUtils = FileUtil()
        val rootPath = fileUtils.initFileRoot(baseContext)+"/AsrDemo2"
        val originalFile1 = rootPath +"/"+ Constant.saveUpPcmFile
        val originalFile2 = rootPath +"/"+ Constant.saveDownPcmFile

        val get_file_data: Button = findViewById(R.id.get_file_data)
        get_file_data.setOnClickListener {

        }
        val pcm_to_wav: Button = findViewById(R.id.pcm_to_wav)
        pcm_to_wav.setOnClickListener {

        }

    }
}