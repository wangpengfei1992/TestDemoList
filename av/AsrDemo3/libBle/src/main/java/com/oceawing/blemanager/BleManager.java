package com.oceawing.blemanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * 1. 对 BluetoothGatt 操作  (read/write)Characteristic() , (read/write)Descriptor() 和  readRemoteRssi() 都是异步操作。需要特别注意的是，同时只能有一个操作.
 * 开发建议：把这写操作都封装成同步操作，一个操作回调之前，阻塞主其他调用
 * 2. BLE 设备的建立和断开连接的操作，例如 BluetoothDevice.connectGatt() ,  BluetoothGatt.connect() , BluetoothGatt.disconnect() 等操作最好都放在主线程中，否则你会遇到很多意想不到的麻烦
 */
public abstract class BleManager {
    //    private static final String TAG = BleManager.class.getSimpleName();
    private static final String TAG = "BleManager";
    public static long WRITE_DELAY = 100L;
    public static long CONNECT_DELAY = 200L;
    public static long SET_MTU_DELAY = 100L;
    public static long DISCOVERY_DELAY = 50L;
    public static long READ_TIMEOUT = 5 * 1000L;
    public static long CONNECT_TIMEOUT = 10 * 1000L;
    private BluetoothGattService mBluetoothGattService;
    private BluetoothGattCharacteristic readNotifyCharacter;
    private BluetoothGattCharacteristic writeCharacter;
    private String serviceUuid;
    private String readNotifyUuid;
    private String writeUuid;
    private String mAddress;
    private BluetoothGatt mBluetoothGatt;
    //connect success and find servics connectState =conntected
    private int connectState = BluetoothGatt.STATE_DISCONNECTED;
    private ArrayList<WeakReference<OnBleConnectStateChangeListener>> connectListenerList;
    private boolean isInited;
    private boolean isRelease;
    private QueuedCmdsCommander commandQueue;
    private CommandDispatcher commandDispatcher;
    private boolean requestMTU = true;
    private byte[] writeCommand;
    private Context mContext;
    private static final int MSG_WHAT_READ_TIMEOUT = 1;
    private static final int MSG_WHAT_CONNECT_DEVICE_TIMEOUT = 2;
    private static final int MSG_WHAT_START_DISCOVER_SERVICE = 3;
    private static final int MSG_WHAT_WRITE_CMD = 4;
    private static final int MSG_WHAT_WRITE_CMD_WAIT = 5;
    private static final int MSG_WHAT_SET_MTU = 6;
    private Handler mainHandler;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isInited) {
                return;
            }
            switch (msg.what) {
                case MSG_WHAT_CONNECT_DEVICE_TIMEOUT:
                    Log.e(TAG, "MSG_WHAT_CONNECT_DEVICE_TIMEOUT tryTimes " + tryTimes);
                    if (needTryConnect((String) msg.obj)) {
                        Log.e(TAG, "MSG_WHAT_CONNECT_DEVICE_TIMEOUT 1111 ");
                        status133();
                        connect(true, (String) msg.obj);
                    } else {
                        Log.e(TAG, "MSG_WHAT_CONNECT_DEVICE_TIMEOUT 222222 ");
                        release(true);
                    }
                    break;
                case MSG_WHAT_START_DISCOVER_SERVICE:
                    BluetoothGatt gatt = (BluetoothGatt) msg.obj;
                    Log.e(TAG, "MSG_WHAT_START_DISCOVER_SERVICE111111111");
                    if (gatt != null) {
                        Log.e(TAG, "MSG_WHAT_START_DISCOVER_SERVICE");
                        gatt.discoverServices();
                    }
                    break;
                case MSG_WHAT_SET_MTU:
                    BluetoothGatt gatt1 = (BluetoothGatt) msg.obj;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && requestMTU) {
                        if (gatt1 != null) {
                            boolean result = gatt1.requestMtu(MTU);
                        }
                    }

                    break;
                case MSG_WHAT_WRITE_CMD:
                    if (!isConnected() || writeCharacter == null) {
                        return;
                    }
                    writeCommand = (byte[]) msg.obj;
                    Log.i(TAG, "write command = " + byteArrayToHexString(writeCommand));
                    writeCharacter.setValue(writeCommand);
                    mBluetoothGatt.writeCharacteristic(writeCharacter);
                    break;
                case MSG_WHAT_WRITE_CMD_WAIT:
                    if (!isConnected()) {
                        return;
                    }
                    Log.e(TAG, "MSG_WHAT_WRITE_CMD_WAIT");
                    checkQueuedActions();
                    break;
                case MSG_WHAT_READ_TIMEOUT:
                    if (!isConnected() || writeCharacter == null) {
                        return;
                    }
                    byte[] command = (byte[]) msg.obj;
                    notifyConnectStateChange(TYPE_READ_TIMEOUT, command, mAddress);
                    checkQueuedActions();
                    break;
            }
        }
    };

    public void setParam(Context context, String serviceUuid, String readNotifyUuid, String writeUuid, boolean requestMTU, CommandDispatcher dispater) {
        this.mContext = context;
        this.serviceUuid = serviceUuid;
        this.readNotifyUuid = readNotifyUuid;
        this.writeUuid = writeUuid;
        this.requestMTU = requestMTU;
        // init state change listener
        connectListenerList = new ArrayList<>();
        isInited = true;
        commandQueue = new QueuedCmdsCommander();
        commandDispatcher = dispater;
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void destroy() {
        isInited = false;
        release(false);
        connectListenerList.clear();
    }

    private boolean needTryConnect(String address) {
        if (!isActiveDisconnect && tryTimes < 2 && isInited && !TextUtils.isEmpty(address) && address.equalsIgnoreCase(mAddress)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * connect device
     */
    private long setUpCnnTime;
    private int tryTimes = 0;
    private boolean isActiveDisconnect;

    public void connect(boolean retryConnect, final String address) {
        if (retryConnect) {
            if (TextUtils.isEmpty(address) || !address.equalsIgnoreCase(mAddress) || connectState == BluetoothGatt.STATE_CONNECTING) {
                return;
            }
            tryTimes++;
            Log.e(TAG, "retryConnect " + retryConnect + " tryTimes " + tryTimes);
        } else {
            tryTimes = 0;
            mAddress = address;
            isActiveDisconnect = false;
        }
        setUpCnnTime = System.currentTimeMillis();
        isRelease = false;
        connectState = BluetoothGatt.STATE_CONNECTING;

        if (mContext == null || TextUtils.isEmpty(address) || !isInited || BluetoothAdapter.getDefaultAdapter() == null) {
            notifyConnectStateChange(TYPE_CONNECT_FAILED, null, address);
            return;
        }

        final BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        if (device == null) {
            Log.e(TAG, "connect remote device =null");
            notifyConnectStateChange(TYPE_CONNECT_FAILED, null, address);
            return;
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "mainHandler device " + device);
                if (device != null) {
                    mBluetoothGatt = device.connectGatt(mContext.getApplicationContext(), false, mBluetoothGattCallback);
                }
                if (mBluetoothGatt == null) {
                    Log.e(TAG, "connectGatt == null");
                    notifyConnectStateChange(TYPE_CONNECT_FAILED, null, address);
                    return;
                }
                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_WHAT_CONNECT_DEVICE_TIMEOUT, address), CONNECT_TIMEOUT);
            }
        }, CONNECT_DELAY);
    }


    /**
     * disconnect device
     */
    private void disconnect() {
        if (/*isConnected()*/true) {
//            mainHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if(mBluetoothGatt!=null){
//                        mBluetoothGatt.disconnect();
//                    }
//                }
//            });
            if (mBluetoothGatt != null) {
                mBluetoothGatt.disconnect();
                Log.e(TAG, "disconnect device -> " + getConnectedAddress());
            }
            connectState = BluetoothGatt.STATE_DISCONNECTED;
            clearMmiQueue();
        }
    }

    private void close() {
//        mainHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (mBluetoothGatt != null) {
//                    Log.e(TAG, " close device -> " + getConnectedAddress());
//                    mBluetoothGatt.close();
//                }
//                mBluetoothGatt = null;
//            }
//        });
        connectState = BluetoothGatt.STATE_DISCONNECTED;
        if (mBluetoothGatt != null) {
            Log.e(TAG, " close device -> " + getConnectedAddress());
            mBluetoothGatt.close();
        }
        mBluetoothGatt = null;

    }

    /**
     * disconnect or connect failed, and then release resource
     */

    private void status133() {
        mHandler.removeCallbacksAndMessages(null);
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        disconnect();
        clearMmiQueue();
        close();
    }

    public void release(boolean connectFailed) {
        Log.e(TAG, "release 00000000000 isRelease " + isRelease);
        if (isRelease) {
            return;
        }
        isActiveDisconnect = true;
        Log.e(TAG, "release 11111111  ");
        disconnect();
        clearMmiQueue();
        close();
        mHandler.removeCallbacksAndMessages(null);
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }

        if (connectFailed) {
            notifyConnectStateChange(TYPE_CONNECT_FAILED, null, mAddress);
            Log.v(TAG, "notifyConnectStateChange  connectFail " + connectFailed);
        } else {
            notifyConnectStateChange(TYPE_DISCONNECTED, null, mAddress);
            Log.v(TAG, "notifyConnectStateChange  disconnected ble  ");
        }
        isRelease = true;
    }

    /**
     * Force to clear all pending MMI commands
     * For Airoha Engineers only
     */
    public synchronized void clearMmiQueue() {
        if (commandQueue != null) {
            commandQueue.isResponded = true;
            commandQueue.clearQueue();
        }
    }

    public String getConnectedAddress() {

        if (mBluetoothGatt != null && mBluetoothGatt.getDevice() != null) {
            return mBluetoothGatt.getDevice().getAddress();
        }
        return null;
    }

    public boolean isConnected() {
        if (mBluetoothGatt == null) {
            return false;
        }
        return connectState == BluetoothGatt.STATE_CONNECTED;
    }

    /**
     * write command
     */
    public synchronized void sendOrEnqueue(final byte[] command) {
        if (!isConnected() || writeCharacter == null || command == null) {
            return;
        }
        if (commandQueue.isQueueEmpty() && commandQueue.isResponded) {
            Log.e(TAG, "soe: cmd send " + byteArrayToHexString(command));
//            commandQueue.isResponded = false;
            sendCommand(command);

        } else {
            Log.e(TAG, "soe: cmd enqueue " + byteArrayToHexString(command));
            commandQueue.enqueueCmd(command);
        }

    }

    //this fun has bug ,because framework bug
    private synchronized void writeWithoutQuery(final byte[] command) {

        if (!isConnected() || writeCharacter == null || command == null) {
            return;
        }
        if (commandQueue.isQueueEmpty() && commandQueue.isResponded) {
            Log.e(TAG, "soe: cmd send");
            commandQueue.isResponded = false;
            sendCommand(command);

        } else {
            Log.e(TAG, "soe: cmd enqueue");
            commandQueue.enqueueCmd(command);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_WHAT_WRITE_CMD_WAIT, command), WRITE_DELAY);
        }

        int a = 0xff;
    }

    private void sendCommand(final byte[] command) {
        if (!isConnected() || writeCharacter == null || command == null) {
            commandQueue.isResponded = true;
            return;
        }

        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_WHAT_WRITE_CMD, command), WRITE_DELAY);
