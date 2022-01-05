package com.anker.bluetoothtool.deviceExport.search;//package com.anker.ankerwork.deviceExport.search;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.Context;
//
//import com.anker.ankerwork.deviceExport.model.AnkerWorkDevice;
//import com.anker.bluetoothtool.model.ProductModel;
//import com.anker.bluetoothtool.deviceExport.util.BTDeviceHelper;
//import com.anker.bluetoothtool.deviceExport.util.LogUtil;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Set;
//
///**
// * Author: feipeng.wang
// * Time:   2021/6/7
// * Description : 利用反射获取连接的设备，并回调
// */
//public class SearchHelp {
//    private String TAG = "SearchHelp";
//
//    public void trySearchConnectDevice(Context context,
//                                       ProductModel productModel,
//                                       SearchCallback callback) {
//        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
//        try {//得到蓝牙状态的方法
//            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
//            //打开权限
//            method.setAccessible(true);
//            int state = (int) method.invoke(BluetoothAdapter.getDefaultAdapter(), (Object[]) null);
//
//            if (state == BluetoothAdapter.STATE_CONNECTED) {
//
//                LogUtil.i(TAG, "BluetoothAdapter.STATE_CONNECTED");
//                ArrayList<AnkerWorkDevice> soundCoreDeviceList = new ArrayList();
//                BTDeviceHelper btDeviceHelper = BTDeviceHelper.INSTANCE;
//                Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
//                LogUtil.i(TAG, "devices:" + devices.size());
//
//                for (BluetoothDevice device : devices) {
//
//                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
//                    method.setAccessible(true);
//                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
//
//                    if (isConnected) {
//                        LogUtil.i(TAG, "connected:" + device.getAddress());
//                        AnkerWorkDevice soundCoreDevice =
//                                btDeviceHelper.recognizeAnkerDevice(context, productModel, device);
//                        soundCoreDeviceList.add(soundCoreDevice);
//                    }
//                }
//                if (soundCoreDeviceList.size() == 0) {
//                    callback.onNoDevice(true);
//                } else if (soundCoreDeviceList.size() == 1) {
//                    callback.hasOneDevice(soundCoreDeviceList.get(0));
//                } else {
//                    callback.hasManyDevices(soundCoreDeviceList);
//                }
//
//            } else {
//                callback.onNoDevice(false);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            callback.onNoDevice(false);
//        }
//    }
//}
