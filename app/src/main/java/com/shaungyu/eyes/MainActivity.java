package com.shaungyu.eyes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 233;
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView devicesList;
    private Button scanButton;
    private TextView log_view;
    private List<BLEDeviceBean> devices = new ArrayList<>();
    private DevicesAdapter devicesAdapter;

    private void log_show(String message)
    {
        log_view.append(message);
        log_view.append("\n");


    }
    private BluetoothSingle.BluetoothChangeListener bluetoothChangeListener = new BluetoothSingle.BluetoothChangeListener() {
        @Override
        public void openFailed(String message) {

        }

        @Override
        public void requestOpenBT(String message) {

        }

        @Override
        public void addBluetoothDevice(final BluetoothDevice device, final byte[] bytes) {
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public void run() {
                    //Toast.makeText(MainActivity.this, device.getName(), Toast.LENGTH_SHORT).show();
                    //log_show(device.getName());
                    for (int i = 0; i < devices.size(); i++) {
                        if (TextUtils.equals(devices.get(i).getAddress(), device.getAddress())) {
                            return;
                        }
                    }
                    BLEDeviceBean bean = new BLEDeviceBean();
                    String deviceName = device.getName();
                    if (device.getName() == null) {
                        deviceName = "No Name";
                    }
                        //String name = BleUtil.parseAdertisedData(bytes).getName();
                        //bean.setName(name);
                    //} else {
                    //    bean.setName(device.getName());
                    //}
                    bean.setName(deviceName);
                    //bean.setRssi(device.g);
                    bean.setAddress(device.getAddress());
                    bean.setType(getType(device.getType()));
                    bean.setDeviceType(BluetoothSingle.getInstance().getBluetoothType(device));

                    final int position = devices.size();
                    devices.add(bean);
                    devicesAdapter.notifyItemRangeInserted(position, 1);
                }
            });
        }

        @Override
        public void scanFailed(String message) {

        }

        @Override
        public void startScanningDevices(String message) {
            //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            log_view.append(message);
            log_view.append("\n");
        }

        @Override
        public void stopScanningDevices(String message) {
            //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            log_show(message);


        }

        @Override
        public void connectBluetoothFailed(String message) {
            //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            log_show(message);

        }

        @Override
        public void scanningServiceFailed(String message) {
            //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            log_show(message);

        }

        @Override
        public void scanningServiceSuccess(List<BluetoothGattService> services) {
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public void run() {
                    //Toast.makeText(MainActivity.this, "连接蓝牙成功", Toast.LENGTH_LONG).show();
                    log_show("连接蓝牙成功");


                }
            });


        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(MainActivity.this, "onCharacteristicChanged " + characteristic.getValue(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(MainActivity.this, "onCharacteristicWrite " + characteristic.describeContents(), Toast.LENGTH_SHORT).show();
                    String message = "onCharacteristicWrite " + characteristic.describeContents();
                    log_show(message);


                }
            });
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead: characteristic");
        }
    };
    private Button sendButton;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }
        BluetoothSingle.getInstance().registerBTListener(bluetoothChangeListener);


        devicesList = findViewById(R.id.recycler_view);
        //添加Android自带的分割线
        devicesList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        devicesList.setLayoutManager(new LinearLayoutManager(this));
        devicesAdapter = new DevicesAdapter(this, devices);
        devicesAdapter.setmListener(new DevicesAdapter.DevicesItemClickListener() {
            @Override
            public void onItemClick(BLEDeviceBean device) {
                //去连接

                //Toast.makeText(MainActivity.this, "去连接 +" + device.getName(), Toast.LENGTH_SHORT).show();
                String message = "去连接 +" + device.getName()+"\n";
                log_show(message);


                BluetoothSingle.getInstance().stopScanningDevice();//停止扫描设备
                BluetoothSingle.getInstance().startScaningServices(getApplicationContext(), device.getAddress());//连接蓝牙+扫描服务
            }
        });

        devicesList.setAdapter(devicesAdapter);
        log_view = findViewById(R.id.txt_log_view);
        log_view.setMovementMethod(ScrollingMovementMethod.getInstance());



        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View view) {
                //开始扫描
                BluetoothSingle.getInstance().startScanningDevices();
            }
        });

        sendButton = findViewById(R.id.btn_sleep);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View view) {
//                    byte[] ff6206FD0100FD01012C05FES = {(byte)0xFF,0x62,0x06,(byte)0xFD,0x01,0x00,(byte)0xFD,0x01,0x01,0x2C,0x05,(byte)0xFE};
                String led_blink_str = "FF620600FD01FD01012C05FE";
                byte[] led_blink_byte = TransformUtil.getInstance().hexStringToByteArray(led_blink_str);
//                byte[] ff6206FD0100FD01012C05FES = {0x01, (byte) 0xFC, 0x04, 0x00};
              //BluetoothSingle.getInstance().writeString("0xFF0x620x060xFD0x010x000xFD0x010x010x2C0x050xFE");
                log_show("send:"+led_blink_str);
                BluetoothSingle.getInstance().writeData(led_blink_byte);
            }
        });
    }


    private String getType(int type) {
        String result = "unknown type";
        switch (type) {
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                result = "classic";
                break;
            case BluetoothDevice.DEVICE_TYPE_LE:
                result = "ble";
                break;
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                result = "dual";
                break;
        }
        return result;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
        }
    }

}
