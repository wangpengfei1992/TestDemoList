//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.wpf.spplib;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Daniel.Lee on 2016/3/28.
 */
public class QueuedCmdsCommander {
    // commands need to be send
    // collections of MMICmd
    private final BlockingQueue<byte[]> cmds;

    public QueuedCmdsCommander() {

        cmds = new LinkedBlockingQueue<>();
    }

    public byte[] getNextCmd() {
        return cmds.isEmpty() ? null : cmds.remove();
    }

    public void enqueueCmd(byte[] cmd) {
        try {
            cmds.put(cmd);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isQueueEmpty(){
        return cmds.isEmpty();
    }

    // is sync done
    public boolean isResponded = true;

    public void clearQueue(){
        cmds.clear();
    }

}
