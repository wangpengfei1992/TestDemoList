package com.wpf.darkmodetest

import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.InputStream


/**
 *  Author: feipeng.wang
 *  Time:   2021/6/22
 *  Description : This is description.
 */
class WebviewAct : AppCompatActivity(){
    private lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_webview_lay)
        mContext = this
        var textWebView = findViewById<WebView>(R.id.webveiw)

        textWebView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, url)
            }
        })

        var url1 = " https://ankerwork.s3-us-west-2.amazonaws.com/legal/PC/PrivacyPolicy/AnkerWorkPolicy_EN.html"
        //"http://www.baidu.com/"
        textWebView.loadUrl(url1)
        val code = getCodeContent()
//        textWebView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('$code');parent.appendChild(style)})();");
        textWebView.loadUrl(
            "javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('$$code');parent.appendChild(style)})();"
        );
    }

    private fun getCodeContent(): String {
        val inputStream: InputStream =mContext.resources.openRawResource(R.raw.night1)
        var buffer = ByteArray(0)
        try {
            buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return Base64.encodeToString(buffer, Base64.NO_WRAP)
    }
}