//        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_WHAT_READ_TIMEOUT, command), READ_TIMEOUT);
    }

    private synchronized void setNotificationEnabled(BluetoothGattCharacteristic character) {
        if (isConnected() || character == null || mBluetoothGatt == null) {
            return;
        }
        // notification enabled
        mBluetoothGatt.setCharacteristicNotification(character, true);
        // solve bug in android 6.0 huawei nova5------
        character.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

        // -------------------------------
        for (BluetoothGattDescriptor descriptor : character.getDescriptors()) {

            //PROPERTY_WRITE  可写
            //PROPERTY_READ 可读
            //PROPERTY_NOTIFY具备通知属性
            if ((character.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            } else if ((character.getProperties() | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            }
            mBluetoothGatt.writeDescriptor(descriptor);
        }

//        BluetoothGattDescriptor defaultDescriptor = character.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
//        if (null != defaultDescriptor) {
//            defaultDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(defaultDescriptor);
//        }

    }

    /**
     * byte array to hex string
     */
    private String byteArrayToHexString(byte[] data) {
        if (data == null) return null;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int v = data[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

    /**
     * gatt callback instance
     */
    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(TAG, "onConnectionStateChange status " + status + " newState " + newState);
            String address = null;
            if (gatt != null && gatt.getDevice() != null) {
                address = gatt.getDevice().getAddress();
            }
            if (status == BluetoothGatt.GATT_SUCCESS) {
                switch (newState) {
                    case BluetoothGatt.STATE_CONNECTED:
                        if (isConnected()) {
                            return;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && requestMTU) {
                            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_WHAT_SET_MTU, gatt), SET_MTU_DELAY);

                        } else {
                            Log.i(TAG, "start discoverServices");
                            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_WHAT_START_DISCOVER_SERVICE, gatt), DISCOVERY_DELAY);
                        }
                        break;
                    case BluetoothGatt.STATE_DISCONNECTED:

                        Log.e(TAG, "STATE_DISCONNECTED address " + address);
                        if (needTryConnect(address)) {
                            Log.e(TAG, "STATE_DISCONNECTED then retry");
                            status133();
                            connect(true, address);
                        } else {
                            Log.e(TAG, "STATE_DISCONNECTED release resource");
                            release(false);
                        }
                        break;
                }

            } else {
                Log.e(TAG, "failed, status" + status);
                if (status == 133 && needTryConnect(address)) {
                    status133();
                    connect(true, address);
                } else {
                    release(status == 132 || status == 133);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (isConnected()) {
                return;
            }

            mBluetoothGattService = gatt.getService(UUID.fromString(serviceUuid));
            Log.e(TAG, "onServicesDiscovered, serviceUuid  " + serviceUuid);
            if (mBluetoothGattService != null && gatt.getDevice() != null) {

                List<BluetoothGattCharacteristic> cList = mBluetoothGattService.getCharacteristics();
                readNotifyCharacter = mBluetoothGattService.getCharacteristic(UUID.fromString(readNotifyUuid));
                if (readNotifyCharacter != null) {

                    // set notification enabled
                    setNotificationEnabled(readNotifyCharacter);

                    writeCharacter = mBluetoothGattService.getCharacteristic(UUID.fromString(writeUuid));
                    if (writeCharacter != null) {
                        mHandler.removeMessages(MSG_WHAT_CONNECT_DEVICE_TIMEOUT);
                        connectState = BluetoothGatt.STATE_CONNECTED;
                        mBluetoothGatt = gatt;
                        notifyConnectStateChange(TYPE_CONNECTED, null, gatt.getDevice().getAddress());

                    } else {
                        Log.e(TAG, "writeCharacter not found");
                        disconnect();
                    }
                } else {
                    Log.e(TAG, "readNotifyCharacter not found");
                    disconnect();
                }
            } else {
                Log.e(TAG, "service not found");
                disconnect();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (characteristic == null || characteristic.getValue() == null) {
                return;
            }
            Log.i(TAG, "onCharacteristicChanged value length " + characteristic.getValue().length);
            Log.i(TAG, "onCharacteristicChanged value length " + byteArrayToHexString(characteristic.getValue()));
            try {
                dispatchValueAndCheckCommandQueue(characteristic.getValue());
            } catch (Exception e) {
                Log.e(TAG, "dispatchValueAndCheckCommandQueue exception " + e.getMessage());
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (characteristic == null || characteristic.getValue() == null) {
                return;
            }
//            Log.i(TAG, "onCharacteristicRead value length" + characteristic.getValue().length);
//            Log.i(TAG, "onCharacteristicRead value length" + byteArrayToHexString(characteristic.getValue()));
//            dispatchValueAndCheckCommandQueue(characteristic.getValue());

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {//
                Log.v(TAG, "onCharacteristicWrite success");
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                Log.v(TAG, "onCharacteristicWrite fail");
            } else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) {
                Log.v(TAG, "onCharacteristicWrite no permition");
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.i(TAG, "onMtuChanged ");
            MTU = mtu;
            if (isConnected()) {
                return;
            }
            Log.i(TAG, "onMtuChanged then start discoverServices");
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_WHAT_START_DISCOVER_SERVICE, gatt), DISCOVERY_DELAY);
        }
    };

    private void dispatchValueAndCheckCommandQueue(byte[] value) {

        //do not change the order dispatch and isSameCommand
        if (value != null && commandDispatcher != null) {
            commandDispatcher.dispatch(writeCommand, value);
        }
        // send next command, if have
//        Log.e(TAG, "last write " + byteArrayToHexString(writeCommand));
//        Log.e(TAG, "read " + byteArrayToHexString(value));
//        boolean same = isSameCommand(writeCommand, value);
//        Log.v(TAG, "is same " + same);
//        if (same) {
//            if (mHandler != null) {
//                mHandler.removeMessages(MSG_WHAT_READ_TIMEOUT);
//            }
        checkQueuedActions();

//        }
    }

    /**
     * check MMI API send messages
     */
    private synchronized void checkQueuedActions() {

//        Log.e(TAG, "checkQueuedActions set responded");
        commandQueue.isResponded = true;

        byte[] nextCmd = commandQueue.getNextCmd();

        if (nextCmd != null) {
            // delay a little bit, FW leak
            Log.e(TAG, "checkQueuedActions 22222222 " + byteArrayToHexString(nextCmd));
            sendCommand(nextCmd);
        }
    }


    public void addOnConnectStateChangeListener(OnBleConnectStateChangeListener listener) {
        if (connectListenerList != null) {
            for (WeakReference<OnBleConnectStateChangeListener> connectStateChangeListenerWeakReference : connectListenerList) {
                if (connectStateChangeListenerWeakReference.get() == listener) {
                    return;
                }
            }
            connectListenerList.add(new WeakReference(listener));
        }
    }

    public void removeOnConnectStateChangeListener(OnBleConnectStateChangeListener listener) {
        if (connectListenerList != null) {

            final Iterator<WeakReference<OnBleConnectStateChangeListener>> iterator =
                    connectListenerList.iterator();
            while (iterator.hasNext()) {

                final WeakReference<OnBleConnectStateChangeListener> next = iterator.next();
                if (next.get() == listener) {
                    iterator.remove();
                    break;
                }
            }
        }
    }


    /**
     * assembly command use header and data
     */
    public abstract byte[] assemblyCommand(byte[] header, byte[] data);

    public abstract boolean isSameCommand(byte[] write, byte[] receive);

    //暂不使用
    public int MTU = 512;

    private void writeLCommand(final byte[] command) {
        if (!isConnected() || writeCharacter == null || command == null) {
            commandQueue.isResponded = true;
            return;
        }
        Log.i(TAG, "write command = " + byteArrayToHexString(command));

        final int count = command.length / MTU;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isConnected() || writeCharacter == null) {
                    return;
                }
                byte[] temp;
                int j = 0;
                for (int i = 0; i < count; i++) {
                    temp = new byte[MTU];
                    System.arraycopy(command, j, temp, 0, MTU);
                    Log.i(TAG, "send: " + byteArrayToHexString(temp));
                    writeCharacter.setValue(temp);
                    writeCharacter.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    mBluetoothGatt.writeCharacteristic(writeCharacter);
                    j += MTU;
                }
                if (j < command.length) {
                    temp = new byte[command.length - j];
                    System.arraycopy(command, j, temp, 0, command.length - j);
                    Log.i(TAG, "send: " + new String(temp));
                    writeCharacter.setValue(temp);
                    writeCharacter.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    mBluetoothGatt.writeCharacteristic(writeCharacter);
                }
            }
        }, WRITE_DELAY);


