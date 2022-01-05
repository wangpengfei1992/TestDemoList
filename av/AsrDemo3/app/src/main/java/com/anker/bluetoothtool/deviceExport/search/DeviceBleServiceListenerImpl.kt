package com.anker.bluetoothtool.deviceExport.search

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import com.anker.bluetoothtool.deviceExport.model.AnkerWorkDevice
import com.anker.bluetoothtool.deviceExport.util.BTDeviceHelper
import com.anker.bluetoothtool.deviceExport.util.BytesUtil
import com.anker.bluetoothtool.model.ProductModel
import java.lang.StringBuilder
import java.lang.ref.WeakReference

/**
 *  Author: anker
 *  Time:   2021/5/20
 *  Description : This is description.
 */
class DeviceBleServiceListenerImpl : ScanCallback(), BluetoothProfile.ServiceListener {

    val deviceList = hashMapOf<String, BluetoothDevice>()
    var bluetoothA2dp: BluetoothA2dp? = null
        private set
    private lateinit var productModel: ProductModel

    fun setProductModel(productModel: ProductModel) {
        this.productModel = productModel
    }

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        super.onScanResult(callbackType, result)
        val byte = result.scanRecord?.bytes
        byte?.let {
            val btMac = parseBleDataToMac(it)
            deviceList[btMac] = result.device
//            Log.d("haha", "mac === " + btMac)
        }
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        callback?.get()?.onNoDevice(false)
        Log.d("haha", "onScanFailed errorCode === " + errorCode)
    }


    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
        if (profile == BluetoothProfile.A2DP) {
            bluetoothA2dp = proxy as BluetoothA2dp
            val connectedDevices: List<BluetoothDevice>? = proxy.getConnectedDevices()
            if (!connectedDevices.isNullOrEmpty()) {
                val bleList = deviceList.filter { connectedDevices.map { device -> device.address }.contains(it.key) }.values.toMutableList()
                filterAnkerDevice(bleList)
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

    fun filterAnkerDevice(connectedDevices: List<BluetoothDevice>) {
        if (!connectedDevices.isNullOrEmpty()) {
            var soundCoreDeviceList: MutableList<AnkerWorkDevice> = mutableListOf()

            for (device in connectedDevices) {
                val soundCoreDevice = BTDeviceHelper.recognizeBleAnkerDevice(productModel, device)
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

        deviceList.clear()
    }

    private var callback: WeakReference<SearchCallback>? = null
    fun setCallback(callback: SearchCallback) {
        this.callback = WeakReference(callback)
    }

    private fun parseBleDataToMac(bytes: ByteArray): String {
        val macHex = BytesUtil.bytesToHexString(bytes).substring(10, 22)
        val macAddress = StringBuilder()
        macHex.forEachIndexed { index, c ->
            macAddress.append(c.toUpperCase())
            if (index % 2 != 0) {
                macAddress.append(":")
            }
        }
        return macAddress.run { substring(0, lastIndex).toString() }
    }
}