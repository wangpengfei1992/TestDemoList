package com.wpf.asrdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wpf.asrdemo.test.DataToWavAct
import com.wpf.bluetooth.ble.BleMainAct
import com.wpf.bluetooth.common.util.startAct
import com.wpf.bluetooth.spp.SppMainAct
import com.wpf.common.permisson.PermissionUtils
import com.wpf.ffmpegcmd.FFmpegCmd
import com.wpf.opususedemo.OpusMainActivity
import java.io.File

class MainActivity : AppCompatActivity() , ActivityCompat.OnRequestPermissionsResultCallback{
    private lateinit var mContext:MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        accessFineLocation()
        accessCoarseLocation()
        readExternalStorage()
        writeExternalStorage()
        recordAudio()
        initPermission()
        var start_ble = findViewById<TextView>(R.id.start_ble)
        start_ble.setOnClickListener {
            startAct<BleMainAct>(mContext)
        }
        var start_spp = findViewById<TextView>(R.id.start_spp)
        start_spp.setOnClickListener {
            startAct<SppMainAct>(mContext)
        }

        var start = findViewById<TextView>(R.id.start_recorde)
        start.setOnClickListener {
            startAct<OpusMainActivity>(mContext)
        }
        var test1 = findViewById<TextView>(R.id.test_file)
        test1.setOnClickListener {
            startAct<DataToWavAct>(mContext)
        }


        var pcmToWav = findViewById<TextView>(R.id.start_pcm_wav)
        pcmToWav.setOnClickListener {
            if (initPermission()){
                var soundPath = "/storage/emulated/0/wpf/AsrDemo/recorder_file/"+ "recorder.pcm"
                Log.e("wpf", "pcm路径:$soundPath")
                if (File(soundPath).exists()){
                    var savePath = "/storage/emulated/0/wpf/AsrDemo/recorder_file/"+ "recorder.wav"
                    var saveFile = File(savePath)
                    if (!saveFile.parentFile.exists())saveFile.parentFile.mkdirs()
                    var audioEditor = AudioEditor()
                    audioEditor.addTextWatermark(soundPath,savePath,
                        50000,  object : FFmpegCmd.OnCmdExecListener {
                            override fun onSuccess() {
                                Log.e("wpf", "onSuccess")
                            }

                            override fun onFailure() {
                                Log.e("wpf", "onFailure")
                            }

                            override fun onProgress(progress: Float) {
                                Log.e("wpf", "$progress")
                            }

                        })
                }else{
                    Log.e("wpf", "pcm不存在")
                }

            }
        }
    }

    /*申请权限*/
    private val CALLS_STATE = arrayOf( Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE)
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


    fun accessFineLocation() {
        PermissionUtils.requestPermission(
            this,
            PermissionUtils.CODE_ACCESS_FINE_LOCATION,
            mPermissionGrant
        )
    }

    fun accessCoarseLocation() {
        PermissionUtils.requestPermission(
            this,
            PermissionUtils.CODE_ACCESS_COARSE_LOCATION,
            mPermissionGrant
        )
    }

    fun readExternalStorage() {
        PermissionUtils.requestPermission(
            this,
            PermissionUtils.CODE_READ_EXTERNAL_STORAGE,
            mPermissionGrant
        )
    }

    fun writeExternalStorage() {
        PermissionUtils.requestPermission(
            this,
            PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE,
            mPermissionGrant
        )
    }

    fun recordAudio() {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_RECORD_AUDIO, mPermissionGrant)
    }


    private val mPermissionGrant: PermissionUtils.PermissionGrant = object :
        PermissionUtils.PermissionGrant {
        override fun onPermissionGranted(requestCode: Int) {
            when (requestCode) {
                PermissionUtils.CODE_RECORD_AUDIO -> Toast.makeText(
                    this@MainActivity,
                    "Result Permission Grant CODE_RECORD_AUDIO",
                    Toast.LENGTH_SHORT
                ).show()
                PermissionUtils.CODE_GET_ACCOUNTS -> Toast.makeText(
                    this@MainActivity,
                    "Result Permission Grant CODE_GET_ACCOUNTS",
                    Toast.LENGTH_SHORT
                ).show()
                PermissionUtils.CODE_READ_PHONE_STATE -> Toast.makeText(
                    this@MainActivity,
                    "Result Permission Grant CODE_READ_PHONE_STATE",
                    Toast.LENGTH_SHORT
                ).show()
                PermissionUtils.CODE_CALL_PHONE -> Toast.makeText(
                    this@MainActivity,
                    "Result Permission Grant CODE_CALL_PHONE",
                    Toast.LENGTH_SHORT
                ).show()
                PermissionUtils.CODE_CAMERA -> Toast.makeText(
                    this@MainActivity,
                    "Result Permission Grant CODE_CAMERA",
                    Toast.LENGTH_SHORT
                ).show()
                PermissionUtils.CODE_ACCESS_FINE_LOCATION -> Toast.makeText(
                    this@MainActivity,
                    "Result Permission Grant CODE_ACCESS_FINE_LOCATION",
                    Toast.LENGTH_SHORT
                ).show()
                PermissionUtils.CODE_ACCESS_COARSE_LOCATION -> Toast.makeText(
                    this@MainActivity,
                    "Result Permission Grant CODE_ACCESS_COARSE_LOCATION",
                    Toast.LENGTH_SHORT
                ).show()
                PermissionUtils.CODE_READ_EXTERNAL_STORAGE -> Toast.makeText(
                    this@MainActivity,
                    "Result Permission Grant CODE_READ_EXTERNAL_STORAGE",
                    Toast.LENGTH_SHORT
                ).show()
                PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE -> Toast.makeText(
                    this@MainActivity,
                    "Result Permission Grant CODE_WRITE_EXTERNAL_STORAGE",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.requestPermissionsResult(
            this,
            requestCode,
            permissions,
            grantResults,
            mPermissionGrant
        )
    }
}
