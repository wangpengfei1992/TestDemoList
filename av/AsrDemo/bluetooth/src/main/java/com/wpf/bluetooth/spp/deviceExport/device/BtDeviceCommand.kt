//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.
//
package com.anker.ankerwork.deviceExport.device


import com.anker.libspp.SppLink
import com.wpf.bluetooth.spp.deviceExport.util.BytesUtil
import java.io.UnsupportedEncodingException
import kotlin.jvm.Throws

/**
 * @author Arrietty
 * 2019/6/13
 * add chechsume for command
 */
class BtDeviceCommand(private val hasCheckSum: Boolean) : SppLink() {
    companion object {
        /**
         * 长度字节数
         */
        private const val LENGTH_COUNT = 2

        /**
         * 校验位字节数
         */
        private const val CHECKSUM_COUNT = 1
    }

    /**
     * 在指令头和数据中间添加长度信息，并在尾部添加checksum
     *
     * @param commandHeader 指令格式头
     * @param data          需要设置的数据
     * @return
     */
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
        sendCommand(command!!)
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

    fun setWhichActionN(cmdHeader: ByteArray, cmdData: ByteArray, delay: Int = 0): ByteArray {
        val command = assemblyCommand(cmdHeader, cmdData)
        sendCommand(command!!, delay.toLong())
        return command
    }

}