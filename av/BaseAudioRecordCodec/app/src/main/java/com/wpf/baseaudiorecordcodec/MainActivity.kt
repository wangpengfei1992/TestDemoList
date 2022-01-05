package com.wpf.baseaudiorecordcodec

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioTrack
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wpf.baseaudiorecordcodec.audiorecord.AudioRecordUtil
import com.wpf.baseaudiorecordcodec.audiorecord.GlobalConfig
import com.wpf.baseaudiorecordcodec.audiorecord.PcmToWavUtil
import com.wpf.baseaudiorecordcodec.audiotrack.AudioTrackUtil
import java.io.File


/*
原生处理音频
*/

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.simpleName
    private lateinit var mContext: MainActivity
    private val CALLS_STATE = arrayOf( Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO, Manifest.permission.RECEIVE_BOOT_COMPLETED)
    private val READ_PHONE_STATE = 1

    private var isStatRecord = false
    public var isPlayPcm = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        Utils.getInstance().initFileRoot(this)
        initPermission()
        initClick()
    }

    private var filePath = ""
    private var newFilePath = "/storage/emulated/0/wpf/record/1.wav"

    private fun initClick() {
        var audioRecord = findViewById<TextView>(R.id.audio_start_record)
        audioRecord.setOnClickListener {

            audioRecord.also {
                isStatRecord = !isStatRecord
                it.text =  when(isStatRecord){
                    true->  {
                        filePath = AudioRecordUtil.instance.creatAudioRecord()
                        "正在录音"
                    }
                    else->  {
                        AudioRecordUtil.instance.stopRecord()
                        var pcmToWavUtil = PcmToWavUtil(GlobalConfig.SAMPLE_RATE_INHZ,GlobalConfig.CHANNEL_CONFIG,GlobalConfig.AUDIO_FORMAT)
                        newFilePath = "/storage/emulated/0/wpf/record/1.wav"
                        var file = File(newFilePath)
                        if (!file.parentFile.exists())file.parentFile.mkdirs()
                        /*转换大wav文件，google手机不能播放，vivo手机可以播。推测是高通内核原因，待查明*/
                        pcmToWavUtil.pcmToWav(filePath,newFilePath)
                        "开始录音"
                    }
                }
            }
        }
        var audioTrack = findViewById<TextView>(R.id.audio_start_track)
        audioTrack.setOnClickListener {
            if (newFilePath.isNotEmpty()){
                audioTrack.let {
                    isPlayPcm = !isPlayPcm
                    it.text = when(isPlayPcm){
                        true->{
                            AudioTrackUtil.instance.playInModeStream("/storage/emulated/0/wpf/record/1.pcm")
                            "正在播放"
                        }
                        else->{
                            AudioTrackUtil.instance.stopTack()
                            "停止播放"
                        }
                    }

                }


            }
        }
    }

    private fun initPermission() {
        var permission:Int = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE);
        /*if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mContext, CALLS_STATE, READ_PHONE_STATE);
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + mContext.getPackageName())
                startActivityForResult(intent, 1)
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
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AudioRecordUtil.instance.stopRecord()
        AudioTrackUtil.instance.stopTack()
    }
}