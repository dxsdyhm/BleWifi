package com.qmx.blewifi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    //线程类
    private ClientThread mClientThread;
    private ReadThread mReadThread;
    // 蓝牙客户端socket
    private BluetoothSocket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                if(device.getName().contains("gwell")){
                    Log.e("dxsTest","device:"+device.getName());
                    mDevice=device;
                    new ClientThread().start();
                }else {
                    Log.e("dxsTest","device:"+device.getName());
                }
            }
        }
    }

    private class ClientThread extends Thread {
        public void run() {
            try {
                mSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                mSocket.connect();
                //TODO 启动接受数据  服务端返回数据(暂时用途不大)
                mReadThread = new ReadThread();
                mReadThread.start();

                //注意子线程不能刷新
                //UIViewRootImpl$CalledFromWrongThreadException:
                //ll_bluetooth.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                Log.e("dxsTest","失去连接:"+e.getMessage());
            }
        }
    }

    /**
     * 读取数据
     */
    private class ReadThread extends Thread {
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream is = null;
            try {
                is = mSocket.getInputStream();
                while (true) {
                    if ((bytes = is.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        String s = new String(buf_data);
                        Log.e("dxsTest","s:"+s);
                        //同样  子线程不能刷新UI
                        //tv_accept.setText(s);
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
    }
}
