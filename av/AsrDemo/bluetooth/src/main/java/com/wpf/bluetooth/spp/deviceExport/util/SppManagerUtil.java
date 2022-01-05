//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not
// limited to reproduction, retransmission, communication, display, mirror, download,
// modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.wpf.bluetooth.spp.deviceExport.util;



/**
 * @author Arrietty
 * 2019/8/20
 */
public class SppManagerUtil {
    public final static int GROUP_ID_POSITION = 5;
    public final static int COMMAND_ID_POSITION = 6;
    /**
     * 根据指令的groupId(5)和cmdId(6)，获得每一条指令唯一标识ID
     *
     * @return
     */
    public static int getIDFromCmd(byte[]cmdHeader) {
        if(cmdHeader ==null || cmdHeader.length<COMMAND_ID_POSITION){
            return -1;
        }
        return getIDFromCmd(cmdHeader[GROUP_ID_POSITION], cmdHeader[COMMAND_ID_POSITION]);
    }
    public static int getIDFromCmd(byte[]cmdHeader, int groupPostion, int cmdIdPostion) {
        if(cmdHeader ==null || cmdHeader.length<cmdIdPostion){
            return -1;
        }
        return getIDFromCmd(cmdHeader[groupPostion], cmdHeader[cmdIdPostion]);
    }
    public static int getIDFromCmd(byte groupId, byte cmdId) {
        return BytesUtil.byteToInt(groupId) * 1000 + BytesUtil.byteToInt(cmdId);
    }
}
