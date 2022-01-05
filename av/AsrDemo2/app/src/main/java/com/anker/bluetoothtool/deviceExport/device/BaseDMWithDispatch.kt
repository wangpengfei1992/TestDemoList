package com.anker.ankerwork.deviceExport.device

import com.anker.bluetoothtool.deviceExport.util.BytesUtil
import com.anker.bluetoothtool.deviceExport.util.LogDeviceE
import java.util.*

/**
 * Create by Arrietty on 2020/11/10
 */
abstract class BaseDMWithDispatch<C : IBaseBtEventCallback, D : BaseBtDispatch<C>> : BaseDeviceManager() {
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

    fun hasReceiveDIEventCallbackListener(listener: C): Boolean {
        return mDIEventCallback.contains(listener)
    }

    override fun destroy(needClear: Boolean) {
        super.destroy(needClear)
        if (needClear) {
            mDIEventCallback.clear()
        }
        mDispatch?.destory()

    }

    fun sendGetCommand(command: ByteArray) {
        val result = commandLink.getWhichActionInfo(command)
        sendCmdTimeout(result)
    }

    fun setCmdAction(cmdHeader: ByteArray, cmdData: ByteArray) {
        val result = commandLink.setWhichAction(cmdHeader, cmdData)
        sendCmdTimeout(result)
    }

    fun setCmdAction(cmdHeader: ByteArray, cmdData: Byte) {
        val result = commandLink.setWhichAction(cmdHeader, cmdData)
        sendCmdTimeout(result)
    }

    fun sendCmdTimeout(cmdData: ByteArray?) {
        if (cmdData != null && cmdData.size > COMMAND_ID_POSITION) {
            //todo 超时分发机制
            mDispatch.timeOut(cmdData[GROUP_ID_POSITION], cmdData[COMMAND_ID_POSITION], cmdData);
        } else {
            LogDeviceE.e("sendCmdTimeout error cmdData " + BytesUtil.bytesToHexString(cmdData))
        }
    }

    fun sendCmdTimeout(cmdData: ByteArray?, delay: Int) {
        if (cmdData != null && cmdData.size > COMMAND_ID_POSITION) {
            mDispatch.timeOut(cmdData[GROUP_ID_POSITION], cmdData[COMMAND_ID_POSITION], cmdData, delay);
        } else {
            LogDeviceE.e("sendCmdTimeout error cmdData " + BytesUtil.bytesToHexString(cmdData))
        }
    }

}