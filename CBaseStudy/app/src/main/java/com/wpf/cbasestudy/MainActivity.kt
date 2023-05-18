package com.wpf.cbasestudy

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wpf.cbasestudy.ecdhe.MyTestEcdH
import com.wpf.cbasestudy.ecdsa.ECDSAUtil
import com.wpf.cbasestudy.ecdsa.MyTest
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        var map = HashMap<String,String>()
        map.put("app_secret","EGOPM4613EASFGE");
        map.put("country","US");
        map.put("language","en");
        map.put("terminal-id","2711b781-b925-4b5c-94ac-1ed1358b130c");
        map.put("timestamp","1682496373");
        map.put("user-agent","Ankerwork-Windows-v2.0.1");
        map.put("x-request-nonce","180906de-daad-4d8e-91ef-5e65b72d2502");
        findViewById<TextView>(R.id.sample_text).text = signMap(map)
        Log.e("wpf","uuid::::"+getRandomUUID())

        var prePublic = "0465e62e8282edebbb468a3a805d4157049ef5085ac9f5b24fe1e3054bfe65d0b4c4471420efa152dd8e5b1945d451b266dea70f4f94050e0044820d0929497ddc"
        var prePrivate = "45ca807fc9129db62c38e9b2bfcbd84ed36ace885c0151c461aaffda10e5fffa"

        var serverPublic = "045071cfeae8dac8c7c65e5c78bce436cdd67b1e5f5e4baf3a6ebc06d0668292a27a3f9e0709b779608b05d54c4f3f02910ceb56636013ca3c63570cb5141d929c"
        var serverPrivate = "82cc8e1e7ed1dedee574bf30732245c45317178cc6c394a33eab3776813f5b6b"

        findViewById<TextView>(R.id.sample_text).setOnClickListener {

//            MyTestEcdH.testEcdh1()
//            val testEcdH = MyTestEcdH()
//        var prePublic1 = "3059301306072a8648ce3d020106082a8648ce3d030107034200046e3e60a2ecdcb361eb7c80fe020bbab50b369bb8e2fa91700e60fede51a53f8e2744767c26da062fa86b5bae97d4eeeef4167e76cad0e9a7e6256c8008898001"
//            var mykeyEnv:MyKeyEev = testEcdH.generateECKeyPair()
//            testEcdH.deriveSharedSecret(prePublic1,mykeyEnv.privateKey)

/*            val testecda = MyTest()
            testecda.generateAgreedKey(MyTest.keyToPrivate(prePrivate),MyTest.keyToPublick(prePublic));

            var mykeyEnv:MyKeyEev = testecda.nnnn()
//            val testEcdH = MyTestEcdH()
//            testecda.generateECKeyPair();*/


            val ecdsaUtil = ECDSAUtil()
            ecdsaUtil.testEcdha()
        }

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String
    external fun signMap(map:kotlin.collections.HashMap<String,String>): String


    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    fun getRandomUUID(): String {
        val m_szDevIDShort:String =
            "35" + System.currentTimeMillis().toString().substring(4)
        + Build.BRAND.length % 10
        + Build.DEVICE.length % 10
        + Build.MANUFACTURER.length % 10
        + Build.MODEL.length % 10 //13 位
        val secondStr = (System.currentTimeMillis() / 1000).toString() + createRandomInt(0, 100).toString()
        //使用硬件信息拼凑出来的15位号码
        return UUID(m_szDevIDShort.hashCode().toLong(), secondStr.hashCode().toLong()).toString()
    }

    
    var random: Random? = null

    fun createRandomInt(min: Int, max: Int): Int {
        if (random == null) random = Random()
        return random!!.nextInt(max) % (max - min + 1) + min
    }
}