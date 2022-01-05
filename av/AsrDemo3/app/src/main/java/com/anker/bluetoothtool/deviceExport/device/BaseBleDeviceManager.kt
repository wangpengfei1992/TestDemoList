//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not
// limited to reproduction, retransmission, communication, display, mirror, download,
// modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.
//
package com.anker.bluetoothtool.deviceExport.device

import android.content.Context
import com.anker.bluetoothtool.deviceExport.util.BytesUtil
import com.oceawing.blemanager.BleManager
import com.oceawing.blemanager.CommandDispatcher
import com.oceawing.blemanager.OnBleConnectStateChangeListener
import java.io.UnsupportedEncodingException
import java.util.*
import kotlin.jvm.Throws

abstract class BaseBleDeviceManager : BleManager(), CommandDispatcher {
    val onBleConnectListener = Collections.synchronizedList<OnBleConnectStateChangeListener>(mutableListOf())
    private var hasInit = false
    var mDataInfo: SppDataInfo? = null
    var mContext: Context? = null
    protected var isWriteEquasRead = false
    override fun isSameCommand(write: ByteArray, receive: ByteArray): Boolean = true
    private val myListener: OnBleConnectStateChangeListener = object : OnBleConnectStateChangeListener {
        override fun onBleConnected(address: String) {
            for (mc in onBleConnectListener) {
                mc.onBleConnected(address)
            }
        }

        override fun onBleDisconnected(address: String) {
            for (mc in onBleConnectListener) {
                mc.onBleDisconnected(address)
            }
        }

        override fun onBleConnectFailed(address: String) {
            for (mc in onBleConnectListener) {
                mc.onBleConnectFailed(address)
            }
        }

        override fun onBleReadTimeout(address: String, command: ByteArray) {
            for (mc in onBleConnectListener) {
                mc.onBleReadTimeout(address, command)
            }
        }
    }

    fun init(context: Context, serviceUuid: String, readNotifyUuid: String, writeUuid: String) {
        if (hasInit) {
            return
        }
        onBleConnectListener.clear()
        mContext = context.applicationContext
        hasInit = true
        setParam(mContext, serviceUuid, readNotifyUuid, writeUuid, true, this)
        addOnConnectStateChangeListener(myListener)
    }

    override fun destroy() {
        if (!hasInit) {
            return
        }
        super.destroy()
        hasInit = false
        mDataInfo = null
        onBleConnectListener.clear()
        //        disconnect();//zfuyan
        removeOnConnectStateChangeListener(myListener)
    }

    /**
     * connect soundCore, need to be called in thread
     *
     * @param address
     * @return
     */
    fun connectDevice(address: String?) {
        mDataInfo = SppDataInfo()
        connect(false, address)
    }

    fun disconnect() {
        release(false)
        mDataInfo = null
    }

    fun addBleConnnectStatuListener(listener: OnBleConnectStateChangeListener) {
        onBleConnectListener.add(listener)
    }

    fun removeBleConnnectStatuListener(listener: OnBleConnectStateChangeListener?) {
        onBleConnectListener.remove(listener)
    }

//    override fun assemblyCommand(commandHeader: ByteArray, data: ByteArray): ByteArray {
//        val commandLength = commandHeader.size + 2
//        val totalLength = commandLength + data.size
//        val command = ByteArray(totalLength)
//        System.arraycopy(commandHeader, 0, command, 0, commandHeader.size)
//        val commandLengthHigh = totalLength / 256
//        val commandLengthLow = totalLength % 256
//        command[commandHeader.size] = commandLengthLow.toByte()
//        command[commandHeader.size + 1] = commandLengthHigh.toByte()
//        System.arraycopy(data, 0, command, commandLength, data.size)
//        return command
//    }

    @Synchronized
    override fun sendOrEnqueue(cmd: ByteArray) {
//        byte[] data;
//        if(ProductConstants.PRODUCT_A3163.equalsIgnoreCase(ProductConstants.CURRENT_CHOOSE_PRODUCT)){
//            data = combinateA3163Cmd(cmd,AMA_HEADER_CMD);
//        }else{
//            data =cmd;
//        }
        super.sendOrEnqueue(cmd)
    }

//    fun combinateA3163Cmd(cmd: ByteArray?, specitalCmdBit: Byte): ByteArray? {
//        if (cmd == null) {
//            return null
//        }
//        val dataLength = cmd.size
//        val header0 = ((AMA_HEADER_START shl 4) + (specitalCmdBit shr 1) and 0xFF) as Byte
//        //        byte[] d = {header0};
////        LogUtil.e("header0 " + BytesUtil.bytesToHexString(d));
//        val low = (dataLength and 0xFF).toByte()
//        val high = (dataLength shr 8 and 0xFF).toByte()
//        var ama_header_lenght_bit = 0
//        var amaArrSize = 3
//        if (dataLength >= 256) {
//            ama_header_lenght_bit = 1
//            amaArrSize = 4
//        }
//        val header1 =
//            ((specitalCmdBit shl 7) + (AMA_HEADER_RESERVE shl 1) + ama_header_lenght_bit and 0xff) as Byte
//        val ama = ByteArray(amaArrSize)
//        ama[0] = header0
//        ama[1] = header1
//        if (dataLength >= 256) {
//            ama[2] = high
//            ama[3] = low
//        } else {
//            ama[2] = low
//        }
//        val command = ByteArray(amaArrSize + cmd.size)
//        System.arraycopy(ama, 0, command, 0, amaArrSize)
//        System.arraycopy(cmd, 0, command, amaArrSize, cmd.size)
//        return command
//    }

