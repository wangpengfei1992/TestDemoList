package com.wpf.bluetooth.spp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.wpf.bluetooth.spp.SppReadThread;
import com.wpf.bluetooth.utils.L;

import java.io.IOException;
import java.util.UUID;

/**
 * Author: feipeng.wang
 * Time:   2021/5/28
 * Description : This is description.
 */
public class SppConnectThread extends Thread {
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    public SppConnectThread(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public void run() {
        try {
            //创建一个Socket连接：只需要服务器在注册时的UUID号
            mBluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));   //UUID被用于唯一标识一个服务,如文件传输服务，串口服务、打印机服务等
            //连接
            mBluetoothSocket.connect();
            //启动接受数据
            SppReadThread mreadThread = new SppReadThread(mBluetoothSocket);
            mreadThread.start();
        } catch (IOException e) {
            L.e("客户端:连接服务端异常！断开连接重新试一试");
            e.printStackTrace();
        }
    }
}
