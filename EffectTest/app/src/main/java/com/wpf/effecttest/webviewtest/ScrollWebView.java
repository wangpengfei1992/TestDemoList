package com.wpf.effecttest.webviewtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

import com.wpf.effecttest.R;
import com.wpf.effecttest.util.NetworkUtil;


/**
 * @author wusg
 */
public class ScrollWebView extends WebView implements NestedScrollingChild {

    private int mLastY;
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mNestedOffsetY;
    private NestedScrollingChildHelper mChildHelper;
    private OnOverScrolledInterface onOverScrolledInterface;
    private int saveScorlled;

    public ScrollWebView(Context context) {
        super(context);
        init();
    }

    public void setOnOverScrolledInterface(OnOverScrolledInterface onOverScrolledInterface) {
        this.onOverScrolledInterface = onOverScrolledInterface;
    }

    public ScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWebViewConfig();
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebViewConfig() {
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.common_bcw));
        WebSettings setting = this.getSettings();
        setting.setAllowFileAccess(true);
        setting.setAllowContentAccess(true);
        setting.setAllowFileAccessFromFileURLs(true);
        setting.setAllowUniversalAccessFromFileURLs(true);
        // 设置是否自动加载图片,包括本地图片,网络图片等,默认true.
        setting.setLoadsImagesAutomatically(true);
        // 设置是否不加载网络图片,只有上面设置为true时,此属性才生效, 默认false
        setting.setBlockNetworkImage(false);
        // 设置是否不加载网络资源, 有网络权限时,默认false, 无网络权限,默认true
        setting.setBlockNetworkLoads(false);
        // 设置是否开启build-in zoom机制, 开启后会在webView上有个缩放按钮,也支持手势缩放,默认false
        setting.setBuiltInZoomControls(true);
        // 设置是否支持缩放按钮和手势缩放网页, 默认是true
        setting.setSupportZoom(true);
        // 设置在build-in zoom 机制下,是否显示缩放按钮,默认是true
        setting.setDisplayZoomControls(false);
        // 设置是否开启overView模式, 开启这个模式的情况下, 当网页本身的宽比手机屏幕要宽时,
        // 就会缩小网页来适配手机屏幕. 当getUseWideViewPort为true时, 该值默认false
        setting.setLoadWithOverviewMode(true);
        // 设置是否支持<viewPort>标签, 当为false时,网页的宽为CSS指定的,跟设备无关。
        // 当为true时, 如果网页中有<viewport>标签并且有指定值,则网页的宽使用该值,默认提供一个默认的<viewport>
        setting.setUseWideViewPort(true);

        if (NetworkUtil.isNetworkAvailable(getContext())) {
            //根据cache-control决定是否从网络上取数据。
            setting.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //没网，则从本地获取，即离线加载
            setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        // 开启 database storage API功能
        setting.setDatabaseEnabled(true);
        // 开启 DOM storage API 功能
        setting.setDomStorageEnabled(true);
        // 开启Application Caches功能
        setting.setAppCacheEnabled(true);
        // 支持JS
        setting.setJavaScriptEnabled(true);
        // 支持插件
        setting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // 当webview调用requestFocus时为webview设置节点
        setting.setNeedInitialFocus(true);
        // 拦截保存表单数据
        setting.setSaveFormData(true);
        setting.setSupportMultipleWindows(false);

    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (scrollY != 0) {
            this.saveScorlled = scrollY;
        }
        if (onOverScrolledInterface != null) {
            onOverScrolledInterface.onOverScrolled(scrollY);
        }
    }

    public interface OnOverScrolledInterface {
        /**
         * 滑动距离
         *
         * @param scrollY
         */
        void onOverScrolled(int scrollY);
    }


    @Override
    public void goBack() {
        super.goBack();

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;

        MotionEvent trackedEvent = MotionEvent.obtain(event);

        final int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsetY = 0;
        }

        int y = (int) event.getY();

        event.offsetLocation(0, mNestedOffsetY);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                result = super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = mLastY - y;
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    trackedEvent.offsetLocation(0, mScrollOffset[1]);
                    mNestedOffsetY += mScrollOffset[1];
                }

                int oldY = getScrollY();
                mLastY = y - mScrollOffset[1];
                if (deltaY < 0) {
                    int newScrollY = Math.max(0, oldY + deltaY);
                    deltaY -= newScrollY - oldY;
                    if (dispatchNestedScroll(0, newScrollY - deltaY, 0, deltaY, mScrollOffset)) {
                        mLastY -= mScrollOffset[1];
                        trackedEvent.offsetLocation(0, mScrollOffset[1]);
                        mNestedOffsetY += mScrollOffset[1];
                    }
                }
                trackedEvent.recycle();
                result = super.onTouchEvent(trackedEvent);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (onOverScrolledInterface != null) {
                    onOverScrolledInterface.onOverScrolled(saveScorlled);
                }
                stopNestedScroll();
                result = super.onTouchEvent(event);
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                                        int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}