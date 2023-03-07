package com.wpf.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.wpf.spplib.OnSppConnectStateChangeListener;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Toast mToast;
    //创建广播接收者的对象
    MyReceiver myReceiver=new MyReceiver();
    //获取蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    //////////////////////////////////////////////创建搜索蓝牙列表的///////////////////////////////////////////////////////////////////
    public ArrayAdapter arrayAdapter;  //这个适配器列表是用来加载到列表的数据
    public ArrayList<BluetoothDevice> deviceAdress = new ArrayList<>();  //存放蓝牙设备（这里Adress我忘了改过来了，这是存放设备不是设备地址）
    public ArrayList<String> deviceName = new ArrayList<>();  //存放蓝牙名称和地址
    public ListView listView;   //定义展示列表
    //手机连接的UUID
    //设备连接的UUID由厂商决定。
    private final String BLUETOOTH_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //蓝牙通信的UUID，必须为这个，如果换成其他的UUID会无法通信
    private BluetoothSocket bluetoothSocket = null;
    private EditText editText_send;
    private TextView textView_receive;
    private Button button_send;
    // 用来收数据
    //InputStream inputStream = bluetoothSocket.getInputStream();
    // 用来发数据
    OutputStream outputStream;
    private DeviceManager deviceManager;

    public class MyReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Toast.makeText(context, "开始", Toast.LENGTH_SHORT).show();
            } else if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //////////////////////////////////////////////创建搜索蓝牙列表的///////////////////////////////////////////////////////////////////
                for (int i = 0; i < deviceAdress.size(); i++) {
                    if (deviceAdress.get(i).getAddress().equals(device.getAddress())) return;
                    //上面if语句就是去除已经获取的蓝牙设备
                }
                // 不是重复的就添加到列表中(获取未配对的蓝牙设备)
                deviceAdress.add(device);  //添加地址到列表中   用于鉴别是否已经添加列表和点击事件用的
                deviceName.add("地址："+device.getAddress()+"\n"+"名称："+device.getName());  //存放蓝牙名称和地址用于显示到列表上的
                arrayAdapter.notifyDataSetChanged();  //更新列表
                Connect_BT(deviceAdress);
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            } else if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            } else if(intent.getAction().equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {//监听配对的状态
                int type = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);
                    Log.e("wpf", "ACTION_PAIRING_REQUEST--" + type);
                    //PAIRING_VARIANT_CONSENT 0
                    if (type == BluetoothDevice.PAIRING_VARIANT_PIN) {
                        //防止弹出一闪而过的配对框
                        abortBroadcast();
                        //弹框后自动输入密码、自动确定
                        try {
                            boolean isSetPin = autoBond(BluetoothDevice.class, clickDevice, "0000");
                            Log.e("wpf", "setPin()-->" + isSetPin);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if(intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {//监听配对的状态
                //设备绑定状态改变
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                //收到绑定成功的通知后自动连接
                if (deviceManager!=null&&bondState == BluetoothDevice.BOND_BONDED) {
                    Log.e("wpf","已绑定："+device.getAddress());
                    deviceManager.connect(device.getAddress());
                }else if (deviceManager!=null&&bondState == BluetoothDevice.BOND_BONDING) {
                    Log.e("wpf","正在绑定："+device.getAddress());
                }else if (deviceManager!=null&&bondState == BluetoothDevice.BOND_NONE) {
                    Log.e("wpf","绑定失败："+device.getAddress());
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceManager = new DeviceManager();
        deviceManager.setUuid("0CF12D31-FAC3-4553-BD80-D6832E7B3511");
        initListen();
        //////////////////////////////////////////////创建搜索蓝牙列表的///////////////////////////////////////////////////////////////////并把
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,deviceName);  //实例化ArrayAdapter对象deviceName集合数据放入arrayAdapter适配器集合内
        listView = (ListView) findViewById(R.id.list);  //获取列表框的
        listView.setAdapter(arrayAdapter);  //将arrayAdapter集合内的数据加载到列表框 就是适配器对象与ListView关联

//        //创建广播接收者的对象
//        MyReceiver myReceiver=new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
 /*       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);  //配对请求
        }*/
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED );
        //上面是添加动作事件
        //过滤器
        intentFilter.addAction("com.mingrisoft");
        //注册广播接收者的对象
        registerReceiver(myReceiver,intentFilter);

        Button button= (Button) findViewById(R.id.Broadcast);  //获取布局文件中的广播按钮
        button.setOnClickListener(new View.OnClickListener() { //为按钮设置单击事件
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent();                    //创建Intent对象
                //intent.setAction("com.mingrisoft");            //为Intent添加动作com.mingrisoft
                //sendBroadcast(intent);                         //发送广播
                //////////蓝牙刷新///////////////
                deviceAdress.clear();         //
                deviceName.clear();           //
                startDiscovery();             //
                ////////////////////////////////
//                listView.setAdapter(arrayAdapter);  //将arrayAdapter集合内的数据加载到列表框 就是适配器对象与ListView关联

            }
        });

        //按钮搜寻蓝牙
        Button button_discovery = (Button) findViewById(R.id.DiscoveryBT);
        button_discovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
            }
        });

        button_send = (Button) findViewById(R.id.button_send);
        textView_receive = findViewById(R.id.receive);
        editText_send = findViewById(R.id.send);
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView_receive.setText(" 发送到数据：" + editText_send.getText());
                String text = editText_send.getText().toString();
                try {
                    outputStream = bluetoothSocket.getOutputStream();
                    outputStream.write(text.getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /***************************一系列的开关*********************************************/
        Button button_oc1 = (Button) findViewById(R.id.button_open_close1);
        button_oc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "a12";
                try {
                    outputStream = bluetoothSocket.getOutputStream();
                    outputStream.write(text.getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button button_oc2 = (Button) findViewById(R.id.button_open_close2);
        button_oc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "b12";
                try {
                    outputStream = bluetoothSocket.getOutputStream();
                    outputStream.write(text.getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button button_oc3 = (Button) findViewById(R.id.button_open_close3);
        button_oc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "d12";
                try {
                    outputStream = bluetoothSocket.getOutputStream();
                    outputStream.write(text.getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    private void initListen() {
        deviceManager.setOnSppConnectStateChangeListener(new OnSppConnectStateChangeListener() {
            @Override
            public void OnSppConnected(String macAddress, String deviceName) {
                Log.e("wpf","OnSppConnected:"+deviceName);
            }

            @Override
            public void OnSppDisconnected(String macAddress) {
                Log.e("wpf","OnSppDisconnected:"+macAddress);
            }
        });
    }
    //判断是否支持蓝牙
    public boolean isSupportBlueTooth() {
        if(mBluetoothAdapter != null) {
            return true;
        }
        else {
            return false;
        }
    }
    //获取蓝牙状态
    public  boolean BlueToothState() {
        assert (mBluetoothAdapter != null);   //若不支持该蓝牙设备会有个断言
        return mBluetoothAdapter.isEnabled();
    }
    //打开蓝牙
    public void OpenBlueTooth(View view) {
        if(isSupportBlueTooth() == true) {
            if(!BlueToothState()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent,0);
                showToast("亲,打开了噢！需要什么帮助吗？");
            }
            else {
                showToast("亲,已经打开了噢，无需重复打开。");
            }
        }
        else {
            showToast("亲,您不支持此蓝牙设备!");
        }
    }
    //关闭蓝牙
    public void CloseBlueTooth(View view) {
        mBluetoothAdapter.disable();
        showToast("亲,我们会再见面的!");
    }

    // 开始搜索
    public void startDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) {mBluetoothAdapter.cancelDiscovery();  Toast.makeText(this,"搜索器打开",Toast.LENGTH_SHORT).show();}
        mBluetoothAdapter.startDiscovery();
        if (!mBluetoothAdapter.isDiscovering()) {Toast.makeText(this,"搜索器没打开",Toast.LENGTH_SHORT).show();}
    }

    //取消搜索
    public void cancelScanBule() {
        mBluetoothAdapter.cancelDiscovery();
    }

    //在activity结束时要注销；
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //解除注册
        unregisterReceiver(myReceiver);
    }


    //连接蓝牙
    private BluetoothDevice clickDevice;
    public void Connect_BT(ArrayList<BluetoothDevice> deviceAdress) {
        //MainActivity 实现OnItemClickListener 然后重写方法
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //final BluetoothDevice[] romoteDevice = new BluetoothDevice[1];
                if(mBluetoothAdapter.isDiscovering())
                {
                    mBluetoothAdapter.cancelDiscovery();
                }
                clickDevice = (BluetoothDevice)deviceAdress.get(position);
                String s1 = String.valueOf(position);  //编号
                Toast.makeText(MainActivity.this, s1 + "--" + clickDevice.getName() + "--" + clickDevice.getAddress(), Toast.LENGTH_SHORT).show();
                //在连接前需要先关闭搜索
                //点击列表，去请求服务器
                if (clickDevice != null) {

                    /*if (isHaveBond(clickDevice)){
                        deviceManager.connect(clickDevice.getAddress());
                    }else {
                        boolean bondResult = false;
                        try {
                            bondResult = createBond(BluetoothDevice.class,clickDevice);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e(getPackageName(), "配对结果："+bondResult);
                    }*/


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
//////////////////////////////////////////需要配对码进行配对连接////////////////////////////////////////////////////////
                    /*            boolean bondResult =  createBond(BluetoothDevice.class,clickDevice);
                                Log.e(getPackageName(), "配对结果："+bondResult);*/
////////////////////////////////这里如果没有配对过的设备是会弹出窗口输入配对码的////////////////////////////////////////////////////////////
//                                deviceManager.connect(clickDevice.getAddress());
//                                pair(clickDevice.getAddress(),"0000");
                                //通过工具类ClsUtils,调用createBond方法
                                ClsUtils.createBond(clickDevice.getClass(), clickDevice);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

    }

    //显示函数
    public void showToast(String text) {
        //Toast显示的时间有限，过一定的时间就会自动消失
        //因此这里要判断是否消失了
        //Toast.LENGTH_SHORT这个是设置显示两秒，LONG是3秒
        if( mToast == null ) {  //检查mToast文本空了没
            //下面这行程序是一般是只执行一次的，就是刚开始空文本时初始化一次里面显示设置
            mToast = Toast.makeText(this,text,Toast.LENGTH_SHORT);
        }
        else {
            mToast.setText(text);//这里是倒数两秒，也就是浮框消失后重新设置文本内容
            //达到事件不同回显文本不同的切换效果
        }
        mToast.show();
    }
     public boolean removeBond(Class btClass, BluetoothDevice btDevice)
            throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }
    //配对方式
    public boolean createBond(Class btClass, BluetoothDevice btDevice)
            throws Exception {
        removeBond(BluetoothDevice.class,btDevice);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //Android 4.4 API 19 以上才开放Bond接口
           return btDevice.createBond();
        } else {
            //API 19 以下用反射调用Bond接口
            try {
                Method createBondMethod = btClass.getMethod("createBond");
                Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
                return returnValue.booleanValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        //API 19 以下用反射调用Bond接口
        try {
            Method createBondMethod = btClass.getMethod("createBond");
            Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
            return returnValue.booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    /*判断是否在绑定列表*/
    private boolean isHaveBond(BluetoothDevice clickDevice){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();//获取已绑定的设备列表
        for (BluetoothDevice item:pairedDevices) {
            if (item.getAddress().equals(clickDevice.getAddress())){
                return true;
            }
        }
        return false;
    }
    //自动配对设置Pin值
    public boolean autoBond(Class btClass,BluetoothDevice device,String strPin) throws Exception {
        Method autoBondMethod = btClass.getMethod("setPin",new Class[]{byte[].class});
        Boolean result = (Boolean)autoBondMethod.invoke(device,new Object[]{strPin.getBytes()});
        return result;
    }

    public  boolean pair(String strAddr, String strPsw)
    {
        boolean result = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();

        bluetoothAdapter.cancelDiscovery();

        if (!bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.enable();
        }

        if (!BluetoothAdapter.checkBluetoothAddress(strAddr))
        { // 检查蓝牙地址是否有效

            Log.d("mylog", "devAdd un effient!");
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strAddr);

        if (device.getBondState() != BluetoothDevice.BOND_BONDED)
        {
            try
            {
                Log.d("mylog", "NOT BOND_BONDED");
                ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
                ClsUtils.createBond(device.getClass(), device);
                clickDevice = device; // 配对完毕就把这个设备对象传给全局的remoteDevice
                result = true;
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block

                Log.d("mylog", "setPiN failed!");
                e.printStackTrace();
            } //

        }
        else
        {
            Log.d("mylog", "HAS BOND_BONDED");
            try
            {
                ClsUtils.createBond(device.getClass(), device);
                ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
                ClsUtils.createBond(device.getClass(), device);
                clickDevice = device; // 如果绑定成功，就直接把这个设备对象传给全局的remoteDevice
                result = true;
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                Log.d("mylog", "setPiN failed!");
                e.printStackTrace();
            }
        }
        return result;
    }
}



