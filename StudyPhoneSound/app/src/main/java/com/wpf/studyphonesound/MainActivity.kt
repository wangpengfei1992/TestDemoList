package com.wpf.studyphonesound

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.simpleName
    private lateinit var mContext: MainActivity
    private lateinit var bluetoothUtil: BluetoothUtil
    private val CALLS_STATE = arrayOf( Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO, Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.BLUETOOTH
        , Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.MODIFY_AUDIO_SETTINGS)
    private val READ_PHONE_STATE = 1

    private lateinit var mAudioManager: AudioManager
    private var audioBufSize: Int = 0
    private var player: AudioTrack? = null
    private var mRecorder: Recorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        var start = findViewById<TextView>(R.id.start)
        var startSco = findViewById<TextView>(R.id.start_sco)
        var getPermission = findViewById<TextView>(R.id.get_permission)
        getPermission.setOnClickListener {
            /*var permission:Int = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mContext, CALLS_STATE, READ_PHONE_STATE);
            }else{
                startSco.performClick()
            }*/
            if(initPermission()){
                startSco.performClick()
            }

        }
        bluetoothUtil = BluetoothUtil.getInstance(mContext)

        startSco.setOnClickListener {
            bluetoothUtil.openSco(object: BluetoothUtil.IBluetoothConnectListener {
                override fun onSuccess() {
                    Log.e(TAG,"openSco,onSuccess")
                    startSco.text = "openSco,onSuccess"
                    start.performClick()
                }

                override fun onError(error: String?) {
                    Log.e(TAG,"openSco,error:$error")
                    startSco.text = "openSco,error"
                }

            })
        }



        var startRecord = findViewById<TextView>(R.id.start_record)
        startRecord.setOnClickListener {
            startLisenRecord()
        }
        var stopRecord = findViewById<TextView>(R.id.stop_record)
        stopRecord.setOnClickListener {
            stopLisenRecord()
        }





        start.setOnClickListener {
            var intent = Intent()
            intent.setClass(mContext,ListenPhoneService::class.java)
            mContext.startService(intent)
        }
        getPermission.performClick()
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG,"$requestCode,$resultCode")
//        if (requestCode==1)
    }

    private var saveSoundPath = ""

    private var recorder: MediaRecorder? = null
    private var record = false
    private var audioFile: File? = null
    /*开启耳机录音*/
    private fun startLisenRecord() {
/*        mAudioManager = getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager
        audioBufSize = AudioTrack.getMinBufferSize(8000,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT)
        player = AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                audioBufSize,
                AudioTrack.MODE_STREAM)
        mRecorder = Recorder(this)
        mRecorder!!.startRecord(object : Recorder.RecoderListener{
            override fun onData(data: ByteArray) {
                Log.e(TAG,"收到蓝牙录取到的数据")
                player!!.write(data, 0, data.size)
            }

        })
        player!!.play()*/

/*        saveSoundPath = Utils.getInstance().initFileRoot(mContext)+"/AphoneSound/"+ System.currentTimeMillis() + ".pcm"
        Log.e(TAG, "音频保存路径:$saveSoundPath")
        mRecorder = Recorder(this)
        mRecorder!!.startRecord(object : Recorder.RecoderListener{
            override fun onData(data: ByteArray) {
                Log.e(TAG,"收到蓝牙录取到的数据")
                Utils.getInstance().writeFile(saveSoundPath,data)
            }

        })*/

        var audiosource = MediaRecorder.AudioSource.VOICE_RECOGNITION
        if (Build.VERSION.SDK_INT > 19){
            audiosource = MediaRecorder.AudioSource.VOICE_COMMUNICATION
        }
        recorder = MediaRecorder();
//                    recorder?.setAudioSource(MediaRecorder.AudioSource.MIC);//从麦克风采集声音
        recorder?.setAudioSource(audiosource);
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //内容输出格式
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //输出到缓存目录，此处可以添加上传录音的功能，也可以存到其他位置
        var saveSoundPath = Utils.getInstance().initFileRoot(mContext)+"/AphoneSound/"+ System.currentTimeMillis()  + ".3gp"
        Log.e(TAG, "音频保存路径:$saveSoundPath")
        audioFile = File(saveSoundPath);
        if (!audioFile?.getParentFile()?.exists()!!) audioFile?.getParentFile()?.mkdirs()
        recorder?.setOutputFile(audioFile?.getAbsolutePath());
        try {
            recorder?.prepare();
        } catch (e: IOException) {
            e.printStackTrace();
        }
        recorder?.start();
        record = true
    }

    private fun stopLisenRecord() {
/*        mRecorder?.stopRecord()
        Utils.getInstance().stopWriteFile()
        Utils.getInstance().pcmToWave(saveSoundPath,saveSoundPath.replace(".pcm",".wav"))*/

        if (record){
            recorder?.stop(); //停止刻录
            recorder?.release(); //释放资源
            record = false
        }

    }

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
                ActivityCompat.requestPermissions(mContext, CALLS_STATE, READ_PHONE_STATE);
                return false
            }else{
                return true
            }
        }
    }
}