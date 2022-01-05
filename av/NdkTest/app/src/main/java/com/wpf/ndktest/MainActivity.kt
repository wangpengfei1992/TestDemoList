package com.wpf.ndktest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import com.wpf.ndktest.util.Utils
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var context:Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        var sampleText = findViewById<TextView>(R.id.sample_text)
        requestPermission()
        sampleText.text = NdkTool.stringFromJNI()
        sampleText.setOnClickListener {
            NdkTool.add(1,2).toString()
//            NdkTool.writeFile(path);
        }
//        Build.VERSION_CODES.M
    }
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                writeFile()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + context.getPackageName())
//                startActivityForResult(intent, 1)
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
                writeFile()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    1
                )
            }
        } else {
            writeFile()
        }
    }
    fun writeFile(){
        var path = Utils.getInstance().initFileRoot(this)+"/"+"NdkTest"+"/dkfile.txt"
        val file = File(path)
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        Log.e("wpf","文件夹路径：：${file.parentFile.path}\n${file.parentFile.absolutePath}")
        Log.e("wpf","文件夹是否存：：${file.parentFile.exists()}")
    }
}