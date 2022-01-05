package com.wpf.bluetooth.ble

import android.bluetooth.*
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wpf.bluetooth.R
import com.wpf.bluetooth.common.util.BytesUtil
import com.wpf.bluetooth.common.util.CommandUtils
import com.wpf.bluetooth.common.util.Constant
import com.wpf.bluetooth.common.util.LogUtil
import com.wpf.bluetooth.utils.L
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap


/**
 *  Author: feipeng.wang
 *  Time:   2021/5/28
 *  Description : ble蓝牙测试主类
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BleMainAct : AppCompatActivity() {
    private val TAG = javaClass.simpleName
    private var searchTv: TextView? = null
    private var linkTv: TextView? = null
    private var sendCommandTv: TextView? = null
    private var receiveTv: TextView? = null
    private lateinit var context: Context

    //data
    private var isScanBle = false
    private var mBluetoothGatt:BluetoothGatt ?= null
    private var readBG: BluetoothGattCharacteristic?= null
    private var writeBG: BluetoothGattCharacteristic?= null
    //设备
    val deviceList5 = hashMapOf<String, BluetoothDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ble_demo_act)
        context = this
        initView()
        initClick()

    }

    override fun onDestroy() {
        super.onDestroy()
        disConnect()
    }

    private fun initView() {
        searchTv = findViewById(R.id.ble_seach)
        linkTv = findViewById(R.id.ble_connect)
        sendCommandTv = findViewById(R.id.ble_send_comond)
        receiveTv = findViewById(R.id.ble_show_content)
    }



    private fun initClick() {
        BleUtils.instance.openBluttooth(context)
        searchTv?.setOnClickListener {
            lifecycleScope.launch {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    isScanBle = true
                    //4.0搜索
/*                     BleUtils.instance.mBluetoothAdapter?.startLeScan(mLeScanCallback)
                    delay(1000 * 15)
                    BleUtils.instance.mBluetoothAdapter?.stopLeScan(mLeScanCallback)*/
                    //5.0搜索
                    BleUtils.instance.mBluetoothAdapter?.bluetoothLeScanner?.startScan(mScanCallback)
                    delay(1000 * 15)
                    BleUtils.instance.mBluetoothAdapter?.bluetoothLeScanner?.stopScan(mScanCallback)
                    isScanBle = false
                }

            }
        }
        linkTv?.setOnClickListener {
            //停止搜索
            if (isScanBle&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BleUtils.instance.mBluetoothAdapter?.bluetoothLeScanner?.stopScan(mScanCallback)
            }

            //获取所需地址 //12:34:43:C2:9B:54  A3028_Tommy
            val mDeviceAddress: String = "12:34:43:C2:9B:54"
            val bluetoothDevice = deviceList5[mDeviceAddress]
            if (bluetoothDevice == null){
                LogUtil.e(TAG,"未找到需要连接的设备，${mDeviceAddress}")
                return@setOnClickListener
            }
            val gattCallback = MyGattCallback{
                read,write->
                run {
                    LogUtil.e(TAG,"连接成功，找到读写特性")
                    readBG = read
                    writeBG = write
                    mBluetoothGatt?.readCharacteristic(readBG)
                }
            }
            mBluetoothGatt= bluetoothDevice?.connectGatt(context, false, gattCallback)
            gattCallback.bluetoothGatt = mBluetoothGatt

        }
        sendCommandTv?.setOnClickListener {
//            var cmdByteArray = CommandUtils.get().getDataCommand(Constant.GET_DEVICE_INFO)//获取设备信息
            var cmdByteArray = CommandUtils.get().setActionCommand(Constant.START_OPUS,1)
            LogUtil.e(TAG,"sendCommandTv:::${BytesUtil.bytesToHexString(cmdByteArray)}")
            writeBG?.value = cmdByteArray
            mBluetoothGatt?.writeCharacteristic(writeBG)
        }
        receiveTv?.setOnClickListener {

        }
    }

    //蓝牙扫描回调接口
    private var deviceMap4: HashMap<String, BluetoothDevice> = HashMap<String, BluetoothDevice>()
    private val mLeScanCallback =
            LeScanCallback { device, rssi, scanRecord ->
                run {
                    //可以将扫描的设备弄成列表，点击设备连接，也可以根据每个设备不同标识，自动连接。
                    L.e("--->搜索到的蓝牙名字：${device.name}\n${device.address}")
                    if (Build.VERSION.SDK_INT >= 21) {
                        if (!deviceMap4?.contains(device.address)) {
                            deviceMap4[device.address] = device
                        }
                    }
                }
            }

    //搜索回调
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private val mScanCallback:ScanCallback =  object :ScanCallback(){
        //批量搜索回调
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
        }
        //搜索失败
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val byte = result.scanRecord?.bytes
            byte?.let {
                val btMac = BleUtils.instance.parseBleDataToMac(it)
                if (!deviceList5.containsKey(btMac)){
                    deviceList5[btMac] = result.device
                    Log.e(TAG, "mac === $btMac,${result.device.name}")
                }
                //F4:4E:FC:9A:72:62
                //PowerConfBLE
            }
        }
    }





    //断开设备
    open fun disConnect(): Boolean {
        if (mBluetoothGatt != null) {
            mBluetoothGatt?.disconnect()
            mBluetoothGatt?.close()
            mBluetoothGatt = null
            return true
        }
        return false
    }

}