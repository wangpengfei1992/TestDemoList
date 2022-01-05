package com.anker.bluetoothtool.deviceExport.util.eventbus

/**
 *  Author: philip.zhang
 *  Time:   2020/10/26
 *  Description : EventBus消息实体类
 */
data class MessageEvent<T>(
    /**
     * 标识，防止重复订阅接收
     */
    var code: Int,
    /**
     * 真正需要的订阅数据
     */
    var data: T
)