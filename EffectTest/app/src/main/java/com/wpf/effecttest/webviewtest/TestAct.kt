package com.wpf.effecttest.webviewtest

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wpf.effecttest.R

class TestAct : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test1)
        val textView =findViewById<TextView>(R.id.text_view)
        /*val webView = findViewById<ScrollWebView>(R.id.scroll_webview)
        webView.setOnOverScrolledInterface {
            var needHide = it>15
            if (needHide){
                textView.visibility = View.GONE
            }else{
                textView.visibility = View.VISIBLE
            }
        }
        var url = "https://blog.csdn.net/hesong1120/article/details/79043482"
        webView.loadUrl(url)*/

        val webView = findViewById<NestedScrollWebView>(R.id.nested_webview)
        webView.setOnOverScrolledInterface {
            var needHide = it>15
            if (needHide){
                textView.visibility = View.GONE
            }else{
                textView.visibility = View.VISIBLE
            }
        }
        var url = "https://blog.csdn.net/hesong1120/article/details/79043482"
        webView.loadUrl(url)

    }
}