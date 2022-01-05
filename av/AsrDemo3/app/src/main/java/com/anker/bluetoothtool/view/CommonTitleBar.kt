package com.anker.bluetoothtool.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.anker.bluetoothtool.R

/**
 *  Author: philip.zhang
 *  Time:   2020/10/28
 *  Description : This is description.
 */
class CommonTitleBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val mRootView: View = inflate(context, R.layout.common_title_bar, this)
    val clRoot: ConstraintLayout
    val imgLeft: ImageView
    val imgRight: ImageView
    val imgRight2: ImageView
    val tvCenter: TextView
    val tvRight: TextView
    val tvLeft: TextView

    var mTitle: String?

    init {
        clRoot = mRootView.findViewById(R.id.clRoot)
        imgLeft = mRootView.findViewById(R.id.imgLeft)
        imgRight = mRootView.findViewById(R.id.imgRight)
        imgRight2 = mRootView.findViewById(R.id.imgRight2)
        tvCenter = mRootView.findViewById(R.id.tvCenter)
        tvRight = mRootView.findViewById(R.id.tvRight)
        tvLeft = mRootView.findViewById(R.id.tvLeft)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar)

        mTitle = typedArray.getString(R.styleable.CommonTitleBar_title)
        mTitle?.let {
            tvCenter.text = mTitle
            tvCenter.visibility = View.VISIBLE
        }
        typedArray.recycle()

    }

}