package com.wpf.studyphonesound

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import java.io.File
import java.io.IOException


class ListenPhoneService :Service(){
    private val TAG = ListenPhoneService::class.simpleName
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        var rootPath:String = Utils.getInstance().initFileRoot(this)
        //获取电话管理器对象
        var telephonyManager: TelephonyManager =getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //设置电话监听器，监听电话状态
        telephonyManager.listen(MyTelephoneListener(rootPath), PhoneStateListener.LISTEN_CALL_STATE);
        Log.e(TAG, "开始监听电话状态")
    }

    //定义监听内部类实现监听录音
    class MyTelephoneListener(val rootPath:String) : PhoneStateListener(){
        private val TAG = MyTelephoneListener::class.simpleName
        private var recorder: MediaRecorder? = null
        private var record = false
        private var audioFile: File? = null

        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            super.onCallStateChanged(state, phoneNumber)
            Log.e(TAG, "电话状态:$state")
            when(state){
                TelephonyManager.CALL_STATE_OFFHOOK->{
                    recorder = MediaRecorder();
                    var audiosource = MediaRecorder.AudioSource.VOICE_RECOGNITION
                    if (Build.VERSION.SDK_INT > 19){
                        audiosource = MediaRecorder.AudioSource.VOICE_COMMUNICATION
                    }
                    recorder?.setAudioSource(audiosource);//从麦克风采集声音
                    recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //内容输出格式
                    recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    //输出到缓存目录，此处可以添加上传录音的功能，也可以存到其他位置
                    var saveSoundPath = rootPath+"/AphoneSound/"+phoneNumber+"/" + System.currentTimeMillis() + ".3gp"
                    val file = File(saveSoundPath)
                    if (!file.parentFile.exists()) file.parentFile.mkdirs()
                    Log.e(TAG, "音频保存路径:$saveSoundPath")
                    audioFile = File(saveSoundPath);
                    recorder?.setOutputFile(audioFile?.getAbsolutePath());
                    try {
                        recorder?.prepare();
                    } catch (e: IOException) {
                        e.printStackTrace();
                    }
                    recorder?.start();
                    record=true;
                    Log.e(TAG,"电话已经摘机");
                }
                TelephonyManager.CALL_STATE_RINGING->{
                    val mobile: String = phoneNumber?:""
                    Log.e(TAG, "电话已响铃")
                    Log.e(TAG, mobile + "来电")
                }
                TelephonyManager.CALL_STATE_IDLE->{
                    if(record) {
                        recorder?.stop(); //停止刻录
                        recorder?.release(); //释放资源
                        Log.e(TAG, "电话空闲");
                        record=false;
                    }
                }

            }
        }
    }


}