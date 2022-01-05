package com.wpf.bluetooth.common.util

/**
 *  Author: feipeng.wang
 *  Time:   2021/6/2
 *  Description : 常量池
 */
object Constant {
    //获取设备信息的指令
    val GET_DEVICE_INFO = byteArrayOf(
            0x08.toByte(), 0xEE.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0x03.toByte()
    )

    //接收opus的指令
    val START_OPUS = byteArrayOf(
            0x08.toByte(), 0xEE.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0xf0.toByte(), 0x81.toByte()
    )

}