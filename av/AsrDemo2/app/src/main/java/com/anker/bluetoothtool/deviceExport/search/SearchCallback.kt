package com.anker.ankerwork.deviceExport.search

import com.anker.ankerwork.deviceExport.model.AnkerWorkDevice

/**
 * Create by Arrietty on 2020/11/7
 */
interface SearchCallback {
    /*
     *根据条件筛选是否有当前选择的产品连接，
     * hasConnectedDevice表示有蓝牙设备连接，但不一定是选择的产品
     */
    fun onNoDevice(hasConnectedDevice: Boolean)
    fun hasOneDevice(device: AnkerWorkDevice)
    fun hasManyDevices(list: List<AnkerWorkDevice>)
}