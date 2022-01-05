//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not
// limited to reproduction, retransmission, communication, display, mirror, download,
// modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.
//
package com.anker.ankerwork.deviceExport.search

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.anker.bluetoothtool.model.ProductModel

/**
 * Create by Arrietty on 2020/11/7
 */
class SearchDevice(context: Context) {
    private val serviceListener: DeviceServiceListenerImpl by lazy {
        DeviceServiceListenerImpl(context.applicationContext)
    }

    /**
     * 安全使用需要在关界面时调用 closeProfile
     *
     * @param productModel
     * @param callback
     */
    fun findBluetoothDevice(context: Context, productModel: ProductModel, callback: SearchCallback) {
        closeProfile()
        serviceListener.setProductModel(productModel)
        serviceListener.setCallback(callback)
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(
            context.applicationContext, serviceListener,
            BluetoothProfile.A2DP
        )
    }

    private fun closeProfile() {
        serviceListener?.let {
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.A2DP, it.bluetoothA2dp)
        }
    }

    fun destory() {
        closeProfile()
    }

}