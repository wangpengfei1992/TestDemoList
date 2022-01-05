package com.wpf.bluetooth.common.util

import android.util.Log
import com.wpf.bluetooth.common.bean.DataInfo
import java.util.*

object ParseData {
    const val TAG = "CmmBt.ParseData"

    /**
     * frmo 09FF000001010B0A000409FF00000102810900 to 09FF000001010B0A0004  09FF00000102810900
     */
    @JvmStatic
    fun parseDataToSegment(dataInfo: DataInfo): Boolean {
        val restBuff = dataInfo.restBuff
        val receiveData = dataInfo.curReveiveData
        val cmdArr = ArrayList<ByteArray?>()
        var mBuff = addBytes(restBuff, receiveData)
        if (mBuff == null || mBuff.isEmpty()) {
            dataInfo.restBuff = null
            Log.e(TAG, "-1-1-1-1 receive data error")
            return false
        }
        var length: Short = 0
        var restLength = mBuff.size
        while (restLength >= length) {
            mBuff = cutOutBytes(mBuff, length.toInt(), mBuff!!.size - length)
            if (mBuff == null) {
//                Log.e(TAG, "0000000receive data parse over");
                break
            }
            restLength = mBuff.size
            if (restLength < 9) {
//                Log.e(TAG, "111111111wait next receive to parse");
                break
            }
            if (mBuff[0] != 0x09.toByte() && mBuff[1] != 0xFF.toByte()) {
                //Log.e(TAG, "illegal data 1111111111 +mBuff[0] " +mBuff[0] +" mBuff[1] "+mBuff[1]);
//                length =1;
//                continue;
                mBuff = null
                break
            }
            val b = byteArrayOf(mBuff[7], mBuff[8])
            length = BytesUtil.getShort(b, false)
            //            Log.v(TAG, "length " +length);
//            Log.v(TAG, "restLength " +restLength);
            if (restLength < length) {
//                Log.e(TAG, "111111111wait next receive to parse")
                break
            }
            val subData = cutOutBytes(mBuff, 0, length.toInt())
            cmdArr.add(subData)
        }
        if (mBuff != null) {
            dataInfo.restBuff = cutOutBytes(mBuff, 0, mBuff.size)
        } else {
            dataInfo.restBuff = null
        }
        dataInfo.eventListArr = cmdArr
        return true
    }

    /**
     *
     * @param data1
     * @param data2
     * @return data1 与 data2拼接的结果
     */
    private fun addBytes(data1: ByteArray?, data2: ByteArray?): ByteArray? {
        if (data1 == null) {
            return data2
        }
        if (data2 == null) {
            return data1
        }
        val data3 = ByteArray(data1.size + data2.size)
        System.arraycopy(data1, 0, data3, 0, data1.size)
        System.arraycopy(data2, 0, data3, data1.size, data2.size)
        return data3
    }

    private fun cutOutBytes(data1: ByteArray?, startPostion: Int, length: Int): ByteArray? {
        var startPostion = startPostion
        if (startPostion < 0) {
            startPostion = 0
        }
        if (length <= 0) {
            return null
        }
        val data2 = ByteArray(length)
        System.arraycopy(data1, startPostion, data2, 0, length)
        //        Log.v(TAG ,"cutOutBytes data1.length " +data1.length + "  data2.length " +data2.length);
        return data2
    }
}