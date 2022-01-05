package com.wpf.webrtcvaddemo

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.name

    private var mSpeaking = false
    private var sample_view:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        sample_view = findViewById<TextView>(R.id.sample_text)
        sample_view?.text = stringFromJNI()
        initPermision()
        iniClick()
    }

    private fun initPermision() {
        if (!hasPermission())requestPermission()
    }

    private fun iniClick() {
        val startRecord = findViewById<TextView>(R.id.start_record)
        startRecord.setOnClickListener {
            initRecord()
            startRecord()
        }
        val stopRecord = findViewById<TextView>(R.id.stop_record)
        stopRecord.setOnClickListener {
            stopRecord()
        }
    }

    // check had permission
    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // request permission
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1
            )
        }
    }


    private var mMinBufferSize = 0
    private var mRecorder: AudioRecord? = null

    private fun initRecord() {
        mMinBufferSize = AudioRecord.getMinBufferSize(
            16000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        mRecorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            16000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            mMinBufferSize * 2
        )
    }

    private var mIsRecording = false

    // 开始录音
    private fun startRecord() {
        mIsRecording = true
        Thread(Runnable {
            try {
                var readSize: Int
                mMinBufferSize = 320
                val audioData = ShortArray(mMinBufferSize)
                if (mRecorder!!.state != AudioRecord.STATE_INITIALIZED) {
                    stopRecord()
                    return@Runnable
                }
                mRecorder!!.startRecording()
                while (mIsRecording) {
                    if (null != mRecorder) {
                        readSize = mRecorder!!.read(audioData, 0, mMinBufferSize)
                        if (readSize == AudioRecord.ERROR_INVALID_OPERATION || readSize == AudioRecord.ERROR_BAD_VALUE) {
                            continue
                        }
                        if (readSize != 0 && readSize != -1) {
                            // 语音活动检测
                            mSpeaking = webRtcVad_Process(audioData, 0, readSize)
                            if (mSpeaking) {
                                sample_view?.post{
                                    sample_view?.text = ">>>>>正在讲话"
                                }
                                Log.e(TAG, ">>>>>正在讲话")
                            } else {
                                sample_view?.post{
                                    sample_view?.text = "=====当前无声音"
                                }
                                Log.e(TAG, "=====当前无声音")
                            }
                        } else {
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }

    private fun stopRecord() {
        mIsRecording = false
        if (mRecorder != null) {
            mRecorder!!.stop()
        }
    }

    external fun webRtcVad_Process(
        audioData: ShortArray?,
        offsetInshort: Int,
        readSize: Int
    ): Boolean
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}