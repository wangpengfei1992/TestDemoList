//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.
//
package com.anker.bluetoothtool.deviceExport.device

import com.anker.bluetoothtool.deviceExport.util.BytesUtil

/**
 * @author Arrietty
 * 计算checksum 的工具类
 */
object CheckSumUtil {
    private const val TAG = "ChecksumUtil"

    /**
     * 求checksum的值
     */
    private fun getCheckSum(data: ByteArray?): Byte {
        if (data == null) {
            return 0
        }
        var sum = 0
        for (d in data) {
            val dInt = BytesUtil.byteToInt(d)
            sum += dInt
        }
        return BytesUtil.intToByte(sum)
    }

    /**
     * add check sum to data end
     *
     * @param data
     * @return
     */
    fun getWithCheckSumData(data: ByteArray?): ByteArray? {
        if (data == null) {
            return null
        }
        val length = data.size
        val newData = ByteArray(length + 1)
        System.arraycopy(data, 0, newData, 0, length)
        newData[length] = getCheckSum(data)
        //        LogDeviceE.v(TAG, "old data =" + BytesUtil.bytesToHexString(data));
//        LogDeviceE.v(TAG, "with checksum data =" + BytesUtil.bytesToHexString(newData));
        return newData
    }

    /**
     * check this data is legally
     *
     * @param withChecksumData
     * @return
     */
    @JvmStatic
    fun isLegallyCmd(withChecksumData: ByteArray?): Boolean {
        if (withChecksumData == null) {
            return false
        }
        val length = withChecksumData.size
        val newData = ByteArray(length - 1)
        System.arraycopy(withChecksumData, 0, newData, 0, length - 1)
        val checksum = getCheckSum(newData)
        return checksum == withChecksumData[length - 1]
    }
}