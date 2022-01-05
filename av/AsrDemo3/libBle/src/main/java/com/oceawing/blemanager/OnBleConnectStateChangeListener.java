package com.oceawing.blemanager;

public interface OnBleConnectStateChangeListener {
    void onBleConnected(String address);

    void onBleDisconnected(String address);

    void onBleConnectFailed(String address);

    void onBleReadTimeout(String address, byte[] command);
}
