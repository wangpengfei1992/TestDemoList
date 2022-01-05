package com.anker.bluetoothtool.deviceExport.search

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import com.anker.bluetoothtool.model.ProductModel


/**
 *  Author: anker
 *  Time:   2021/5/20
 *  Description : This is description.
 */
class SearchBleDevice(private val mContext: Context) {
    private val bluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }
    private val bluetoothLeScanner by lazy { bluetoothAdapter.bluetoothLeScanner }
    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())

    private val A3301_UUID = ParcelUuid.fromString("0112F5DA-0000-1000-8000-00805F9B34FB")
//    private val A3301_UUID = ParcelUuid.fromString("0124F5DA-0000-1000-8000-00805F9B34FB")

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10 * 1000

    private val serviceListener: DeviceBleServiceListenerImpl by lazy {
        DeviceBleServiceListenerImpl()
    }

    fun searchDeviceByUUID(productModel: ProductModel, callback: SearchCallback) {
        serviceListener.setCallback(callback)
        serviceListener.setProductModel(productModel)
        scanLeDevice(productModel)
    }

    private fun scanLeDevice(productModel: ProductModel) {
        val filter = ScanFilter.Builder().setServiceUuid(
            ParcelUuid.fromString(productModel.serviceUUID)
        ).build()
//        val filter = ScanFilter.Builder().setDeviceAddress("12:34:56:C2:9B:57").build()
        val setting = ScanSettings.Builder().build()
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                scanning = false
                bluetoothLeScanner.stopScan(serviceListener)
                findBluetoothDevice(mContext)
            }, SCAN_PERIOD)
            scanning = true
            //TODO 3510 3511搜索不到
//            bluetoothLeScanner.startScan(arrayListOf(filter), setting, serviceListener)
            bluetoothLeScanner.startScan(serviceListener)
        } else {
            scanning = false
            bluetoothLeScanner.stopScan(serviceListener)
            serviceListener.deviceList.clear()
        }
    }

    /**
     * 安全使用需要在关界面时调用 closeProfile
     *
     * @param productModel
     * @param callback
     */
    fun findBluetoothDevice(context: Context) {
        closeProfile()
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