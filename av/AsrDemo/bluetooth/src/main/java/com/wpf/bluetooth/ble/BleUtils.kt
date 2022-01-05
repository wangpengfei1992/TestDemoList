package com.wpf.bluetooth.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import com.wpf.bluetooth.common.util.BytesUtil
import java.lang.StringBuilder
import java.util.*

/**
 *  Author: feipeng.wang
 *  Time:   2021/5/28
 *  Description : 蓝牙帮助类
 */
class BleUtils private constructor(){
    companion object{

        val serviceUuid: UUID = UUID.fromString("0124F5DA-0000-1000-8000-00805F9B34FB")
        //写通道uuid
        val writeCharactUuid: UUID = UUID.fromString("00007777-0000-1000-8000-00805F9B34FB")
        //通知通道 uuid
        val notifyCharactUuid: UUID = UUID.fromString("00008888-0000-1000-8000-00805F9B34FB")


        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            BleUtils()
        }
    }

    var mBluetoothAdapter:BluetoothAdapter ?= null

    fun getBlutoothAdapter(context:Context): BluetoothAdapter? {
        if (mBluetoothAdapter==null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                val mBluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                mBluetoothAdapter = mBluetoothManager.adapter
            } else {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
        }
        return mBluetoothAdapter
    }

    fun openBluttooth(context:Context){
        if (mBluetoothAdapter == null) {    //是否支持蓝牙
            getBlutoothAdapter(context)
        }
        if (!mBluetoothAdapter?.enable()!!) {
            // 打开蓝牙方式一,直接打开
            mBluetoothAdapter?.enable();
            // 打开蓝牙方式二,调用对话框打开: onActivityResult()提供打开成功的回调
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, 0);
        }
    }

    fun parseBleDataToMac(bytes: ByteArray): String {
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