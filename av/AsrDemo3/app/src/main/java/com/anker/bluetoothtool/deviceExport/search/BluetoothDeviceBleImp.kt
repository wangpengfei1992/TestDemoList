package com.anker.bluetoothtool.deviceExport.search

import android.content.Context
import com.anker.bluetoothtool.deviceExport.device.BaseBleDeviceManager
import com.anker.bluetoothtool.deviceExport.util.BTDeviceHelper
import com.anker.bluetoothtool.deviceExport.util.BytesUtil
import com.anker.bluetoothtool.model.ProductModel

class BluetoothDeviceBleImp(private var mBleManager: BaseBleDeviceManager?) : IBluetoothDevice {

    private var search: SearchBleDevice? = null

    override fun findDevice(context: Context, productModel: ProductModel, callback: SearchCallback) {
        search = BTDeviceHelper.searchBleAnkerDevice(context, productModel, callback)
    }

    override fun sendCmd(bytes: String) {
        mBleManager?.setWhichAction(BytesUtil.toBytes(bytes))
    }

    override fun connect(context: Context, productModel: ProductModel, mac: String): Boolean {
        mBleManager?.init(context, productModel.serviceUUID, productModel.notifyChara, productModel.writeChara)
        mBleManager?.connect(false, mac)
        return true
    }

    override fun isConnected(): Boolean = mBleManager?.isConnected ?: false

    override fun destroy() {
        search?.destory()
        mBleManager?.destroy()
    }

    override fun setManager(manager: Any) {
        this.mBleManager = manager as BaseBleDeviceManager
    }
}