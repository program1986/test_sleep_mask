package com.shaungyu.eyes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 233;
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView devicesList;
    private Button scanButton;
    private TextView log_view;
    private List<BLEDeviceBean> devices = new ArrayList<>();
    private DevicesAdapter devicesAdapter;
    private MediaPlayer mediaPlayer = new MediaPlayer();;
    private MediaPlayer nMediaPlayer = new MediaPlayer();;
    //播放
    private Button btn_play,btn_stop;

    private void initMediaPlayer()  {



        mediaPlayer = mediaPlayer.create(getApplicationContext(),R.raw.wav1);
        //try {
        //          mediaPlayer.prepareAsync();
        ///} catch (Exception e) {
        //    log_show("Music open error");
        //    e.printStackTrace();

        //}
        /*
        AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.wav1);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                    file.getLength());
            mediaPlayer.prepare();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            log_show("Music open error");
        }
        */

        //mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.setLooping(true);
        log_show("Music init complete");
        //mediaPlayer.start();
    }

    private void log_show(String message) {
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

        log_view = findViewById(R.id.txt_log_view);
        log_view.setMovementMethod(ScrollingMovementMethod.getInstance());

        BluetoothSingle.getInstance().registerBTListener(bluetoothChangeListener);
        //初始化播放器
        //权限判断，如果没有权限就请求权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        initMediaPlayer();//初始化播放器 MediaPlayer
        //播放按钮
        btn_play = findViewById(R.id.btn_play);
        btn_play.setOnClickListener(this);

        btn_stop =findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);

        devicesList = findViewById(R.id.recycler_view);
        //添加Android自带的分割线
        devicesList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        devicesList.setLayoutManager(new LinearLayoutManager(this));
        devicesAdapter = new DevicesAdapter(this, devices);
        devicesAdapter.setmListener(new DevicesAdapter.DevicesItemClickListener() {
            @Override
            public void onItemClick(BLEDeviceBean device) {
                //去连接
          //Toast.makeText(MainActivity.this, "去连接 +" + device.getName(), Toast.LENGTH_SHORT).show();
                String message = "去连接 +" + device.getName() + "\n";
                log_show(message);
                BluetoothSingle.getInstance().stopScanningDevice();//停止扫描设备
                BluetoothSingle.getInstance().startScaningServices(getApplicationContext(), device.getAddress());//连接蓝牙+扫描服务
            }
        });

        devicesList.setAdapter(devicesAdapter);



        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View view) {
                //开始扫描
                BluetoothSingle.getInstance().startScanningDevices();
            }
        });

        log_show("ver1.11");
        sendButton = findViewById(R.id.btn_sleep);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View view) {
//                    byte[] ff6206FD0100FD01012C05FES = {(byte)0xFF,0x62,0x06,(byte)0xFD,0x01,0x00,(byte)0xFD,0x01,0x01,0x2C,0x05,(byte)0xFE};
                String led_blink_str = "FF6106010000012C05FE";
                byte[] led_blink_byte = TransformUtil.getInstance().hexStringToByteArray(led_blink_str);
//                byte[] ff6206FD0100FD01012C05FES = {0x01, (byte) 0xFC, 0x04, 0x00};
                //BluetoothSingle.getInstance().writeString("0xFF0x620x060xFD0x010x000xFD0x010x010x2C0x050xFE");

                //发送整体时间，30分钟
                String full_time_string = "FF600400000708FE";
                byte[] full_time_byte = TransformUtil.getInstance().hexStringToByteArray(full_time_string);

                log_show("send:" + full_time_string);
                BluetoothSingle.getInstance().writeData(full_time_byte);

                log_show("send:" + led_blink_str);
                BluetoothSingle.getInstance().writeData(led_blink_byte);


                //FF600400000708FE

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:
                log_show("Play");
                testLoopPlayer();
                //mediaPlayer.start();// 这样使用会出现停顿
                //如果没在播放中，立刻开始播放。
                //if(!mediaPlayer.isPlaying()){
                //    mediaPlayer.start();
                break;
            case R.id.btn_stop:
                mediaPlayer.stop();
                break;
                }
    }

    private int mPlayResId = R.raw.wav1;
    public void testLoopPlayer() {
        mediaPlayer = MediaPlayer.create(this, mPlayResId);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        createNextMediaPlayer();
    }

    private void createNextMediaPlayer() {
        nMediaPlayer = MediaPlayer.create(this, mPlayResId);
        mediaPlayer.setNextMediaPlayer(nMediaPlayer);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();

                mediaPlayer = nMediaPlayer;

                createNextMediaPlayer();
            }
        });
    }
}
