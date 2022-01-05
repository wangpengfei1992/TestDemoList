package com.anker.bluetoothtool.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Author: philip.zhang
 * Time:   2021/1/4
 * Description : This is description.
 */
public class LinearItemDecoration extends RecyclerView.ItemDecoration {
    private int leftMargin = 0;
    private int rightMargin = 0;
    private int mDividerHeight = 2;
    private int dividerColor;
    private Paint mPaint;
    private Context mContext;
    private Paint whitePaint;

    public LinearItemDecoration(Context context, int dividerColor) {
        this.dividerColor = dividerColor;
        mContext = context;


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);


        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setColor(Color.WHITE);
        whitePaint.setStyle(Paint.Style.FILL);
    }

    public void setDividerHeight(int height) {
        this.mDividerHeight = dp2px(height);
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = dp2px(leftMargin);
    }

    public void setRightMargin(int rightMargin) {
        this.rightMargin = dp2px(rightMargin);
    }


    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);


        final int childCount = parent.getChildCount();
        @SuppressLint("RtlGetPadding")
        int left = parent.getPaddingLeft();
        @SuppressLint("RtlGetPadding")
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerHeight;
            if (leftMargin != 0) {
                canvas.drawRect(left, top, left + leftMargin, bottom, whitePaint);
            }
            if (rightMargin != 0) {
                canvas.drawRect(right - rightMargin, top, right, bottom, whitePaint);
            }
            canvas.drawRect(left + leftMargin, top, right - rightMargin, bottom, mPaint);
        }
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, mDividerHeight);
    }

    private int dp2px(int dp) {
        return (int) (mContext.getResources().getDisplayMetrics().density * dp);
    }
}
