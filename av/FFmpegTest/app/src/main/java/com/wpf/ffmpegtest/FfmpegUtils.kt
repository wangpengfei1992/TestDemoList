package com.wpf.ffmpegtest

class FfmpegUtils {



    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    external fun handle(commands: Array<String>): Int
}