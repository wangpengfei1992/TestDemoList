package com.oceawing.blemanager

interface CommandDispatcher {
    fun dispatch(curWriteData: ByteArray?, readData: ByteArray)
}