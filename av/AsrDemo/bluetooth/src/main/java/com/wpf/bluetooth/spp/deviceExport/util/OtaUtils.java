//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not
// limited to reproduction, retransmission, communication, display, mirror, download,
// modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.wpf.bluetooth.spp.deviceExport.util;

/**
 * @author Arrietty
 * 2019/9/24
 * 高通的ota在ota过程中不允许anker spp 去连接，否则mini系列ota有问题
 */
public class OtaUtils {
    private static boolean mInOtaing=false;
    public static boolean isQcmmInOtaing(){
        return mInOtaing;
    }
    public static void setInQcmmOtaing(boolean inOta){
        mInOtaing = inOta;
    }
    public static int getOnceOtaTime(long startTime){
        long totalTime =  (System.currentTimeMillis()-startTime)/1000;
        return (int) totalTime;
    }

}
