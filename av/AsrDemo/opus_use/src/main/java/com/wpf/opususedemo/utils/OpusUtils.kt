/*
 * Coding by Zhonghua. from 18-9-14 下午5:56.
 */

package com.wpf.opususedemo.utils

/**
 * JNI操作
 */
class OpusUtils {
    init {
        System.loadLibrary("opus-use")
    }

    companion object {
        private var opusUtils: OpusUtils? = null
        fun getInstant(): OpusUtils {
            if (opusUtils == null) {
                synchronized(OpusUtils::class.java) {
                    if (opusUtils == null) {
                        opusUtils = OpusUtils()
                    }
                }
            }
            return opusUtils!!
        }
    }

    external fun createEncoder(sampleRateInHz:Int, channelConfig:Int, complexity:Int, biteRate:Int = 32000): Long
    external fun encode(handle: Long, lin: ShortArray, offset: Int, encoded: ByteArray): Int
    external fun destroyEncoder(handle: Long)

    external fun createDecoder(sampleRateInHz:Int, channelConfig:Int, biteRate:Int = 32000): Long
    external fun decode(handle: Long, encoded: ByteArray, lin: ShortArray): Int
    external fun destroyDecoder(handle: Long)
}