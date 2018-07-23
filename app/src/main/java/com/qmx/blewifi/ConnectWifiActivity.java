package com.qmx.blewifi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jflavio1.wificonnector.WifiConnector;
import com.jflavio1.wificonnector.interfaces.ConnectionResultListener;
import com.jflavio1.wificonnector.interfaces.ShowWifiListener;
import com.thanosfisherman.wifiutils.WifiUtils;

import org.json.JSONArray;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnectWifiActivity extends AppCompatActivity {

    @BindView(R.id.tx_wifiname)
    TextView txWifiname;
    @BindView(R.id.tx_wifipwd)
    TextView txWifipwd;
    @BindView(R.id.btn_connect)
    Button btnConnect;
    @BindView(R.id.tx_result)
    TextView txResult;

    private ConnectionResultListener listener = new ConnectionResultListener() {
        @Override
        public void successfulConnect(String SSID) {
            txResult.append("连接成功："+SSID);
            txResult.append("\n");
        }

        @Override
        public void errorConnect(int codeReason) {
            txResult.append("连接失败："+codeReason);
            txResult.append("\n");
        }

        @Override
        public void onStateChange(SupplicantState supplicantState) {
            txResult.append("正在连接-->"+supplicantState);
            txResult.append("\n");
        }
    };
    WifiConnector connector ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wifi);
        ButterKnife.bind(this);
//        WifiUtils.enableLog(true);
//        WifiUtils.withContext(getApplicationContext()).enableWifi();
        requestLocationPermission();
        connector= new WifiConnector(this);
//        WifiUtils.withContext(getApplicationContext()).scanWifi(this::getScanResults).start();
    }

    private void getScanResults(@NonNull final List<ScanResult> results) {
        if (results.isEmpty()) {
            Log.e("dxsTest", "SCAN RESULTS IT'S EMPTY");
            return;
        }
        Log.e("dxsTest", "GOT SCAN RESULTS " + results);
    }

    private void checkResult(boolean isSuccess) {
        Log.e("dxsTest", "checkResult " + isSuccess);
    }

    @OnClick(R.id.btn_connect)
    public void onViewClicked() {
//        WifiUtils.withContext(getApplicationContext())
//                .connectWith("GW-2.4G", "29418680")
//                .setTimeout(15000)
//                .onConnectionResult(this::checkResult)
//                .start();

        otherConnect("GW-2.4G", "29418680");
    }


    private void otherConnect(final String name, final String pwd) {
        connector.enableWifi();
        connector.showWifiList(new ShowWifiListener() {
            @Override
            public void onNetworksFound(WifiManager wifiManager, List<ScanResult> wifiScanResult) {
                if (wifiScanResult.isEmpty()) {
                    Log.e("dxsTest", "SCAN RESULTS IT'S EMPTY");
                }
                for (ScanResult result : wifiScanResult) {
                    if (result.SSID.equals(name)) {
                        connector.setScanResult(result, pwd);
                        connector.connectToWifi(listener);
                    }
                }

            }

            @Override
            public void onNetworksFound(JSONArray wifiList) {

            }

            @Override
            public void errorSearchingNetworks(int errorCode) {
                connector.setWifiConfiguration(name, "", "WEP", pwd);
                connector.connectToWifi(listener);
            }
        });

    }
    private void unreist(){
        connector.unregisterShowWifiListListener();
        connector.unregisterWifiConnectionListener();
    }

    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(this, "自Android 6.0开始需要打开位置权限才可以搜索到WIFI设备", Toast.LENGTH_SHORT);

                }
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }
        }
    }
}
