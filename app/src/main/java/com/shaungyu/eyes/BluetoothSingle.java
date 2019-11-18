package com.shaungyu.eyes;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


public class BluetoothSingle {

    private final String TAG = BluetoothSingle.this.getClass().getSimpleName();
    private BluetoothLeScanCall bluetoothLeScanCall;
    private BluetoothScanCall bluetoothScanCall;
    private MyBluetoothGattCallback bluetoothGattCallback;
    private BluetoothAdapter bluetoothAdapter;
    private MyHandler bluetoothHandler;
    private BluetoothGatt bluetoothGatt;
    boolean isScanning = false;
    private List<BluetoothChangeListener> listenerList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BluetoothSingle() {
        Log.e("BluetoothSingle", "初始化蓝牙单例");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothScanCall = new BluetoothScanCall(this);
        bluetoothLeScanCall = new BluetoothLeScanCall(this);
        bluetoothGattCallback = new MyBluetoothGattCallback(this);
        bluetoothHandler = new MyHandler(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static class InerrHolder {
        private static final BluetoothSingle instance = new BluetoothSingle();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static BluetoothSingle getInstance() {
        return InerrHolder.instance;
    }

    //开启蓝牙扫描
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void openBluetooth() {
        if (bluetoothAdapter == null) {
            for (BluetoothChangeListener listener : listenerList) {
                listener.openFailed("设备不支持蓝牙");
            }
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            for (BluetoothChangeListener listener : listenerList) {
                listener.requestOpenBT("请求打开蓝牙");
            }
            return;
        }
        startScanningDevices();
    }

    //扫描附近蓝牙设备
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startScanningDevices() {
        stopScanningDevice();
        startScanning();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void startScanning() {
        if (bluetoothAdapter == null) {
            Log.e(TAG, "设备不支持蓝牙");
            for (BluetoothChangeListener listener : listenerList) {
                listener.openFailed("设备不支持蓝牙");
            }
            return;
        }
        Log.e(TAG, "开始扫描蓝牙");
        isScanning = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothAdapter.getBluetoothLeScanner().startScan(bluetoothScanCall);
        } else {
            bluetoothAdapter.startLeScan(bluetoothLeScanCall);
        }
        Message msg = Message.obtain();
        msg.what = 0x101;
        bluetoothHandler.sendMessageDelayed(msg, 12000);
        for (BluetoothChangeListener listener : listenerList) {
            listener.startScanningDevices("开始扫描蓝牙...");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopScanningDevice() {
        if (bluetoothAdapter == null) {
            Log.e(TAG, "设备不支持蓝牙");
            for (BluetoothChangeListener listener : listenerList) {
                listener.openFailed("设备不支持蓝牙");
            }
            return;
        }
        if (!isScanning) {
            return;
        }
        Log.e(TAG, "停止扫描蓝牙");
        isScanning = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(bluetoothScanCall);
        } else {
            bluetoothAdapter.stopLeScan(bluetoothLeScanCall);
        }
        for (BluetoothChangeListener listener : listenerList) {
            listener.stopScanningDevices("停止扫描蓝牙设备...");
        }
    }

    //添加蓝牙设备
    private void addBluetoothDevice(BluetoothDevice device, byte[] recordBytes) {
        for (BluetoothChangeListener listener : listenerList) {
            listener.addBluetoothDevice(device, recordBytes);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startScaningServices(Context context, String mac) {
        bluetoothGatt = bluetoothAdapter.getRemoteDevice(mac).connectGatt(context, false, bluetoothGattCallback);
    }

    private void connectBluetoothFailed(String message) {
        connectServiceResult = false;
        for (BluetoothChangeListener listener : listenerList) {
            listener.connectBluetoothFailed(message);
        }
    }

    //扫描蓝牙服务失败
    private void scanningServiceFailed(String message) {
        connectServiceResult = false;
        for (BluetoothChangeListener listener : listenerList) {
            listener.scanningServiceFailed(message);
        }
    }

    private boolean connectServiceResult = false;

    //扫描蓝牙服务成功
    private void scanningServiceSuccess(List<BluetoothGattService> list) {
        connectServiceResult = true;
        for (BluetoothChangeListener listener : listenerList) {
            listener.scanningServiceSuccess(list);
        }
    }

    private void characterChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        for (BluetoothChangeListener listener : listenerList) {
            listener.onCharacteristicChanged(gatt,characteristic);
        }
    }

    private void characterWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        for (BluetoothChangeListener listener : listenerList) {
            listener.onCharacteristicWrite(gatt,characteristic,status);
        }
    }

    private void characterRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        for (BluetoothChangeListener listener : listenerList) {
            listener.onCharacteristicRead(gatt,characteristic,status);
        }
    }

    public String getDevicesInfo() {
        if (bluetoothAdapter != null) {
            String address = bluetoothAdapter.getAddress() == null ? "Unknown address" : bluetoothAdapter.getAddress();
            return address;
        }
        return null;
    }

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void writeData(byte[] bytes) {
        UUID characterUuid = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
        UUID serviceUuid = UUID.fromString("0000ff12-0000-1000-8000-00805f9b34fb");
//        for (BluetoothGattService service : bluetoothGatt.getServices()) {
//            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
//                if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
//                    serviceUuid = service.getUuid();
//                    characterUuid = characteristic.getUuid();
////                    Log.e(TAG, "writeData: 找到写特征");
//                    break;
//                }
//            }
//            if (characterUuid != null && serviceUuid != null) {
//                break;
//            }
//        }
        if (characterUuid != null && serviceUuid != null) {
            for (BluetoothGattCharacteristic characteristic2 : bluetoothGatt.getService(serviceUuid).getCharacteristics()) {
                if ((characteristic2.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    bluetoothGatt.setCharacteristicNotification(characteristic2, true);
                }
            }
            BluetoothGattCharacteristic characteristic = bluetoothGatt.getService(serviceUuid).getCharacteristic(characterUuid);
            characteristic.setValue(bytes);
            bluetoothGatt.writeCharacteristic(characteristic);
            Log.e(TAG, "发送数据...");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void writeString(String data) {
        //根据文档  服务id是固定的
        UUID characterUuid = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
        UUID serviceUuid = UUID.fromString("0000ff12-0000-1000-8000-00805f9b34fb");
//        for (BluetoothGattService service : bluetoothGatt.getServices()) {
//            if(service.getUuid().equals(serviceUuid)){
//                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
//                    if (characteristic.getUuid().equals()) {
//                        serviceUuid = service.getUuid();
//                        characterUuid = characteristic.getUuid();
////                    Log.e(TAG, "writeData: 找到写特征");
//                        break;
//                    }
//                }
//            }
//
//
//            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
//                if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
//                    serviceUuid = service.getUuid();
//                    characterUuid = characteristic.getUuid();
////                    Log.e(TAG, "writeData: 找到写特征");
//                    break;
//                }
//            }
//            if (characterUuid != null && serviceUuid != null) {
//                break;
//            }
//        }


        if (characterUuid != null && serviceUuid != null) {
            for (BluetoothGattCharacteristic characteristic2 : bluetoothGatt.getService(serviceUuid).getCharacteristics()) {
                if ((characteristic2.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    bluetoothGatt.setCharacteristicNotification(characteristic2, true);
                }
            }
            bluetoothGatt.getService(serviceUuid).getCharacteristic(characterUuid).setValue(data);
            bluetoothGatt.writeCharacteristic(bluetoothGatt.getService(serviceUuid).getCharacteristic(characterUuid));
            Log.e(TAG, "发送数据...");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setCharacteristicNotifify(BluetoothGattCharacteristic characteristic) {
        if (bluetoothGatt != null && characteristic != null) {
            bluetoothGatt.setCharacteristicNotification(characteristic, true);
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            bluetoothGatt.writeDescriptor(descriptor);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void disconnectDevice() {
        if (bluetoothGatt != null) {
            Log.e("BluetoothSingle", "断开设备连接");
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
    }

    public int getBluetoothType(BluetoothDevice device) {
        if (device == null) {
            Log.e(TAG, "Device is null!!!!!!!!");
            return -1;
        }
        int classInt = device.getBluetoothClass().getMajorDeviceClass();
//        Log.e(TAG, "设备类型：" + classInt);
        return classInt;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean isConnect() {
        if (bluetoothAdapter != null) {
            if (bluetoothGatt != null) {
                return (connectServiceResult && bluetoothGatt.getServices() != null);
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopAll() {
        Log.e("BluetoothSingle", "释放蓝牙单例类中资源");
        listenerList.clear();
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
        if (bluetoothAdapter != null && bluetoothLeScanCall != null && isScanning) {
            bluetoothAdapter.stopLeScan(bluetoothLeScanCall);
        }
        if (bluetoothHandler != null)
            bluetoothHandler.removeCallbacksAndMessages(null);
        bluetoothGatt = null;
        bluetoothAdapter = null;
        bluetoothHandler = null;
        bluetoothLeScanCall = null;
        bluetoothGattCallback = null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static class BluetoothScanCall extends ScanCallback {
        private final String TAG = BluetoothScanCall.this.getClass().getSimpleName();

        private final WeakReference<BluetoothSingle> weakReference;

        public BluetoothScanCall(BluetoothSingle single) {
            weakReference = new WeakReference<>(single);
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            final BluetoothDevice device = result.getDevice();
            final ScanRecord scanRecord = result.getScanRecord();
//            String sss = TransformUtil.getInstance().bytesToHex(scanRecord.getBytes());
//            Log.e(TAG, "广播数据包：" + sss + "\t长度：" + sss.length());
            if (weakReference.get() != null && device != null) {
                weakReference.get().addBluetoothDevice(device, scanRecord.getBytes());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.e(TAG, "onBatchScanResults: ");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "onScanFailed: ");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static class BluetoothLeScanCall implements BluetoothAdapter.LeScanCallback {//扫描蓝牙后回调接口

        private final String TAG = BluetoothLeScanCall.this.getClass().getSimpleName();
        private final WeakReference<BluetoothSingle> weakReference;

        public BluetoothLeScanCall(BluetoothSingle activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            //rssi：是蓝牙当前的信号值，绝对值越小信号越好。
            Log.e(TAG, "扫描到一台蓝牙设备：" + bluetoothDevice.getAddress());
            Log.e(TAG, "广播包数据：" + TransformUtil.getInstance().bytesToHex(bytes));
            if (weakReference.get() != null) {
                weakReference.get().addBluetoothDevice(bluetoothDevice, bytes);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static class MyBluetoothGattCallback extends BluetoothGattCallback {//连接蓝牙、断开连接蓝牙、发现蓝牙服务回调接口
        final WeakReference<BluetoothSingle> weakReference;
        private long startTime = SystemClock.elapsedRealtime();

        public MyBluetoothGattCallback(BluetoothSingle activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.e("MainActivity", "连接到蓝牙：" + gatt.getDevice().getAddress());
                    gatt.discoverServices();
                    return;
                }
            }
            if (weakReference != null && weakReference.get() != null) {
                weakReference.get().connectBluetoothFailed("没有连接到蓝牙");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (weakReference != null && weakReference.get() != null) {
                    Log.e("MainActivity", "连接蓝牙服务：" + gatt.getDevice().getAddress());
                    weakReference.get().scanningServiceSuccess(gatt.getServices());
                    return;
                }
            }
            if (weakReference != null && weakReference.get() != null) {
                weakReference.get().scanningServiceFailed("没有连接到蓝牙服务，影响蓝牙通信");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {//系统读取到蓝牙设备发送过来的数据
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.e("onCharacteristicRead", "characteristic uuid=" + characteristic.getUuid());
            final String hexString = TransformUtil.getInstance().bytesToHexStringToString(characteristic.getValue());
            Log.e("onCharacteristicRead", "接收数据：" + hexString);


            if (weakReference.get() != null) {
                weakReference.get().characterRead( gatt,  characteristic,  status);
            }

        }

        //发送数据后的回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {//蓝牙设备发送数据
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.e("onCharacteristicWrite", "characteristic uuid=" + characteristic.getUuid());
            Log.e("onCharacteristicWrite", "发送数据：" + TransformUtil.getInstance().byte2stringHex(characteristic.getValue()));
            final byte[] data = characteristic.getValue();
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            Log.e("onCharacteristicWrite", "发送数据：" + stringBuilder.toString());
            if (weakReference != null && weakReference.get() != null) {
                weakReference.get().characterWrite(gatt,characteristic,status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
//            Log.e("onCharacteristicChanged", characteristic.getUuid().toString());
            long endTime = SystemClock.elapsedRealtime();
            final long internalTime = endTime - startTime;
            startTime = endTime;
            Log.e("changed", TransformUtil.getInstance().byte2stringHex(characteristic.getValue()) + "  间隔时间:" + internalTime + " ms");
            if (weakReference != null && weakReference.get() != null) {
                weakReference.get().characterChanged(gatt,characteristic);
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.e("onDescriptorRead", "打印内容： " + descriptor.getValue());
        }

//        @Override
//        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//            super.onDescriptorWrite(gatt, descriptor, status);
//            Log.e("onDescriptorWrite", "打印内容： " + descriptor.getValue());
//        }
    }

    private static class MyHandler extends Handler {

        final WeakReference<BluetoothSingle> weakReference;

        public MyHandler(BluetoothSingle single) {
            weakReference = new WeakReference<>(single);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x101:
                    if (weakReference != null && weakReference.get() != null) {
                        weakReference.get().stopScanningDevice();
                    }
                    break;
            }
        }
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public void registerBTListener(BluetoothChangeListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void unregistBTListener(BluetoothChangeListener listener) {
        if (listenerList.contains(listener)) {
            listenerList.remove(listener);
        }
    }

    public interface BluetoothChangeListener {

        void openFailed(String message);

        void requestOpenBT(String message);

        void addBluetoothDevice(BluetoothDevice device, byte[] recordBytes);

        void scanFailed(String message);

        void startScanningDevices(String message);

        void stopScanningDevices(String message);

        void connectBluetoothFailed(String message);

        void scanningServiceFailed(String message);

        void scanningServiceSuccess(List<BluetoothGattService> services);

        void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

        void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

        void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
    }
}
