package com.wpf.bluetooth.common.util

/**
 *  Author: feipeng.wang
 *  Time:   2021/6/2
 *  Description : 指令处理发送校验，结果接收校验
 */
class CommandUtils private constructor() {
    companion object {
        private var instance: CommandUtils? = null
            get() {
                if (field == null) {
                    field = CommandUtils()
                }
                return field
            }

        fun get(): CommandUtils {
            return instance!!
        }

        /**
         * 长度字节数
         */
        private const val LENGTH_COUNT = 2

        /**
         * 校验位字节数
         */
        private const val CHECKSUM_COUNT = 1

        const val GROUP_ID_POSITION = 5
        const val COMMAND_ID_POSITION = 6
    }
    /*获取指令*/
    fun getDataCommand(cmd: ByteArray): ByteArray? {
        return assemblyCommand(cmd, null)
    }
    /*设置指令*/
    fun setActionCommand(cmdHeader: ByteArray, cmdData: Int): ByteArray? {
        val data = byteArrayOf(BytesUtil.intToByte(cmdData))
        val command = assemblyCommand(cmdHeader, data)
        return command
    }
    /**
     * 在指令头和数据中间添加长度信息，并在尾部添加checksum
     *
     * @param commandHeader 指令格式头
     * @param data          需要设置的数据
     * @return
     */
    var hasCheckSum = true
    fun assemblyCommand(commandHeader: ByteArray, data: ByteArray?): ByteArray? {
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

}