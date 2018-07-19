package com.qmx.blewifi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.qmx.adapter.BleDeviceAdapter;
import com.qmx.adapter.bleAdapter;
import com.qmx.entity.bleViewMode;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rcBle;
    private MultiTypeAdapter adapter;
    private Items items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BleManager.getInstance().init(getApplication());
        initUI();
        adapter.setItems(items);
        initData();
    }

    private void initData() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                // 开始扫描（主线程）
                Log.e("dxsTest","success:"+success);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                // 扫描到一个符合扫描规则的BLE设备（主线程）
                items.add(bleDevice);
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                // 扫描结束，列出所有扫描到的符合扫描规则的BLE设备（主线程）
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initUI() {
        rcBle=findViewById(R.id.rc_ble);
        adapter = new MultiTypeAdapter();
        adapter.register(BleDevice.class, new BleDeviceAdapter());
        rcBle.setLayoutManager(new LinearLayoutManager(this));
        items = new Items();
        rcBle.setAdapter(adapter);
    }
}
