//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.wpf.spplib;

/**
 * There is no Android's origin event for SPP state changes.
 *
 * @author Daniel.Lee
 */

public interface OnSppConnectStateChangeListener {

    /**
     * SPP connected
     *
     * @param macAddress
     * @param deviceName
     */
    void OnSppConnected(String macAddress, String deviceName);

    /**
     * SPP disconnected
     */
    void OnSppDisconnected(String macAddress);
}
