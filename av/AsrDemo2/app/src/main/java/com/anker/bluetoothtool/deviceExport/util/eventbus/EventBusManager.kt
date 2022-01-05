package com.anker.bluetoothtool.deviceExport.util.eventbus

import org.greenrobot.eventbus.EventBus

object EventBusManager {
    /**
     * 绑定 接受者
     */
    fun register(subscriber: Any) {
        EventBus.getDefault().register(subscriber)
    }

    /**
     * 解绑定
     */
    fun unregister(subscriber: Any) {
        EventBus.getDefault().unregister(subscriber)
    }

    /**
     * 发送消息(事件)
     */
    fun sendEvent(event: MessageEvent<*>) {
        EventBus.getDefault().post(event)
    }
}