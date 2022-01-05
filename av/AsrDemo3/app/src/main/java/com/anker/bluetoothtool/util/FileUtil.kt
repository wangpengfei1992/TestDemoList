package com.anker.bluetoothtool.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile


/**
 *  Author: feipeng.wang
 *  Time:   2021/9/26
 *  Description : This is description.
 */
class FileUtil {
    private var environmentFileRoot : String = ""//文件根路径
    fun initFileRoot(context: Context): String {
        if (TextUtils.isEmpty(environmentFileRoot)) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { //sd卡是否可用
                val currentapiVersion = Build.VERSION.SDK_INT //手机系统版本号
                Log.e("FileHelp", "SDK_INT::$currentapiVersion")
                if (currentapiVersion < Build.VERSION_CODES.Q) {
                    environmentFileRoot = Environment.getExternalStorageDirectory().getAbsolutePath()
                } else {
                    val external: File? = context.getExternalFilesDir(null)
                    if (external != null) {
                        environmentFileRoot = external.absolutePath
                    }
                }
            } else {
                environmentFileRoot = context.getFilesDir().getAbsolutePath()
            }
        }
        Log.e("FileHelp", "environmentFileRoot::$environmentFileRoot")
        return environmentFileRoot
    }


    /***
     *
     * @param path  文件路径
     * @param content 添加的内容
     * @param fileName 文件名
     * @throws IOException
     */
    @Throws(IOException::class)
    fun write(path: String?, content: String, fileName: String) {

        // 根据路径创建文件
        // 判断是否为文件
        // 如果为文件夹，获取所有文件，进行遍历
        // 获得目标文件，进行操作
        val targetFile = File(path, fileName)
        if (targetFile.parentFile != null) targetFile.parentFile.mkdirs()
        if (targetFile != null) targetFile.createNewFile()
        val raf = RandomAccessFile(targetFile, "rw")
        raf.seek(targetFile.length())
        raf.write(content.toByteArray())
        raf.close()
    }

    @Throws(IOException::class)
    fun write(path: String?, content: ByteArray, fileName: String) {

        // 根据路径创建文件
        // 判断是否为文件
        // 如果为文件夹，获取所有文件，进行遍历
        // 获得目标文件，进行操作
        val targetFile = File(path, fileName)
        if (targetFile.parentFile != null) targetFile.parentFile.mkdirs()
        if (targetFile != null) targetFile.createNewFile()
        val raf = RandomAccessFile(targetFile, "rw")
        raf.seek(targetFile.length())
        raf.write(content)
        raf.close()
    }
}