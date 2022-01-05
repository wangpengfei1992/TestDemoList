package com.wpf.ffmpegtest

object FfmpegCmdUtils {

    //音频格式转换
    fun transformAudio(srcFilePath:String,destFilePath:String):List<String>{
        var transformAudioCmd = "ffmpeg -i %s %s"
        transformAudioCmd = String.format(srcFilePath,destFilePath)
        return transformAudioCmd.split(" ")
    }

    //音频拼接
    fun conactAudio(srcFilePath:String,appentFilePath:String,destFilePath:String):List<String>{
        var transformAudioCmd = "ffmpeg -i concat:%s|%s -acodec copy %s"
        transformAudioCmd = String.format(srcFilePath,appentFilePath,destFilePath)
        return transformAudioCmd.split(" ")
    }
    //音频混合
    fun mixAudio(srcFilePath:String,maxFilePath:String,destFilePath:String):List<String>{
        var transformAudioCmd = "ffmpeg -i %s -i %s -filter_complex amix=inputs=2:duration=first -strict -2 %s"
        transformAudioCmd = String.format(srcFilePath,maxFilePath,destFilePath)
        return transformAudioCmd.split(" ")
    }
    //音频剪切
    fun cutAudio(srcFilePath:String,startTime:Int,endTime:Int,destFilePath:String):List<String>{
        var transformAudioCmd = "ffmpeg -i %s -ss %d -t %d %s"
        transformAudioCmd = String.format(srcFilePath,startTime,endTime,destFilePath)
        return transformAudioCmd.split(" ")
    }
}