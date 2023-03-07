package com.wpf.bluetoothdemo;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Author: feipeng.wang
 * Time:   2021/12/10
 * Description : This is description.
 */
public class BluetoothConnectActivityReceiver extends BroadcastReceiver
{

    String strPsw = "0000";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(
                "android.bluetooth.device.action.PAIRING_REQUEST"))
        {
            BluetoothDevice btDevice = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            // byte[] pinBytes = BluetoothDevice.convertPinToBytes("1234");
            // device.setPin(pinBytes);
            Log.e("tag11111", "ddd");
            try
            {
/*                ClsUtils.setPin(btDevice.getClass(), btDevice, strPsw); // 手机和蓝牙采集器配对
                ClsUtils.createBond(btDevice.getClass(), btDevice);
                ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);*/


                //1.确认配对
                ClsUtils.setPairingConfirmation(btDevice.getClass(), btDevice, true);
//                ClsUtils.setPairingConfirmation(btDevice);
//                btDevice.setPairingConfirmation(true);
                //2.终止有序广播
                Log.e("order...", "isOrderedBroadcast:"+isOrderedBroadcast()+",isInitialStickyBroadcast:"+isInitialStickyBroadcast());
                abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                //3.调用setPin方法进行配对...
                boolean ret = ClsUtils.setPin(btDevice.getClass(), btDevice, "1234");
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }
}
