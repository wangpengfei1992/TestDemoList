package com.anker.bluetoothtool.model


import java.io.Serializable


data class ProductModel(
    var productCode: String = "A3305",
    var sppUUID: String = "00001101-0000-1000-8000-00805f9b34fb",
    var serviceUUID: String = "0124F5DA-0000-1000-8000-00805F9B34FB",
    var writeChara: String = "00007777-0000-1000-8000-00805F9B34FB",
    var notifyChara: String = "00008888-0000-1000-8000-00805F9B34FB",
    var isBle: Boolean = false,
    var createTime: Long = 0
) : Serializable