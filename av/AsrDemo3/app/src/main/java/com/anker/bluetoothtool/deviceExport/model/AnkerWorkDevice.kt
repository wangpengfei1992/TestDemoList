//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.
//
package com.anker.bluetoothtool.deviceExport.model

import java.io.Serializable


/**
 * Created by MD01 on 2017/12/21.
 */
data class AnkerWorkDevice(
    var deviceName: String,
    var macAddress: String, //classic mac address
    var uuid: String,
    var productCode: String,
    var productIcon: Int = 0
): Serializable