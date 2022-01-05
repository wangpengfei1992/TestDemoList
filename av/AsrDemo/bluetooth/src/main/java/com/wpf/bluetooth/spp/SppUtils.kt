package com.wpf.bluetooth.spp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter

/**
 *  Author: feipeng.wang
 *  Time:   2021/5/28
 *  Description : 蓝牙帮助类
 */
class SppUtils private constructor(){
    companion object{
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            SppUtils()
        }
    }

    private var mBluetoothAdapter:BluetoothAdapter ?= null
    fun openBluttooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {    //是否支持蓝牙
            return
        }
        if (!mBluetoothAdapter?.enable()!!) {
            // 打开蓝牙方式一,直接打开
            mBluetoothAdapter?.enable();
            // 打开蓝牙方式二,调用对话框打开: onActivityResult()提供打开成功的回调
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, 0);
        }
    }
    fun getSearchIntent():IntentFilter{
        var intent =  IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND); // 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);    // 获取蓝牙设备的连接状态
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        /*   registerReceiver(mReceiver, intent);*/
        return intent
    }

}