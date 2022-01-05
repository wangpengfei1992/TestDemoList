//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.
//
package com.anker.bluetoothtool.deviceExport.search

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.ServiceListener
import android.content.Context
import com.anker.bluetoothtool.deviceExport.model.AnkerWorkDevice
import com.anker.bluetoothtool.model.ProductModel
import com.anker.bluetoothtool.deviceExport.util.BTDeviceHelper.recognizeAnkerDevice
import java.lang.ref.WeakReference

internal class DeviceServiceListenerImpl(context: Context) : ServiceListener {
    private val mContext = context.applicationContext
    var bluetoothA2dp: BluetoothA2dp? = null
        private set
    private lateinit var productModel: ProductModel

    fun setProductModel(productModel: ProductModel) {
        this.productModel = productModel
    }

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
        if (profile == BluetoothProfile.A2DP) {
            bluetoothA2dp = proxy as BluetoothA2dp
            val connectedDevices: List<BluetoothDevice>? = proxy.getConnectedDevices()

            if (!connectedDevices.isNullOrEmpty()) {
                var soundCoreDeviceList: MutableList<AnkerWorkDevice> = mutableListOf()

                for (device in connectedDevices) {
                    val soundCoreDevice = recognizeAnkerDevice(mContext, productModel, device)
                    soundCoreDevice?.run {
                        soundCoreDeviceList.add(soundCoreDevice)
                    }
                }
                callback?.apply {
                    get()?.apply {
                        when {
                            soundCoreDeviceList.isNullOrEmpty() -> {
                                onNoDevice(true)
                            }
                            soundCoreDeviceList.size == 1 -> {
                                hasOneDevice(soundCoreDeviceList[0])
                            }
                            else -> {
                                hasManyDevices(soundCoreDeviceList)
                            }
                        }

                    }
                }
            } else {
                callback?.apply {
                    get()?.apply {
                        onNoDevice(false)

                    }
                }
            }
        }
    }

    override fun onServiceDisconnected(profile: Int) {
        if (profile == BluetoothProfile.A2DP) {
            bluetoothA2dp = null
        }
    }

    private var callback: WeakReference<SearchCallback>? = null
    fun setCallback(callback: SearchCallback) {
        this.callback = WeakReference(callback)
    }
}