//        int delayTime=1;
//        if(count>=2){
//            delayTime =2;
//        }
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (isConnected()){
//                    notifyReadTimeout(command);
//                }
//
//            }
//        }, READ_TIMEOUT *delayTime);

    }

    private final static int TYPE_CONNECTED = 1;
    private final static int TYPE_DISCONNECTED = 2;
    private final static int TYPE_CONNECT_FAILED = 3;
    private final static int TYPE_READ_TIMEOUT = 4;

    private void notifyConnectStateChange(int type, byte[] command, String address) {

        if (connectListenerList == null || mAddress == null || !mAddress.equalsIgnoreCase(address)) {
            return;
        }

        for (int i = 0; i < connectListenerList.size(); i++) {
            WeakReference<OnBleConnectStateChangeListener> weakListener = connectListenerList.get(i);
            final OnBleConnectStateChangeListener listener = weakListener.get();
            if (listener == null) continue;

            switch (type) {
                case TYPE_CONNECTED:
                    listener.onBleConnected(address);
                    break;
                case TYPE_DISCONNECTED:
                    listener.onBleDisconnected(address);
                    break;
                case TYPE_CONNECT_FAILED:
                    listener.onBleConnectFailed(address);
                    break;
                case TYPE_READ_TIMEOUT:
                    listener.onBleReadTimeout(address, command);
                    break;
            }
        }
    }
}
