//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not
// limited to reproduction, retransmission, communication, display, mirror, download,
// modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.
//
package com.anker.bluetoothtool.deviceExport.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import com.anker.bluetoothtool.deviceExport.model.AnkerWorkDevice
import com.anker.bluetoothtool.model.ProductModel
import com.anker.bluetoothtool.deviceExport.receive.BluetoothReceiver
import com.anker.bluetoothtool.deviceExport.search.SearchBleDevice
import com.anker.bluetoothtool.deviceExport.search.SearchCallback
import com.anker.bluetoothtool.deviceExport.search.SearchDevice

/**
 * Create by Arrietty on 2020/11/7
 */
object BTDeviceHelper {
    private const val KEY_SOUNDCORE_HISTORY = "keySoundcoreHistory"

    /**
     * 搜索anker work 设备，通过callback回调信息
     */
    fun searchAnkerDevice(context: Context, productModel: ProductModel, callback: SearchCallback): SearchDevice {
        val searchDevice = SearchDevice(context)
        searchDevice.findBluetoothDevice(context, productModel, callback)
        return searchDevice
    }

    /**
     * 搜索anker work 设备，通过callback调信息
     */
    fun searchBleAnkerDevice(context: Context, productModel: ProductModel, callback: SearchCallback): SearchBleDevice {
        val searchDevice = SearchBleDevice(context)
        searchDevice.searchDeviceByUUID(productModel, callback)
        return searchDevice
    }

    /**
     * 主动释放资源
     * @param searchDevice ，通过searchAnkerDevice 返回的值
     */
    fun closeSearch(searchDevice: SearchDevice?) {
        searchDevice?.destory()
    }

    /**
     * bt action register
     * @param context
     * @return
     */
    fun registerBtBroadcast(context: Context): BluetoothReceiver {
        val receiver = BluetoothReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(receiver, intentFilter)
        return receiver
    }

    /**
     * bt action unRegister
     * @param context
     * @param receiver  registerBtBroadcast 得到的值
     * @return
     */
    fun unRegisterBtBroadcast(context: Context, receiver: BluetoothReceiver?) {
        receiver?.let {
            context.unregisterReceiver(it)
        }
    }

    /**
     * recognize anker device
     *
     * @param productModel
     * @param device
     * @return
     */
    fun recognizeAnkerDevice(context: Context, productModel: ProductModel, device: BluetoothDevice): AnkerWorkDevice? {
        val productCode = productModel.productCode
        LogDeviceE.v(device.address)
        LogDeviceE.v("getSoundCoreDevice productCode -> $productCode")
        var soundCoreDevice: AnkerWorkDevice? = null
        val sppUuid = productModel.sppUUID
        device.fetchUuidsWithSdp()
        val uuids = device.uuids
        if (!uuids.isNullOrEmpty()) {
            for (uuid in uuids) {
                val strUuid = uuid.toString()
                LogDeviceE.e(strUuid)
                if (sppUuid.equals(strUuid, true)) {
                    soundCoreDevice = AnkerWorkDevice(
                        device.name ?: "", device.address,
                        sppUuid, productCode
                    )
                    break
                }
            }
        }
        return soundCoreDevice
    }

    fun recognizeBleAnkerDevice(productModel: ProductModel, device: BluetoothDevice): AnkerWorkDevice? {
        val productCode = productModel.productCode
        LogDeviceE.v(device.address)
        LogDeviceE.v("getSoundCoreDevice productCode -> $productCode")
        val serviceUUID = productModel.serviceUUID
        val soundCoreDevice = AnkerWorkDevice(device.name ?: "", device.address, serviceUUID, productCode)
        return soundCoreDevice
    }

}