    fun cutAMAHeader(cmd: ByteArray): ByteArray {
        val lengthBit: Int = cmd[1].toInt() and 0x01
        var lengthIndex = 3
        if (lengthBit == 1) {
            lengthIndex = 4
        }
        val command = ByteArray(cmd.size - lengthIndex)
        System.arraycopy(cmd, lengthIndex, command, 0, command.size)
        //        LogUtil.e( "cutAMAHeader command " +BytesUtil.bytesToHexString(command));
        return command
    }

    open fun commpareIsEquals(cmd1: ByteArray?, cmd2: ByteArray?): Boolean {
        if (cmd1 == null || cmd1.size < 9 || cmd2 == null || cmd2.size < 9) {
            return false
        }
        return cmd1[5] == cmd2[5] && cmd1[6] == cmd2[6]
    }

    /**
     * 在指令头和数据中间添加长度信息，并在尾部添加checksum
     *
     * @param commandHeader 指令格式头
     * @param data          需要设置的数据
     * @return
     */
    var hasCheckSum = true
    override fun assemblyCommand(commandHeader: ByteArray, data: ByteArray?): ByteArray? {
        return if (hasCheckSum) {
            commandDataHasCheckSum(commandHeader, data)
        } else {
            commandDataNoCheckSum(commandHeader, data)
        }
    }

    private fun commandDataNoCheckSum(commandHeader: ByteArray?, data: ByteArray?): ByteArray? {
        if (data == null) {
            return commandHeader
        }
        val commandLength = commandHeader!!.size + 2
        val totalLength = commandLength + data.size
        val command = ByteArray(totalLength)
        System.arraycopy(commandHeader, 0, command, 0, commandHeader.size)
        val commandLengthHigh = totalLength / 256
        val commandLengthLow = totalLength % 256
        command[commandHeader.size] = commandLengthLow.toByte()
        command[commandHeader.size + 1] = commandLengthHigh.toByte()
        System.arraycopy(data, 0, command, commandLength, data.size)
        return command
    }

    private fun commandDataHasCheckSum(commandHeader: ByteArray, data: ByteArray?): ByteArray? {
        val commandLength = commandHeader.size + LENGTH_COUNT
        var dataLength = 0
        if (data != null) {
            dataLength = data.size
        }
        val totalLength = commandLength + dataLength + CHECKSUM_COUNT
        val command = ByteArray(totalLength - CHECKSUM_COUNT)
        System.arraycopy(commandHeader, 0, command, 0, commandHeader.size)
        val lengthByte = BytesUtil.intToByteArray(totalLength)
        command[commandHeader.size] = BytesUtil.intToByte(lengthByte[3].toInt())
        command[commandHeader.size + 1] = BytesUtil.intToByte(lengthByte[2].toInt())
        if (data != null) {
            System.arraycopy(data, 0, command, commandLength, data.size)
        }
        return CheckSumUtil.getWithCheckSumData(command)
    }

    /**
     * get action info by cmd
     *
     * @param cmd
     */
    fun getWhichActionInfo(cmd: ByteArray): ByteArray? {
        val command = assemblyCommand(cmd, null)
        sendOrEnqueue(command!!)
        return command
    }

    /**
     * set action info by cmd
     *
     * @param cmd
     */
    fun setWhichAction(cmd: ByteArray): ByteArray? {
        val command = assemblyCommand(cmd, null)
        sendOrEnqueue(command!!)
        return command
    }

    fun setWhichAction(cmdHeader: ByteArray, cmdData: Int): ByteArray? {
        val data = byteArrayOf(BytesUtil.intToByte(cmdData))
        val command = assemblyCommand(cmdHeader, data)
        sendOrEnqueue(command!!)
        return command
    }

    fun setWhichAction(cmdHeader: ByteArray, cmdData: Byte): ByteArray? {
        val data = byteArrayOf(cmdData)
        val command = assemblyCommand(cmdHeader, data)
        sendOrEnqueue(command!!)
        return command
    }

    /**
     * 字符串需要转化为utf-8形式
     *
     * @param cmdHeader
     * @param cmdData
     * @throws UnsupportedEncodingException
     */
    @Throws(UnsupportedEncodingException::class)
    fun setWhichAction(cmdHeader: ByteArray, cmdData: String): ByteArray? {
        val data = cmdData.toByteArray(charset("UTF-8"))
        val command = assemblyCommand(cmdHeader, data)
        sendOrEnqueue(command!!)
        return command
    }

    fun setWhichAction(cmdHeader: ByteArray, cmdData: ByteArray): ByteArray {
        val command = assemblyCommand(cmdHeader, cmdData)
        sendOrEnqueue(command!!)
        return command
    }
//
//    fun setWhichActionN(cmdHeader: ByteArray, cmdData: ByteArray, delay: Int = 0): ByteArray {
//        val command = assemblyCommand(cmdHeader, cmdData)
//        sendCommand(command!!, delay.toLong())
//        return command
//    }

    companion object {
        const val MIN_CMD_LENGTH = 10
        const val COMMAND_ID_POSITION = 6
        const val CMD_DATA_FIRST_P = COMMAND_ID_POSITION + 3
        const val CMD_LENGTH_FIRST_P = COMMAND_ID_POSITION + 1
        const val SUCCESS_FLAG = 4
        const val GROUP_ID_POSITION = 5

        //19 80(0表示长度为1个字节,1表示长度为2个字节)_high_low
        var AMA_HEADER_START = 0x01.toByte() //0001
        var AMA_HEADER_CMD = 0x13.toByte() //10011
        var AMA_HEADER_RESERVE = 0x00.toByte() //000000

        /**
         * 长度字节数
         */
        private const val LENGTH_COUNT = 2

        /**
         * 校验位字节数
         */
        private const val CHECKSUM_COUNT = 1
    }

}