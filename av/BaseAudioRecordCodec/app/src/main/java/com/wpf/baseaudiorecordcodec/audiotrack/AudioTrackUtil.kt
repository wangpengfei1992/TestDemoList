package com.wpf.baseaudiorecordcodec.audiotrack

import android.media.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.wpf.baseaudiorecordcodec.audiorecord.GlobalConfig
import com.wpf.baseaudiorecordcodec.audiorecord.GlobalConfig.Companion.AUDIO_FORMAT
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import kotlin.concurrent.thread

class AudioTrackUtil{
    companion object{
        val instance:AudioTrackUtil by lazy {
            AudioTrackUtil()
        }
    }
    var audioTack:AudioTrack?=null



    /*有延迟*/
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun playInModeStream(pcmFilePath:String){
        var minBufferSize = AudioRecord.getMinBufferSize(GlobalConfig.SAMPLE_RATE_INHZ, GlobalConfig.CHANNEL_CONFIG,
        GlobalConfig.AUDIO_FORMAT)

        var audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        var audioFormat = AudioFormat.Builder().setSampleRate(GlobalConfig.SAMPLE_RATE_INHZ)
            .setEncoding(AUDIO_FORMAT)
            .setChannelMask( GlobalConfig.CHANNEL_CONFIG)
            .build();
        audioTack = AudioTrack(audioAttributes,audioFormat,minBufferSize,AudioTrack.MODE_STREAM,AudioManager.AUDIO_SESSION_ID_GENERATE)
        audioTack?.play()
        var pcmFile = File(pcmFilePath)
        val tempBuffer = ByteArray(minBufferSize)
        thread {
            var fileInputStream = FileInputStream(pcmFile)
            try {
                while (fileInputStream.available()>0){
                    var readcount = fileInputStream.read(tempBuffer)
                    if (readcount == AudioTrack.ERROR_INVALID_OPERATION||readcount == AudioTrack.ERROR_BAD_VALUE){
                        continue
                    }
                    if (readcount != 0 && readcount != -1) {
                        audioTack?.write(tempBuffer, 0, readcount);
                    }
                }
            }catch (e:Exception){
                Log.e("wpf","出错了")
            }finally {
                fileInputStream.close()
            }
        }

    }

    /*占内存*/
    fun playInModeStatic(){

    }
    /*占内存*/
    fun stopTack(){
        if (audioTack!=null){
            audioTack?.stop()
            audioTack?.release()
        }
    }
}