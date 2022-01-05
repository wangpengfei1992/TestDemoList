//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not
// limited to reproduction, retransmission, communication, display, mirror, download,
// modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.
//
package com.anker.ankerwork.deviceExport.device

import android.os.SystemClock
import com.anker.bluetoothtool.deviceExport.util.LogDeviceE
import com.anker.libspp.MmiDispatcher
import com.anker.libspp.OnSppConnectStateChangeListener
import java.util.*

/**
 * Create by Arrietty on 2020/11/6
 */
abstract class BaseDeviceManager : MmiDispatcher {
    protected val TAG = this::class.java.simpleName
    val mSppConnectListener: MutableList<OnSppConnectStateChangeListener> = Collections.synchronizedList(ArrayList())
    var hasInit = false

    var mDataInfo: SppDataInfo? = null
    var commandLink: BtDeviceCommand

    init {
        commandLink = getDeviceCommand()
    }

    companion object {
        const val MIN_CMD_LENGTH = 10
        const val COMMAND_ID_POSITION = 6
        const val CMD_DATA_FIRST_P = COMMAND_ID_POSITION + 3
        const val CMD_LENGTH_FIRST_P = COMMAND_ID_POSITION + 1
        const val SUCCESS_FLAG = 4
        const val GROUP_ID_POSITION = 5
    }


    open fun getDeviceCommand() = BtDeviceCommand(true)

    open fun init(spp_uuid: String, productCode: String) {
        if (hasInit) {
            return
        }
        hasInit = true
        commandLink.setMmiDispatcher(this)
        commandLink.setOnSppConnectStateChangeListener(object : OnSppConnectStateChangeListener {
            override fun OnSppCnnError(macAddress: String?) {
                mSppConnectListener?.let {
                    for (i in mSppConnectListener.indices) {
                        mSppConnectListener[i].OnSppCnnError(macAddress)
                    }
                }
            }

            override fun OnSppConnected(macAddress: String?, deviceName: String?) {
                mSppConnectListener?.let {
                    for (i in mSppConnectListener.indices) {
                        mSppConnectListener[i].OnSppConnected(macAddress, deviceName)
                    }
//                    if (!macAddress.isNullOrEmpty() && !productCode.isNullOrEmpty()) {
//                        saveDeviceAddress(productCode, macAddress)
//                    }
                }
            }

            override fun OnSppDisconnected(macAddress: String?) {
                //LogDeviceE.d(TAG, "OnSppDisconnected(): disconnected device address : " + macAddress
                //+ " mSppConnectListener size = " + mSppConnectListener.size());
                if (mSppConnectListener.isNotEmpty()) {
                    //分发事件
                    val listeners: MutableList<OnSppConnectStateChangeListener> = mutableListOf()
                    synchronized(mSppConnectListener) { listeners.addAll(mSppConnectListener) }
                    for (listener in listeners) {
                        listener.OnSppDisconnected(macAddress)
                    }
                }
            }
        })
        commandLink.setUuid(spp_uuid)
    }

    open fun destroy(needClear: Boolean) {
        if (!hasInit) {
            return
        }
        hasInit = false
        mDataInfo = null
        disconnect()
        if (needClear) {
            mSppConnectListener.clear()
        }
    }

    /**
     * connect soundCore, need to be called in thread
     *
     * @param address
     * @return
     */
    fun connectDevice(address: String): Boolean {
        mDataInfo = SppDataInfo()
        var retryCount = 0
        var result = false
        while (retryCount < 3 && !result) {
            result = commandLink.connect(address)
            if (result) {
                break
            }
            retryCount++
            SystemClock.sleep(200)
        }
        LogDeviceE.e("AnkerWork Connect is ${if (result) "success" else "error"}")
        return result
    }

    fun disconnect() {
        LogDeviceE.e("app disconnect")
        commandLink.disconnect()
        mDataInfo = null
    }

    open fun getDeviceInfo() {

    }

    fun clearMmiQueue() {
        commandLink.clearMmiQueue()
    }

    val isConnected: Boolean
        get() = commandLink.isConnected

    val isConnecting: Boolean
        get() = commandLink.isConnecting

    fun addSppConnnectStatuListener(listener: OnSppConnectStateChangeListener) {
        mSppConnectListener!!.add(listener)
    }

    fun removeSppConnnectStatuListener(listener: OnSppConnectStateChangeListener?) {
        mSppConnectListener!!.remove(listener)
    }

    fun sendData(data: ByteArray) = commandLink.sendCommand(data)


    abstract override fun dispatch(data: ByteArray?, length: Int)


}