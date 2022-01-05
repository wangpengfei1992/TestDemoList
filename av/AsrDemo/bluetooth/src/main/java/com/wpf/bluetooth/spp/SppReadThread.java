package com.wpf.bluetooth.spp;

import android.bluetooth.BluetoothSocket;

import com.wpf.bluetooth.utils.L;

import java.io.IOException;
import java.io.InputStream;

/**
 * Author: feipeng.wang
 * Time:   2021/5/28
 * Description : This is description.
 */
public class SppReadThread extends Thread {
    private BluetoothSocket bluetoothSocket;

    public SppReadThread(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        InputStream is = null;
        try {
            is = bluetoothSocket.getInputStream();
            L.e("客户端:获得输入流");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        while (true) {
            try {
                if ((bytes = is.read(buffer)) > 0) {
                    byte[] buf_data = new byte[bytes];
                    for (int i = 0; i < bytes; i++) {
                        buf_data[i] = buffer[i];
                    }
                    String s = new String(buf_data);
                    L.e("客户端:读取数据了" + s);
                }
            } catch (IOException e) {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            }
        }
    }
}
