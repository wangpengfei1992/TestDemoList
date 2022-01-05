package com.anker.bluetoothtool.util

import com.anker.bluetoothtool.model.CmdFunctionModel
import com.anker.bluetoothtool.model.ProductModel

/**
 *  Author: anker
 *  Time:   2021/7/22
 *  Description : This is description.
 */
object Constant {
    val rootPath =  "/storage/emulated/0/wpf/AsrDemo2/data/"
    var saveFileName = "updata.txt"
    var saveFileName2 = "down.txt"

    var saveUpByteFileName = "upBytedata.txt"
    var saveDownByteFile = "downBytedata.txt"

    var saveUpPcmFile = "updata.pcm"
    var saveDownPcmFile = "down.pcm"
    val DEF_JSON_PRODUCT_EDITTEXT = "[\n" +
            "    {\n" +
            "        \"isBle\":false,\n" +
            "        \"notifyChara\":\"00008888-0000-1000-8000-00805F9B34FB\",\n" +
            "        \"productCode\":\"A3305\",\n" +
            "        \"serviceUUID\":\"0124F5DA-0000-1000-8000-00805F9B34FB\",\n" +
            "        \"sppUUID\":\"00001101-0000-1000-8000-00805f9b34fb\",\n" +
            "        \"writeChara\":\"00007777-0000-1000-8000-00805F9B34FB\",\n" +
            "        \"cmds\":[\n" +
            "            {\n" +
            "                \"name\":\"获取设备信息\",\n" +
            "                \"cmd\":\"08ee0000000101\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\":\"获取设备电量\",\n" +
            "                \"cmd\":\"08ee0000000103\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\":\"获取充电状态\",\n" +
            "                \"cmd\":\"08ee0000000104\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"isBle\":false,\n" +
            "        \"notifyChara\":\"00007777-0000-1000-8000-00805F9B34FB\",\n" +
            "        \"productCode\":\"A3301\",\n" +
            "        \"serviceUUID\":\"0112F5DA-0000-1000-8000-00805F9B34FB\",\n" +
            "        \"sppUUID\":\"0CF12D31-FAC3-4553-BD80-D6832E7B3301\",\n" +
            "        \"writeChara\":\"00008888-0000-1000-8000-00805F9B34FB\",\n" +
            "        \"cmds\":[\n" +
            "            {\n" +
            "                \"name\":\"获取设备信息\",\n" +
            "                \"cmd\":\"08ee0000000101\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\":\"获取设备电量\",\n" +
            "                \"cmd\":\"08ee0000000103\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\":\"获取充电状态\",\n" +
            "                \"cmd\":\"08ee0000000104\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "]"

    fun getDefProduct(isBle: Boolean = false) = mutableListOf<ProductModel>(
        ProductModel("A3305", "00001101-0000-1000-8000-00805f9b34fb", "0124F5DA-0000-1000-8000-00805F9B34FB", isBle = isBle),
        ProductModel(
            "A3301",
            "0CF12D31-FAC3-4553-BD80-D6832E7B3301",
            "0112F5DA-0000-1000-8000-00805F9B34FB",
            "00008888-0000-1000-8000-00805F9B34FB",
            "00007777-0000-1000-8000-00805F9B34FB",
            isBle
        ),
        ProductModel(
            "A3302",
            "0CF12D31-FAC3-4553-BD80-D6832E7B3302",
            "0119F5DA-0000-1000-8000-00805F9B34FB",
            "00008888-0000-1000-8000-00805F9B34FB",
            "00007777-0000-1000-8000-00805F9B34FB",
            isBle
        ),
        ProductModel(
            "A3952",
            "0cf12d31-fac3-4553-bd80-d6832e7b3952",//0cf12d31-fac3-4553-bd80-d6832e7b3952
            "0134F5DA-0000-1000-8000-00805F9B34FB",
            "00008888-0000-1000-8000-00805F9B34FB",
            "00007777-0000-1000-8000-00805F9B34FB",
            isBle
        ),
        ProductModel("A3510", "0CF12D31-FAC3-4553-BD80-D6832E7B3510", "0135F5DA-0000-1000-8000-00805F9B34FB", isBle = isBle),
    )

    fun getDefFunction(productCode: String): MutableList<CmdFunctionModel> {
        return mutableListOf<CmdFunctionModel>(
            CmdFunctionModel("获取设备信息", "08ee0000000101", productCode),
            CmdFunctionModel("获取设备电量", "08ee0000000103", productCode),
            CmdFunctionModel("获取充电状态", "08ee0000000104", productCode)
        )
    }

    /*
    *
2021-09-28 16:45:24.923 13788-13788/com.anker.bluetoothtool E/recognizeAnkerDevice: 00001101-0000-1000-8000-00805f9b34fb
2021-09-28 16:45:24.923 13788-13788/com.anker.bluetoothtool E/recognizeAnkerDevice: 0000111e-0000-1000-8000-00805f9b34fb
2021-09-28 16:45:24.923 13788-13788/com.anker.bluetoothtool E/recognizeAnkerDevice: 0000110b-0000-1000-8000-00805f9b34fb
2021-09-28 16:45:24.923 13788-13788/com.anker.bluetoothtool E/recognizeAnkerDevice: 0000110e-0000-1000-8000-00805f9b34fb
2021-09-28 16:45:24.923 13788-13788/com.anker.bluetoothtool E/recognizeAnkerDevice: 00000000-0000-1000-8000-00805f9b34fb
2021-09-28 16:45:24.923 13788-13788/com.anker.bluetoothtool E/recognizeAnkerDevice: 00000000-0000-1000-8000-00805f9b34fb
2021-09-28 16:45:24.923 13788-13788/com.anker.bluetoothtool E/recognizeAnkerDevice: 0cf12d31-fac3-4553-bd80-d6832e7b3952
    *
    * */
}