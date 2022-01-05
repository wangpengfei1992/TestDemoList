package com.wpf.baseaudiorecordcodec.audiorecord

import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.wpf.baseaudiorecordcodec.Utils
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import kotlin.concurrent.thread

class AudioRecordUtil private constructor(){
    companion object{
        val instance:AudioRecordUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            AudioRecordUtil()
        }
    }

    private var audioRecord: AudioRecord?=null
    private var recordBUfferSize = 0
    private var isRecording = false

    fun creatAudioRecord():String{
        if (recordBUfferSize==0){
            recordBUfferSize = AudioRecord.getMinBufferSize(GlobalConfig.SAMPLE_RATE_INHZ,GlobalConfig.CHANNEL_CONFIG,GlobalConfig.AUDIO_FORMAT)
        }
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,GlobalConfig.SAMPLE_RATE_INHZ,GlobalConfig.CHANNEL_CONFIG,GlobalConfig.AUDIO_FORMAT,
            recordBUfferSize)
//        var filePath = "/storage/emulated/0/wpf/record/"+System.currentTimeMillis()+".pcm"
        var filePath = "/storage/emulated/0/wpf/record/1.pcm"
        var file = File(filePath)
        if (!file.parentFile.exists())file.parentFile.mkdirs()
        isRecording = true
        audioRecord?.startRecording()

        val data = ByteArray(recordBUfferSize)
        thread {
            //开线程写入到文件中
            try {
                var fileOutputStream = FileOutputStream(file)
                while (isRecording){
                    var read = audioRecord?.read(data,0,recordBUfferSize)
                    if (read != AudioRecord.ERROR_INVALID_OPERATION){
                        fileOutputStream.write(data)
                    }
                }
                fileOutputStream.close()
            }catch (e:Exception){
                Log.e("wpf","录音到文件出错")
            }
        }
        return filePath
    }

    fun stopRecord(){
        isRecording = false
        if (audioRecord!=null){
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        }
    }

}