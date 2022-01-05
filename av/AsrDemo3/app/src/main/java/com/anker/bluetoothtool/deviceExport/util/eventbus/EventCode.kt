package com.anker.bluetoothtool.deviceExport.util.eventbus;

/**
 *  Author: philip.zhang
 *  Time:   2020/10/26
 *  Description : EventCode 唯一识别码
 */
object EventCode {

    //bluetooth
    const val TYPE_BLUETOOTH_ON = 1001
    const val TYPE_BLUETOOTH_OFF = 1002
    const val TYPE_SPP_CONNECT_ERROR = 1003
    const val TYPE_SPP_CONNECTING = 1004
    const val TYPE_BLUETOOTH_CONNECTED = 1005
    const val TYPE_BLUETOOTH_DISCONNECTED = 1006

    const val SWITCH_HOME_PAGE = 500
    const val LOG_OUT = 501
    const val HOME_DEVICE_SPP_DESTROY = 502
    const val OTA_SUCCESS = 503
    const val OTA_GAIA_TRANSFORM_SUCCESS = 504
    const val OTA_GAIA_INSTALL_SUCCESS = 505
    const val UPDATE_USER_INFO = 506
    const val DEVICE_IS_CONNECTED = 507

    //Jscall
    const val JS_CALL = 800
}
