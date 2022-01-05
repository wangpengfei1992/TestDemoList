package com.wpf.onedrivedemo

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.onedrivesdk.picker.IPicker
import com.microsoft.onedrivesdk.picker.LinkType
import com.microsoft.onedrivesdk.picker.Picker
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var mAct:MainActivity ?= null;
    private var getPermission:TextView ?= null
    private var upFile:TextView ?= null

    private var mPicker: IPicker? = null
    private val ONEDRIVE_APP_ID = "46fe1a5d-3266-4f9f-8fb5-5914e41715db"
//    private val ONEDRIVE_APP_ID = "48122D4E"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAct = this
        getPermission = findViewById(R.id.get_permisson)
        getPermission?.setOnClickListener {
 
        }
        upFile = findViewById(R.id.up_file)
        upFile?.setOnClickListener {
            mPicker = Picker.createPicker(ONEDRIVE_APP_ID)
            mPicker?.startPicking(mAct, LinkType.WebViewLink)
        }
        getHash()
    }

    private fun getHash(){
        try {
            var i:Int = 0;
            var info:PackageInfo = getPackageManager().getPackageInfo( getPackageName(),  PackageManager.GET_SIGNATURES);
            for ( signature in info.signatures) {
                i++;
                var md:MessageDigest = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                var KeyHash:String = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                //KeyHash 就是你要的，不用改任何代码  复制粘贴 ;
                Log.e("获取应用KeyHash", "KeyHash: " + KeyHash);
            }
        }
        catch (e:Exception) {
            Log.e("获取应用KeyHash", "getHash Exception" );
        }
    }
    private fun test(){

    }

}