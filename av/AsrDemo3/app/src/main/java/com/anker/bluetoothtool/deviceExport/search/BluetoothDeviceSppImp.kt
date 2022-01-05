package com.anker.bluetoothtool.deviceExport.search

import android.content.Context
import com.anker.bluetoothtool.deviceExport.device.BaseDeviceManager
import com.anker.bluetoothtool.deviceExport.util.BTDeviceHelper
import com.anker.bluetoothtool.deviceExport.util.BytesUtil
import com.anker.bluetoothtool.model.ProductModel

class BluetoothDeviceSppImp(private var mSppManager: BaseDeviceManager?) : IBluetoothDevice {

    private var search : SearchDevice? = null

    override fun findDevice(context: Context, productModel: ProductModel, callback: SearchCallback) {
        search = BTDeviceHelper.searchAnkerDevice(context, productModel, callback)
    }

    override fun sendCmd(bytes: String) {
        mSppManager?.commandLink?.setWhichAction(BytesUtil.toBytes(bytes))
    }

    override fun connect(context: Context, productModel: ProductModel, mac: String): Boolean {
        mSppManager?.init(productModel.sppUUID, productModel.productCode)
        val result = mSppManager?.connectDevice(mac)
        return result ?: false
    }

    override fun isConnected(): Boolean = mSppManager?.isConnected ?: false

    override fun destroy() {
        search?.destory()
        mSppManager?.destroy(true)
    }

    override fun setManager(manager: Any) {
        this.mSppManager = manager as BaseDeviceManager
    }
}