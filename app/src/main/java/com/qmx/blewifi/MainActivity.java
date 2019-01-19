package com.qmx.blewifi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.qmx.adapter.BleDeviceAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_connect)
    Button btnConnect;
    @BindView(R.id.btn_oldblue)
    Button btnOldblue;
    private RecyclerView rcBle;
    private MultiTypeAdapter adapter;
    private Items items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BleManager.getInstance().init(getApplication());
        initUI();
        adapter.setItems(items);
        initData();
    }

    private void initData() {
        BleManager.getInstance().enableBluetooth();
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                // 开始扫描（主线程）
                Log.e("dxsTest", "success:" + success);
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
        rcBle = findViewById(R.id.rc_ble);
        adapter = new MultiTypeAdapter();
        adapter.register(BleDevice.class, new BleDeviceAdapter());
        rcBle.setLayoutManager(new LinearLayoutManager(this));
        items = new Items();
        rcBle.setAdapter(adapter);
    }

    @OnClick(R.id.btn_connect)
    public void onViewClicked() {
        startActivity(new Intent(MainActivity.this, ConnectWifiActivity.class));
    }

    @OnClick(R.id.btn_oldblue)
    public void onToOldBlue() {
        startActivity(new Intent(MainActivity.this, OldBlueActivity.class));
    }
}
