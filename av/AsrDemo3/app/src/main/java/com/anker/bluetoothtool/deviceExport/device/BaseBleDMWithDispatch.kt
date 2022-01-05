package com.anker.bluetoothtool.deviceExport.device

import com.anker.bluetoothtool.deviceExport.util.BytesUtil
import com.anker.bluetoothtool.deviceExport.util.LogDeviceE
import java.util.*

/**
 * Create by Arrietty on 2020/11/10
 */
abstract class BaseBleDMWithDispatch<C : IBaseBtEventCallback, D : BaseBtDispatch<C>> : BaseBleDeviceManager() {
    var mDIEventCallback: MutableList<C> = Collections.synchronizedList(mutableListOf())
    var mDispatch: D

    init {
        mDispatch = getDispatch()
    }

    protected abstract fun getDispatch(): D
    fun addReceiveDIEventCallbackListener(listener: C) {
        mDIEventCallback.add(listener)
    }

    fun removeReceiveDIEventCallbackListener(listener: C) {
        mDIEventCallback.remove(listener)
    }

    override fun destroy() {
        super.destroy()
        mDIEventCallback.clear()
        mDispatch?.destory()
    }

    /**
     * 设备上报的所有数据的输出
     *
     * @param buff
     */
    fun cmdDataCallback(buff: ByteArray) {
        for (mc in mDIEventCallback) {
            mc.receiveCmd(buff)
        }
    }

    fun sendGetCommand(command: ByteArray) {
        val result = getWhichActionInfo(command)
        sendCmdTimeout(result)
    }

    fun setCmdAction(cmdHeader: ByteArray, cmdData: ByteArray) {
        val result = setWhichAction(cmdHeader, cmdData)
        sendCmdTimeout(result)
    }

    fun setCmdAction(cmdHeader: ByteArray, cmdData: Byte) {
        val result = setWhichAction(cmdHeader, cmdData)
        sendCmdTimeout(result)
    }

    fun sendCmdTimeout(cmdData: ByteArray?, delay: Int = 0) {
        if (cmdData != null && cmdData.size > COMMAND_ID_POSITION) {
            mDispatch.timeOut(cmdData[GROUP_ID_POSITION], cmdData[COMMAND_ID_POSITION], cmdData, delay)
        } else {
            LogDeviceE.e("sendCmdTimeout error cmdData " + BytesUtil.bytesToHexString(cmdData))
        }
    }

}