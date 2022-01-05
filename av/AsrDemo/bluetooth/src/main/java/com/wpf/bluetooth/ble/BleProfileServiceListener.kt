package com.wpf.bluetooth.ble

import android.bluetooth.BluetoothProfile

/**
 *  Author: feipeng.wang
 *  Time:   2021/6/3
 *  Description : 监听停止搜索后的设备
 */
object BleProfileServiceListener : BluetoothProfile.ServiceListener{
    override fun onServiceDisconnected(profile: Int) {
        TODO("Not yet implemented")
    }

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
        TODO("Not yet implemented")
    }
}