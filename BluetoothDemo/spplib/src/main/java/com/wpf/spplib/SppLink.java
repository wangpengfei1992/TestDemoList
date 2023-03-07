//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.wpf.spplib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public abstract class SppLink {

    private final static String TAG = "SppLink";

    private String mMAC;
    private String deviceName;
    private String mUuid;
    private MmiDispatcher dispatcher;
    private boolean mIsConnectOk;
    private boolean mIsConnecting;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket mbsSocket = null;
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;

    private QueuedCmdsCommander mQueuedCmdsCommander;
    private ConnectedThread mConnectedThread;

    private OnSppConnectStateChangeListener mSppStateListener;
    private boolean isIgnoreNofity=false;

    private static boolean SHOW_LOG= true;
    public static void setShowLog(boolean debug){
        SHOW_LOG = debug;
    }
    /**
     * Constructor
     */
    public SppLink() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void setUuid(String uuid) {

        this.mUuid = uuid;
    }
    public void setIgnoreNofity(boolean ignoreNofity){
        isIgnoreNofity = ignoreNofity;
    }
    public void setMmiDispatcher(MmiDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    private void logE(String msg){
        if(SHOW_LOG) {
            Log.e(TAG, msg);
        }
    }

    /**
     * Standard way to connect to SPP
     *
     * @param address BT Addr
     * @return true: Success, false: fail
     * @see OnSppConnectStateChangeListener
     */
    final public boolean connect(String address) {
        logE("connect spp start mUuid= "+mUuid + "  address " +address);


        if (!mBluetoothAdapter.isEnabled())
            return false;

        if (mIsConnectOk)
            this.disconnect();
        mIsConnecting = true;
        this.mMAC = address;

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(this.mMAC);

        deviceName = device.getName();
        if(TextUtils.isEmpty(mUuid)){
            return false;
        }
        try {

            UUID uuid = UUID.fromString(mUuid);
            //1、建立安全的蓝牙连接，会弹出配对框
//            mbsSocket = device.createRfcommSocketToServiceRecord(uuid);


            //2、建立不安全的蓝牙连接，不进行配对，就不弹出配对框
            mbsSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);

            if (mbsSocket != null) {

                mbsSocket.connect();
                mOutStream = mbsSocket.getOutputStream();
                mInStream = mbsSocket.getInputStream();
                mIsConnectOk = true;
                mIsConnecting = false;
                mQueuedCmdsCommander = new QueuedCmdsCommander();
                startConnectedThread();

            } else {
                this.disconnect();
                return false;
            }

        } catch (IOException e) {
            logE("createConn, exception:" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * disconnect from SPP
     *
     * @see OnSppConnectStateChangeListener
     */
    public synchronized void disconnect() {
        if (isConnected()) {
            this.mIsConnectOk = false;
            mIsConnecting = false;
            try {
                // 2016.5.3 Daniel commented
                stopConnectedThread();

                if (null != this.mInStream) {
                    this.mInStream.close();
                    this.mInStream = null;
                }
                if (null != this.mOutStream) {
                    this.mOutStream.close();
                    this.mOutStream = null;
                }
                if (null != this.mbsSocket) {
                    this.mbsSocket.close();
                    this.mbsSocket = null;
                }


                logE("mIsConnectOK false, normal");
            } catch (IOException e) {

                this.mInStream = null;
                this.mOutStream = null;
                this.mbsSocket = null;
                this.mIsConnectOk = false;
                mIsConnecting = false;
            }
            clearMmiQueue();
            notifySppDisconnected(mMAC);
        }
    }

    public boolean isConnected() {
        return this.mIsConnectOk;
    }
    public boolean isConnecting() {
        return this.mIsConnecting;
    }

    private final static char[] HEX_ARRAY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};

    private String bytesToHexString(byte[] bytes, int length) {
        char[] hexChars = new char[length * 2];
        for (int j = 0; j < length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * for extended feature
     *
     * @param sData
     * @return
     */
    public int sendCommand(byte[] sData) {
        if(sData.length<50){
            logE( bytesToHexString(sData, sData.length));
        }
        if (isConnected()) {
            try {
                if(mOutStream ==null){
                    logE("mOutStream =null");
                    this.disconnect();
                    return -4;
                }
                mOutStream.write(sData);
                mOutStream.flush();
                return sData.length;
            } catch (IOException e) {
                logE("send data error");
                this.disconnect();
                return -3;
            }
        } else {
            return -2;
        }
    }
    private synchronized void startConnectedThread() {
        if (mConnectedThread != null) {
//            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread();
        mConnectedThread.start();
    }

    private void stopConnectedThread() {
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    private byte[] buffer;

    /**
     * Thread to connect the Bluetooth socket and start the thread that reads from the socket.
     */
    private class ConnectedThread extends Thread {

        private boolean mmIsRunning;

        public ConnectedThread() {

            mmIsRunning = true;
        }

        public void run() {

            notifySppConnected();

            while (mmIsRunning) {
                try {

                    if (buffer == null) {
                        buffer = new byte[1024];
                    }
                    if (!mIsConnectOk || mInStream == null || mbsSocket == null) {
                        return;
                    }
                    int length = mInStream.read(buffer);
                    logE("read byte result: " + length);

                    if (length > 0) {

                        if (dispatcher != null) {
                            dispatcher.dispatch(buffer, length);
                        }
                        if(!isIgnoreNofity){
                            // send next cmd, if had
                            checkQueuedActions();
                        }

                    }

                } catch (IOException ioe) {
                    logE("Connected io exec: " + ioe.getMessage());
                    disconnect();
                } catch (IndexOutOfBoundsException ioobe) {
                    logE("Connected thread ioobe");
                    disconnect();
                } catch (Exception e) {
                    logE("Connected thread Except: " + e.getMessage());
                }
            }

        }

        public void cancel() {
            mmIsRunning = false;
            logE("ConnectedThread cancel");
        }
    }

    /**
     * Force to clear all pending MMI commands
     * For Airoha Engineers only
     */
    public synchronized void clearMmiQueue() {
        if (mQueuedCmdsCommander != null) {
            mQueuedCmdsCommander.isResponded = true;
            mQueuedCmdsCommander.clearQueue();
        }
    }

    public void setOnSppConnectStateChangeListener(OnSppConnectStateChangeListener listener) {

        this.mSppStateListener = listener;
    }

    private void notifySppDisconnected(String mac) {
        if (mSppStateListener != null) {
            mSppStateListener.OnSppDisconnected(mac);
        }
    }

    private void notifySppConnected() {
        if (mSppStateListener != null) {
            mSppStateListener.OnSppConnected(mMAC, deviceName);
        }
    }

    /**
     * MMI api should use this, Queue mechanism is invoked
     */
    public synchronized void sendOrEnqueue(byte[] cmd) {
        if (!isConnected()) {
            return;
        }

        if (mQueuedCmdsCommander.isQueueEmpty() && mQueuedCmdsCommander.isResponded) {
            logE( "soe: cmd send");
            sendCommand(cmd);
            mQueuedCmdsCommander.isResponded = false;
        } else {
            logE("soe: cmd enqueue"+bytesToHexString(cmd,cmd.length));
            mQueuedCmdsCommander.enqueueCmd(cmd);
            if(isIgnoreNofity){
                sendNextCmdIgnoreNotify();
            }
        }
    }

    private void sendNextCmdIgnoreNotify(){
        checkQueuedActions();
    }
    /**
     * check MMI API send messages
     */
    private synchronized void checkQueuedActions() {

        logE("checkQueuedActions set responded");
        mQueuedCmdsCommander.isResponded = true;

        byte[] nextCmd = mQueuedCmdsCommander.getNextCmd();

        if (nextCmd != null) {
            // delay a little bit, FW leak
            if(isIgnoreNofity){
                SystemClock.sleep(50);
            }else{
                SystemClock.sleep(500);
            }

            sendCommand(nextCmd);
        }
    }

    public abstract byte[] assemblyCommand(byte[] commandHeader, byte[] data);

}
