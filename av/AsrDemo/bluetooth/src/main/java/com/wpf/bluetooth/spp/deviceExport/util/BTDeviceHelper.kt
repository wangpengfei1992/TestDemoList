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
import com.anker.ankerwork.deviceExport.model.AnkerWorkDevice
import com.anker.bluetoothtool.model.ProductModel
import com.anker.ankerwork.deviceExport.receive.BluetoothReceiver
import com.anker.ankerwork.deviceExport.search.SearchCallback
import com.anker.ankerwork.deviceExport.search.SearchDevice
import com.wpf.bluetooth.spp.deviceExport.util.LogDeviceE

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
    open fun recognizeAnkerDevice(context: Context, productModel: ProductModel, device: BluetoothDevice): AnkerWorkDevice? {
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
//                if (strUuid.toUpperCase().startsWith(uuidPrefix.toUpperCase())) {
//                    soundCoreDevice = AnkerWorkDevice(
//                        device.name ?: "", device.address,
//                        sppUuid, productCode
//                    )
//                    break
//                } else if (strUuid.toUpperCase().endsWith(uuidSuffix.toUpperCase())) {
//                    soundCoreDevice = AnkerWorkDevice(
//                        device.name ?: "", device.address,
//                        sppUuid, productCode
//                    )
//                    break
//                }
            }
        }
        // sometimes no uuid, use address history
//        if (soundCoreDevice == null && deviceAddressExit(context, productCode, device.address)) {
//            soundCoreDevice = AnkerWorkDevice(
//                device.name ?: "", device.address,
//                sppUuid, productCode
//            )
//        }
//        if (soundCoreDevice == null) {
//            soundCoreDevice = getDeviceByName(productModel, device, sppUuid)
//        }
        return soundCoreDevice
    }

//    private fun getDeviceByName(productModel: ProductModel, device: BluetoothDevice, sppUuid: String): AnkerWorkDevice? {
//        val name = device.name ?: ""
//        LogDeviceE.e("device name $name")
//        var soundCoreDevice: AnkerWorkDevice? = null
//        val label = AnkerWorkApplication.context.getString(productModel.lableString)
//        if (!TextUtils.isEmpty(name)) {
//            if (name.equals(label, ignoreCase = true)) {
//                soundCoreDevice = AnkerWorkDevice(
//                    device.name ?: "", device.address,
//                    sppUuid, productModel.productCode
//                )
//            }
//        }
//        return soundCoreDevice
//    }

//    private fun deviceAddressExit(context: Context, productCode: String, address: String): Boolean {
//        var retValue = false
//        val listString = SPUtil.getString(
//            context, KEY_SOUNDCORE_HISTORY + productCode,
//            null
//        )
//        if (TextUtils.isEmpty(listString)) {
//            retValue = false
//        } else {
//            val saveAddressAry = listString.split(",").toTypedArray()
//            for (saveAddress in saveAddressAry) {
//                if (saveAddress == address) {
//                    retValue = true
//                    break
//                }
//            }
//        }
//        return retValue
//    }

//    @JvmStatic
//    fun saveDeviceAddress(productCode: String, address: String) {
//        val context = AnkerWorkApplication.context
//        val key = KEY_SOUNDCORE_HISTORY + productCode
//        val listString = SPUtil.getString(context, key, null)
//        if (TextUtils.isEmpty(listString)) {
//            SPUtil.putString(context, key, address)
//            LogDeviceE.e("save soundcore address -> $address")
//        } else {
//            val saveAddressAry = listString.split(",").toTypedArray()
//            for (saveAddress in saveAddressAry) {
//                if (saveAddress == address) {
//                    return
//                }
//            }
//            val result = "$listString,$address"
//            LogDeviceE.e("save soundcore address -> $result")
//            SPUtil.putString(context, key, result)
//        }
//    }
}