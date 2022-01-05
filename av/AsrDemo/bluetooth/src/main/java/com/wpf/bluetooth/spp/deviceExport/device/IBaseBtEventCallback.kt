package com.anker.ankerwork.deviceExport.device

interface IBaseBtEventCallback {
    /**
     * 接收到的指令
     *
     * @param data
     */
    fun receiveCmd(data: ByteArray)

    /**
     * 超时的指令
     *
     * @param request
     * @param tag
     */
    fun onTimeOut(request: Int, tag: String)
}