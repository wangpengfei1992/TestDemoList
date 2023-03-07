package com.wpf.bluetoothdemo;


import com.wpf.spplib.SppLink;

/**
 * Author: feipeng.wang
 * Time:   2021/12/10
 * Description : This is description.
 */
public class DeviceManager extends SppLink {
    @Override
    public byte[] assemblyCommand(byte[] commandHeader, byte[] data) {
        return new byte[0];
    }
}
