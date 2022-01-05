package com.wpf.ffmpegtest;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

public class Utils {
    private static Utils utils;

    private Utils() {

    }
    public static Utils getInstance() {
        if (utils == null) {
            utils = new Utils();
        }
        return utils;
    }


    private String environmentFileRoot;//文件根路径
    public String initFileRoot(Context context) {
        if (TextUtils.isEmpty(environmentFileRoot)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // 先判断有没有权限
                if (!Environment.isExternalStorageManager()) {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//sd卡是否可用
                        int currentapiVersion= Build.VERSION.SDK_INT;//手机系统版本号
                        Log.e("FileHelp","SDK_INT::"+currentapiVersion);
                        if (currentapiVersion< Build.VERSION_CODES.Q){
                            environmentFileRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
                        }else {
                            File external = context.getExternalFilesDir(null);
                            if (external != null) {
                                environmentFileRoot =  external.getAbsolutePath();
                            }
                        }
                    }else {
                        environmentFileRoot= context.getFilesDir().getAbsolutePath();
                    }
                }else{
                    environmentFileRoot = "/storage/emulated/0/wpf/FFmpegTest";
                }
            }

        }
        Log.e("FileHelp","environmentFileRoot::"+environmentFileRoot);
        return environmentFileRoot;
    }
}
