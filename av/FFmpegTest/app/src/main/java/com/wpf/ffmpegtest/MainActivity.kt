package com.wpf.ffmpegtest

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var mContext: AppCompatActivity
    private var rootPath:String ?= null
    private var srcFile:String =""
    private var appendFile:String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        findViewById<TextView>(R.id.get_permisson).setOnClickListener {
            if (initPermission()){
                rootPath = Utils.getInstance().initFileRoot(mContext)
                srcFile = rootPath+"/"
                appendFile = rootPath+"/"
            }
        }
        findViewById<TextView>(R.id.cut_audio).setOnClickListener {
            doHandleAudio(1)
        }
        findViewById<TextView>(R.id.conact_audio).setOnClickListener {
            doHandleAudio(2)
        }
        findViewById<TextView>(R.id.mix_audio).setOnClickListener {
            doHandleAudio(3)
        }
        findViewById<TextView>(R.id.tansform_audio).setOnClickListener {
            doHandleAudio(0)
        }
    }

    /**
     * 调用ffmpeg处理音频
     * @param handleType handleType
     */
    private fun doHandleAudio(handleType: Int) {
        var commandLine: Array<String> = emptyArray<String>()
        when (handleType) {
            0 -> {
                val transformFile: String =
                    rootPath + File.separator.toString() + "transform.aac"
                var commondList:List<String> = FfmpegCmdUtils.transformAudio(srcFile, transformFile)
                commandLine = commondList.toTypedArray()
            }
            1 -> {
                val cutFile: String = rootPath + File.separator.toString() + "cut.mp3"
                var commondList:List<String> = FfmpegCmdUtils.cutAudio(srcFile, 10, 15, cutFile)
                commandLine = commondList.toTypedArray()
            }
            2 -> {
                val concatFile: String = rootPath + File.separator.toString() + "concat.mp3"
                var commondList:List<String> = FfmpegCmdUtils.conactAudio(srcFile, appendFile, concatFile)
                commandLine = commondList.toTypedArray()
            }
            3 -> {
                val mixFile: String = rootPath + File.separator.toString() + "mix.aac"
                var commondList:List<String> =FfmpegCmdUtils.mixAudio(srcFile, appendFile, mixFile)
                commandLine = commondList.toTypedArray()
            }
            else -> {
            }
        }
//        executeFFmpegCmd(commandLine)
        if (commandLine == null)return
        thread {
            FfmpegUtils().handle(commandLine)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            Utils.getInstance().initFileRoot(mContext)
        }
    }

    private val CALLS_STATE = arrayOf( Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO, Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.BLUETOOTH
        , Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.MODIFY_AUDIO_SETTINGS)
    private fun initPermission(): Boolean {
        var permission:Int = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + mContext.getPackageName())
                startActivityForResult(intent, 1)
                return false
            }else{
                return true
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    1
                )
                return false
            }else{
                return true
            }
        }else {
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mContext, CALLS_STATE, 1);
                return false
            }else{
                return true
            }
        }
    }
}