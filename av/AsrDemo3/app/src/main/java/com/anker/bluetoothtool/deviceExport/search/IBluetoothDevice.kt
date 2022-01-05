package com.anker.bluetoothtool.deviceExport.search

import android.content.Context
import com.anker.bluetoothtool.model.ProductModel

interface IBluetoothDevice {

    fun findDevice(context: Context, productModel: ProductModel, callback: SearchCallback)

    fun sendCmd(bytes: String)

    fun connect(context: Context, productModel: ProductModel, mac: String): Boolean

    fun destroy()

    fun isConnected(): Boolean

    fun setManager(manager: Any)
}