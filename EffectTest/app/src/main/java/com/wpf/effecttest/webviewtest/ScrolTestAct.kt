package com.wpf.effecttest.webviewtest

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.wpf.effecttest.R


class ScrolTestAct : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_scrol_act)
        val app_bar = findViewById<View>(R.id.my_appBarlayout) as AppBarLayout
        val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.my_toolbar_layout) as CollapsingToolbarLayout
        val second_title = findViewById<TextView>(R.id.second_title)
        val first_title = findViewById<TextView>(R.id.first_title)


        setSupportActionBar(findViewById(R.id.my_toolbar))
//        findViewById<CollapsingToolbarLayout>(R.id.my_toolbar_layout).title = title
        val webView = findViewById<ScrollWebView>(R.id.scroll_webview)
        var url = "https://blog.csdn.net/hesong1120/article/details/79043482"
        webView.loadUrl(url)






        app_bar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                if (state !== CollapsingToolbarLayoutState.EXPANDED) {
                    state = CollapsingToolbarLayoutState.EXPANDED //修改状态标记为展开
//                    collapsingToolbarLayout.setTitle("EXPANDED") //设置title为EXPANDED
                    first_title.text="主标题"
                }
            } else if (Math.abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                if (state !== CollapsingToolbarLayoutState.COLLAPSED) {
//                    collapsingToolbarLayout.setTitle("1111") //设置title不显示
//                    playButton.setVisibility(View.VISIBLE) //隐藏播放按钮
                    state = CollapsingToolbarLayoutState.COLLAPSED //修改状态标记为折叠
                    first_title.text="主标题"
                }
            } else {
                if (state !== CollapsingToolbarLayoutState.INTERNEDIATE) {
                    if (state === CollapsingToolbarLayoutState.COLLAPSED) {
//                        playButton.setVisibility(View.GONE) //由折叠变为中间状态时隐藏播放按钮
                    }
//                    collapsingToolbarLayout.setTitle("INTERNEDIATE") //设置title为INTERNEDIATE
                    first_title.text=""
                    state = CollapsingToolbarLayoutState.INTERNEDIATE //修改状态标记为中间
                }
            }
        })
    }
    private var state: CollapsingToolbarLayoutState? = null

    private enum class CollapsingToolbarLayoutState {
        EXPANDED, COLLAPSED, INTERNEDIATE
    }
}