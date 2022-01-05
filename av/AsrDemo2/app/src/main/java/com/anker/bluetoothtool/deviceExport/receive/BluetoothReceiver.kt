//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.
//
package com.anker.ankerwork.deviceExport.receive

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.anker.bluetoothtool.deviceExport.util.LogDeviceE
import com.anker.bluetoothtool.deviceExport.util.eventbus.EventBusManager.sendEvent
import com.anker.bluetoothtool.deviceExport.util.eventbus.EventCode
import com.anker.bluetoothtool.deviceExport.util.eventbus.MessageEvent

/**
 * Created by MD01 on 2017/8/24.
 */
class BluetoothReceiver : BroadcastReceiver() {
    private val TAG = "BluetoothReceiver"
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        LogDeviceE.e(TAG, "classic action : $action")
        if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED == action) {
            // blue connect state change
            val state = intent.getIntExtra(
                BluetoothAdapter.EXTRA_CONNECTION_STATE,
                BluetoothAdapter.ERROR
            )
            when (state) {
                BluetoothAdapter.STATE_CONNECTED -> {
                    LogDeviceE.e(TAG, "classic STATE_CONNECTED")
                    sendEvent(MessageEvent(EventCode.TYPE_BLUETOOTH_CONNECTED, device))
                }
                BluetoothAdapter.STATE_CONNECTING -> LogDeviceE.e("classic STATE_CONNECTING")
                BluetoothAdapter.STATE_DISCONNECTED -> {
                    LogDeviceE.e(TAG, "classic STATE_DISCONNECTED")
                    sendEvent(MessageEvent<Any?>(EventCode.TYPE_BLUETOOTH_DISCONNECTED, null))
                }
                BluetoothAdapter.STATE_DISCONNECTING -> LogDeviceE.e("STATE_DISCONNECTING")
            }
        } else if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            // bluetooth state change
            when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                BluetoothAdapter.STATE_OFF -> sendEvent(
                    MessageEvent<Any?>(EventCode.TYPE_BLUETOOTH_OFF, null)
                )
                BluetoothAdapter.STATE_TURNING_OFF -> {
                }
                BluetoothAdapter.STATE_ON -> sendEvent(
                    MessageEvent<Any?>(EventCode.TYPE_BLUETOOTH_ON, null)
                )
                BluetoothAdapter.STATE_TURNING_ON -> {
                }
            }
        }
    }
}