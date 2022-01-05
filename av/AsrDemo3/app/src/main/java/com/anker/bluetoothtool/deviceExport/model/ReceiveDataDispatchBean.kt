//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not
// limited to reproduction, retransmission, communication, display, mirror, download,
// modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.
//
package com.anker.bluetoothtool.deviceExport.model

/**
 * @author Arrietty
 * 2019/8/20
 * 中间层事件分发时的对象分装类
 */
data class ReceiveDataDispatchBean(
    /**
     * 一般是由GroupId+command 组成
     */
    var what: Int, var commandId: Byte, var success: Boolean, var info: Any
)