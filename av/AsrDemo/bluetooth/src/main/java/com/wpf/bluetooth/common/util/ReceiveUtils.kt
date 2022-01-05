package com.wpf.bluetooth.common.util

/**
 *  Author: feipeng.wang
 *  Time:   2021/6/2
 *  Description : 接收数据处理
 */
class ReceiveUtils private constructor() {
    companion object {
        private var instance: ReceiveUtils? = null
            get() {
                if (field == null) {
                    field = ReceiveUtils()
                }
                return field
            }

        fun get(): ReceiveUtils {
            return instance!!
        }
        //常量
        const val MIN_CMD_LENGTH = 10
        const val COMMAND_ID_POSITION = 6
        const val CMD_DATA_FIRST_P = COMMAND_ID_POSITION + 3
        const val CMD_LENGTH_FIRST_P = COMMAND_ID_POSITION + 1
        const val SUCCESS_FLAG = 4
        const val GROUP_ID_POSITION = 5
    }
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

    fun commpareIsEquals(cmd1: ByteArray?, cmd2: ByteArray?): Boolean {
        if (cmd1 == null || cmd1.size < 9 || cmd2 == null || cmd2.size < 9) {
            return false
        }
        return cmd1[5] == cmd2[5] && cmd1[6] == cmd2[6]
    }
}