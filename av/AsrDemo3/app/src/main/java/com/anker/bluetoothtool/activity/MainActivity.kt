package com.anker.bluetoothtool.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anker.bluetoothtool.R
import com.anker.bluetoothtool.util.AudioEditor
import com.anker.bluetoothtool.util.Constant
import com.anker.bluetoothtool.util.FileUtil
import com.wpf.common.permisson.PermissionActivity
import com.wpf.ffmpegcmd.FFmpegCmd

class MainActivity : AppCompatActivity() {
    private var mContext:Context ?= null


    // 创建一个Handler
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg?.what) {
                1 -> {
                    Toast.makeText(mContext!!,"上行wav转换成功",Toast.LENGTH_SHORT).show()
                }
                2 -> {
                    Toast.makeText(mContext!!,"下行wav转换成功",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        val permision = findViewById<Button>(R.id.permision)
        permision.setOnClickListener {
            val intent = Intent(this, PermissionActivity::class.java)
            startActivity(intent)
        }
        val file_test = findViewById<Button>(R.id.file_test)
        file_test.setOnClickListener {
            chechPermisson()
        }
        val pcm_to_aac = findViewById<Button>(R.id.pcm_to_aac)
        val fileUtils = FileUtil()
        var audioEditor = AudioEditor()
        val rootPath = fileUtils.initFileRoot(baseContext)+"/AsrDemo2"
        pcm_to_aac.setOnClickListener {
            val opusUpPath = rootPath+"/"+Constant.saveUpPcmFile;
            val wavUpPath = "$rootPath/up.wav"
            audioEditor.pcmToWav(opusUpPath, wavUpPath,
                    50000, object : FFmpegCmd.OnCmdExecListener {
                override fun onSuccess() {
                    Log.e("wpf", "onSuccess,输出到：${wavUpPath}")
                    mHandler.sendEmptyMessage(1)
                }

                override fun onFailure() {
                    Log.e("wpf", "onFailure")
                }

                override fun onProgress(progress: Float) {
                    Log.e("wpf", "$progress")
                }

            })
        }
        val downpcm_to_aac = findViewById<Button>(R.id.downpcm_to_aac)
        downpcm_to_aac.setOnClickListener {
            val opusDownPath = rootPath+"/"+Constant.saveDownPcmFile;
            val wavDownPath = "$rootPath/down.wav"
            audioEditor.pcmToWav(opusDownPath, wavDownPath,
                    50000, object : FFmpegCmd.OnCmdExecListener {
                override fun onSuccess() {
                    Log.e("wpf", "onSuccess,输出到：${wavDownPath}")
                    mHandler.sendEmptyMessage(2)
                }

                override fun onFailure() {
                    Log.e("wpf", "onFailure")
                }

                override fun onProgress(progress: Float) {
                    Log.e("wpf", "$progress")
                }

            })
        }
    }
    private fun makeFile(){
        val fileUtils = FileUtil()
        val rootPath = fileUtils.initFileRoot(baseContext)+"/AsrDemo2"
        fileUtils.write(rootPath, "wpf test ", "testFile.txt")
    }

    private val REQUEST_PERMISSION_CODE = 110
    private fun chechPermisson(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 适配android11读写权限
            if (Environment.isExternalStorageManager()) {
                //已获取android读写权限
                makeFile()
            } else {
                val intent:Intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_PERMISSION_CODE);
            }
            return;
        }else {
            makeFile()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERMISSION_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                Toast.makeText(baseContext,"获取文件权限成功",Toast.LENGTH_SHORT).show()
                //已获取android读写权限
                makeFile()
            } else {
                //存储权限获取失败
                Toast.makeText(baseContext,"获取文件权限失败",Toast.LENGTH_SHORT).show()
            }
        }
    }
}