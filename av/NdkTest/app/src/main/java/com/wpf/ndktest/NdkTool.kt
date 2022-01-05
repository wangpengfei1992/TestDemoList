package com.wpf.ndktest

object NdkTool {
    init {
//        System.loadLibrary("native-lib")
        System.loadLibrary("hello_c_test")
    }
    external fun stringFromJNI(): String
    external fun add(a:Int,b:Int): Int
    external fun writeFile(path:String):Unit
}