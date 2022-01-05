package com.wpf.myviewtest

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.wpf.myviewtest.bean.Sentence

class ScrollingActivity : AppCompatActivity() {
    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        context = this
        var contentText = findViewById<TextView>(R.id.content_text)
        var txt = this.getString(R.string.large_text)
        var spannableString:SpannableString =  SpannableString(txt);
        var sentenceList = StringHelper.dealWith(txt)
        for (bean in sentenceList) {
            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    Log.e("wpf", "点击事件,${bean.content}")
                    Toast.makeText(context,"${bean.content}",Toast.LENGTH_SHORT).show()
                }

            }, bean.startPoint, bean.endPoint, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
      /* spannableString.setSpan(object: ClickableSpan() {
            override fun onClick(widget: View) {
                Log.e("wpf","点击事件1")
                StringHelper.dealWith(txt)
            }

        },0,31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/
/*         spannableString.setSpan(object: ClickableSpan() {
            override fun onClick(widget: View) {
                Log.e("wpf","点击事件2")
            }

        },31,70, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/
        contentText.setMovementMethod(LinkMovementMethod.getInstance());
        contentText.setText(spannableString);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}