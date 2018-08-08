# HopShing
一个蓝牙demo

# API

> 
> 具体使用请参考本项目提供的例子
> 

### 获取BleController实例并进行初始化（初始化只需执行一次）

* BleController.getInstance().init(this);



### 搜所设备，获取设备列表
* BleController.getInstance().scanBle(int time,ScanCallback scanCallbak);



### 连接设备
* BleController.getInstance().connect(int time,String deviceAdress,ConnectCallback connectCallback);


### 发送数据
* BleController.getInstance().writeBuffer(byte[] buf,OnWriteCallback onWriteCallback);



### 注册接收数据的监听
* BleController.getInstance().registReciveListener(String requestKey,OnReceiverCallback onReceiveCallback);


### 注销接收数据的监听
* BleController.getInstance().unregistReciveListener(String requestKey);


### 断开连接
* BleController.getInstance().closeBleConn();



---

# 必备操作

### 添加权限

    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

定位权限必须添加,否则Android 5.0以上的手机搜索不到设备


### 指定UUID

* 打开BleController.class
* 修改以下成员变量的值为自己所要通信的BLE模块对应的UUID
    
        private static final String BLUETOOTH_S = "0000fff0-0000-1000-8000-00805f9b34fb";
    	private static final String BLUETOOTH_NOTIFY_C = "0000fff7-0000-1000-8000-00805f9b34fb";
    	private static final String BLUETOOTH_WRITE_C = "0000fff6-0000-1000-8000-00805f9b34fb";

> 变量名最后一位字母：
> 
> S 代表service的UUID；
> 
> C 代表characteristic的UUID；


如果硬件没有提供UUID，可以下载Ble调试助手进行查看
