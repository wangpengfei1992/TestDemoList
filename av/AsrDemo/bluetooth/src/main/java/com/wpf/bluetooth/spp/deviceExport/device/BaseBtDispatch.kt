//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not
// limited to reproduction, retransmission, communication, display, mirror, download,
// modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.
//
package com.anker.ankerwork.deviceExport.device

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.anker.ankerwork.deviceExport.model.ReceiveDataDispatchBean
import com.wpf.bluetooth.spp.deviceExport.util.BytesUtil
import com.wpf.bluetooth.spp.deviceExport.util.LogDeviceE
import com.wpf.bluetooth.spp.deviceExport.util.SppManagerUtil

import java.util.*

/**
 * @author Arrietty
 * 2019/8/20
 * 事件分发的基类，公共方法放在基类
 */
abstract class BaseBtDispatch<L : IBaseBtEventCallback>(cmdEventCallback: List<L>) {
    protected val TAG = "BaseBtDispatch"
    private val mCmdEventCallback = cmdEventCallback
    abstract fun handleCmdMessage(msg: Message)
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            handleCmdMessage(msg)
        }
    }

    fun destory() {
        mHandler.removeCallbacksAndMessages(null)
    }

    fun sendDispatchMsg(response: ReceiveDataDispatchBean) {
        mHandler.sendMessage(mHandler.obtainMessage(response.what, response))
    }

    fun copyEventCallbcks(): List<L> {
        val callbacks: MutableList<L> = ArrayList()
        synchronized(mCmdEventCallback) { callbacks.addAll(mCmdEventCallback) }
        return callbacks
    }

    fun dispatchTimeOut(id: Int, tag: String) {
        LogDeviceE.e(TAG, "dispatchTimeOut: $id")
        for (mc in copyEventCallbcks()) {
            mc.onTimeOut(id, tag)
        }
    }

    /**
     * 收到消息时做取消计时
     *
     * @param groupId
     * @param cmdId
     */
    fun removeTimeOutCallback(groupId: Byte, cmdId: Byte) {
        val request = SppManagerUtil.getIDFromCmd(groupId, cmdId)
        LogDeviceE.e(TAG, "removeTimeOutCallback: $request")
        mHandler.removeMessages(request)
    }

    /**
     * 超时
     */
    @JvmOverloads
    fun timeOut(groupId: Byte, cmdId: Byte, cmd: ByteArray?, delay: Int = 10 * 1000) {
        val request = SppManagerUtil.getIDFromCmd(groupId, cmdId)
        mHandler.removeMessages(request)
        val message = Message()
        message.what = request
        message.obj = BytesUtil.bytesToHexString(cmd)
        mHandler.sendMessageDelayed(message, delay.toLong())
    }


}