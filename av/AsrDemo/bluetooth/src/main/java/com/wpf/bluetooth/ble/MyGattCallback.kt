package com.wpf.bluetooth.ble

import android.bluetooth.*
import android.os.Build
import androidx.annotation.RequiresApi
import com.wpf.bluetooth.common.bean.DataInfo
import com.wpf.bluetooth.common.util.*
import com.wpf.bluetooth.utils.L


/**
 *  Author: feipeng.wang
 *  Time:   2021/5/31
 *  Description : ble设备连接过程
 *  1.连接成功，再发现服务，调用bluetoothGatt.discoverServices()
 *  2.在onServicesDiscovered回调，找到的服务
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class MyGattCallback(val callBack:(readCharacteristic:BluetoothGattCharacteristic?,
writeCharacteristic:BluetoothGattCharacteristic?)->Unit) : BluetoothGattCallback() {
    private val TAG = javaClass.simpleName
    var bluetoothGatt: BluetoothGatt ?= null
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            LogUtil.e(TAG,"连接成功,查找服务")
            // 连接成功,查找服务
            bluetoothGatt?.discoverServices()
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // 连接断开
            L.e("onConnectionStateChange fail-->$status")
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            //发现设备，遍历服务，初始化特征
            initBLE(gatt)
        } else {
            L.e("onServicesDiscovered fail-->$status")
        }
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            // 收到的数据
            val receiveByte = characteristic.value
            dispatchValueAndCheckCommandQueue(characteristic.value)
            L.e("onCharacteristicRead 收到的数据-->$receiveByte")
        } else {
            L.e("onCharacteristicRead fail-->$status")
        }
    }



    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        super.onCharacteristicChanged(gatt, characteristic)
        //当特征中value值发生改变
        var value: ByteArray? = characteristic.getValue()
        //对特征值进行解析
        LogUtil.e(TAG,"onCharacteristicChanged:::${BytesUtil.bytesToHexString(value)}")
    }

    /**
     * 收到BLE终端写入数据回调
     * @param gatt
     * @param characteristic
     * @param status
     */
    override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        super.onCharacteristicWrite(gatt, characteristic, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            // 发送成功
            L.e("onCharacteristicWrite 发送成功-->")
        } else {
            // 发送失败
            L.e("onCharacteristicWrite 发送失败-->")
        }
    }

    override fun onDescriptorWrite(gatt: BluetoothGatt,
                                   descriptor: BluetoothGattDescriptor, status: Int) {
        super.onDescriptorWrite(gatt, descriptor, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
        }
    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
        super.onReadRemoteRssi(gatt, rssi, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
        }
    }

    override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
        super.onDescriptorRead(gatt, descriptor, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
        }
    }
    
    /*遍历service,找到读写特性*/
    //初始化特征
    fun initBLE(gatt: BluetoothGatt?) {
        LogUtil.e(TAG,"initBLE")
        if (gatt == null) {
            return
        }
        var read:BluetoothGattCharacteristic ?= null
        var write:BluetoothGattCharacteristic ?= null
        var bGS:BluetoothGattService ?= null
        //遍历所有服务
        for (BluetoothGattService in gatt.services) {
           L.e( "--->BluetoothGattService" + BluetoothGattService.uuid.toString())
            if (BleUtils.serviceUuid == BluetoothGattService.uuid) {
                //根据写UUID找到写特征
                bGS = BluetoothGattService
            }
            //遍历所有特征
            for (bluetoothGattCharacteristic in BluetoothGattService.characteristics) {
                L.e("---->gattCharacteristic：${bluetoothGattCharacteristic.uuid.toString()}")
                val str = bluetoothGattCharacteristic.uuid
                if (str == BleUtils.notifyCharactUuid) {
                    //根据写UUID找到写特征
                    write = bluetoothGattCharacteristic
                } else if (str == BleUtils.writeCharactUuid) {
                    //根据通知UUID找到通知特征
                    read = bluetoothGattCharacteristic

                    // set notification enabled
                    setNotificationEnabled(read)
                }
            }
        }
        callBack(read,write);
    }
    //开启支持读写
    @Synchronized
    fun setNotificationEnabled(character: BluetoothGattCharacteristic): Unit {
        // notification enabled
        bluetoothGatt?.setCharacteristicNotification(character, true)
        // solve bug in android 6.0 huawei nova5------
        character.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT

        // -------------------------------
        for (descriptor in character.descriptors) {

            //PROPERTY_WRITE  可写
            //PROPERTY_READ 可读
            //PROPERTY_NOTIFY具备通知属性
            if (character.properties or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            } else if (character.properties or BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
                descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            }
            bluetoothGatt?.writeDescriptor(descriptor)
        }
    }


    //处理接收的数据
    private var mDataInfo: DataInfo ?= null
    protected var isWriteEquasRead = false
    private fun dispatchValueAndCheckCommandQueue(value: ByteArray) {
        val data = value
        val length = data.size
        if (mDataInfo == null) {
            mDataInfo = DataInfo()
        }
        mDataInfo!!.curReveiveData = BytesUtil.cutOutBytes(data, 0, length)
        //拆分包，处理粘包问题
        val parseResult = ParseData.parseDataToSegment(mDataInfo!!)
        if (!parseResult) {
            return
        }

//        isWriteEquasRead = false
        mDataInfo!!.eventListArr?.run {
            val it: Iterator<ByteArray?> = this.iterator()
            while (it.hasNext()) {
                val subData = it.next()
               /* if (!isWriteEquasRead) {
                    isWriteEquasRead = ReceiveUtils.get().commpareIsEquals(wCmmd, subData)
                }*/

                val hexString = BytesUtil.bytesToHexString(subData, subData!!.size)
                LogUtil.e(hexString)
                //检测代码checksum值是否正常
                val isLegally = CheckSumUtil.isLegallyCmd(subData)
                if (subData == null || subData.size < ReceiveUtils.MIN_CMD_LENGTH || !isLegally) {
                    LogUtil.e("receive illegal data")
                    continue
                }
                val success_flag = 0x01.toByte()
                val success = subData[ReceiveUtils.SUCCESS_FLAG] == success_flag
                val commandGroup = subData[ReceiveUtils.GROUP_ID_POSITION]
                val commandId = subData[ReceiveUtils.COMMAND_ID_POSITION]

                //todo 需要按照项目情况修改
                val haha = subData
                LogUtil.e(TAG, "收到的数据：" + BytesUtil.bytesToHexString(haha))
            }
        }
    }
}