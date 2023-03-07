//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.wpf.spplib;

/**
 * Created by MD01 on 2018/1/30.
 */

public interface MmiDispatcher {

    void dispatch(byte[] data, int length);
}
