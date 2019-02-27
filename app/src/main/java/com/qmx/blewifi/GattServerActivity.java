package com.qmx.blewifi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.Utils;
import com.clj.fastble.BleManager;
import com.qmx.entity.BleBroadData;
import com.qmx.entity.BleConfig;
import com.qmx.entity.BleWifiInfo;
import com.qmx.entity.BluePackage;
import com.qmx.utils.ByteUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.UUID;

/**
 * 仅供测试
 */
public class GattServerActivity extends AppCompatActivity {
    public static final UUID UUID_SERVER=UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static UUID UUID_CHARNOTIFY = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public static UUID UUID_CHARWRITE = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    public static UUID UUID_CHARREAD = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb");
    public static UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private Button btnOpen;
    private TextView txData;
    private final static String TAG=GattServerActivity.class.getSimpleName();


    BluetoothManager mBluetoothManager;
    private BluetoothGattServer bluetoothGattServer;
    private BluetoothGattCharacteristic characteristicRead;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeAdvertiser bluetoothLeAdvertiser;
    BluetoothGattService service;
    private BluetoothDevice currentDevice;
    BluetoothGattCharacteristic characteristicWrite;
    BluetoothGattCharacteristic characteristicNotify;

    private byte[] result=new byte[0];
    BleBroadData data=new BleBroadData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gatt_server);
        mBluetoothManager=(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter=mBluetoothManager.getAdapter();
        bluetoothLeAdvertiser=mBluetoothAdapter.getBluetoothLeAdvertiser();
        service=new BluetoothGattService(UUID_SERVER, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        initUI();
    }

    private void initUI() {
        btnOpen=findViewById(R.id.btn_openble);
        txData=findViewById(R.id.tx_data);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BleManager.getInstance().enableBluetooth();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initGATTServer();
                    }
                },5000);
            }
        });
        findViewById(R.id.btn_notify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToClient(ByteUtils.longToBytes(System.currentTimeMillis()));
            }
        });
    }

    private void initServices(Context context) {
        bluetoothGattServer = mBluetoothManager.openGattServer(context, bluetoothGattServerCallback);
        //add a read characteristic.
        characteristicRead = new BluetoothGattCharacteristic(UUID_CHARREAD, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
        //add a descriptor
        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(UUID_DESCRIPTOR, BluetoothGattCharacteristic.PERMISSION_WRITE);
        characteristicRead.addDescriptor(descriptor);
        service.addCharacteristic(characteristicRead);

        //add a write characteristic.
        characteristicWrite = new BluetoothGattCharacteristic(UUID_CHARWRITE,
                BluetoothGattCharacteristic.PROPERTY_WRITE |
                        BluetoothGattCharacteristic.PROPERTY_READ |
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        service.addCharacteristic(characteristicWrite);

        characteristicNotify=new BluetoothGattCharacteristic(UUID_CHARNOTIFY,BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);
        service.addCharacteristic(characteristicNotify);

        bluetoothGattServer.addService(service);
        Log.e(TAG, "2. initServices ok");
    }

    private void initGATTServer() {
        boolean is=mBluetoothAdapter.isMultipleAdvertisementSupported();
        Log.e("dxsTest","isMultipleAdvertisementSupported:"+is);
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setConnectable(true)
                .build();

        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(true)
                .addManufacturerData(0x4c, data.getByte())
                .build();

        AdvertiseData scanResponseData = new AdvertiseData.Builder()
                .addServiceUuid(new ParcelUuid(UUID_SERVER))
                .addServiceData(new ParcelUuid(UUID_SERVER),data.getByte())
                .setIncludeTxPowerLevel(true)
                .build();


        AdvertiseCallback callback = new AdvertiseCallback() {

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d(TAG, "BLE advertisement added successfully");
                println("1. initGATTServer success");
                initServices(GattServerActivity.this);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e(TAG, "Failed to add BLE advertisement, reason: " + errorCode);
                println("Failed to add BLE advertisement, reason: " + errorCode);
            }
        };
        bluetoothLeAdvertiser.startAdvertising(settings, advertiseData, scanResponseData, callback);
    }

    /**
     * 服务事件的回调
     */
    private BluetoothGattServerCallback bluetoothGattServerCallback = new BluetoothGattServerCallback() {

        /**
         * 1.连接状态发生变化时
         * @param device
         * @param status
         * @param newState
         */
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            Log.e(TAG, String.format("1.onConnectionStateChange：device name = %s, address = %s", device.getName(), device.getAddress()));
            Log.e(TAG, String.format("1.onConnectionStateChange：status = %s, newState =%s ", status, newState));
            super.onConnectionStateChange(device, status, newState);
            if(newState==0){
                currentDevice=null;
            }else if(newState==2){
                currentDevice=device;
            }
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
            Log.e(TAG, String.format("onServiceAdded：status = %s", status));
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            Log.e(TAG, String.format("onCharacteristicReadRequest：device name = %s, address = %s", device.getName(), device.getAddress()));
            Log.e(TAG, String.format("onCharacteristicReadRequest：requestId = %s, offset = %s", requestId, offset));
            if(characteristic.getUuid().equals(UUID_CHARREAD)){
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, data.getByte());
            }else {
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
            }
        }

        /**
         * 3. onCharacteristicWriteRequest,接收具体的字节
         * @param device
         * @param requestId
         * @param characteristic
         * @param preparedWrite
         * @param responseNeeded
         * @param offset
         * @param requestBytes
         */
        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] requestBytes) {
            Log.e(TAG, String.format("3.onCharacteristicWriteRequest：device name = %s, address = %s", device.getName(), device.getAddress()));
            currentDevice=device;
            bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, requestBytes);
            //4.处理响应内容
            onResponseToClient(requestBytes, device, requestId, characteristic);
        }

        /**
         * 2.描述被写入时，在这里执行 bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS...  收，触发 onCharacteristicWriteRequest
         * @param device
         * @param requestId
         * @param descriptor
         * @param preparedWrite
         * @param responseNeeded
         * @param offset
         * @param value
         */
        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            Log.e(TAG, String.format("2.onDescriptorWriteRequest：device name = %s, address = %s", device.getName(), device.getAddress()));
            // now tell the connected device that this was all successfull
            bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
        }

        /**
         * 5.特征被读取。当回复响应成功后，客户端会读取然后触发本方法
         * @param device
         * @param requestId
         * @param offset
         * @param descriptor
         */
        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            Log.e(TAG, String.format("onDescriptorReadRequest：device name = %s, address = %s", device.getName(), device.getAddress()));
            Log.e(TAG, String.format("onDescriptorReadRequest：requestId = %s", requestId));
            if(descriptor.getUuid().equals(UUID_DESCRIPTOR)){
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, data.getByte());
            }else {
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, descriptor.getValue());
            }
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            Log.e(TAG, String.format("5.onNotificationSent：device name = %s, address = %s", device.getName(), device.getAddress()));
            Log.e(TAG, String.format("5.onNotificationSent：status = %s", status));
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
            Log.e(TAG, String.format("onMtuChanged：mtu = %s", mtu));
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
            Log.e(TAG, String.format("onExecuteWrite：requestId = %s", requestId));
        }
    };

    /**
     * 4.处理响应内容
     *
     * @param reqeustBytes
     * @param device
     * @param requestId
     * @param characteristic
     */
    private void onResponseToClient(byte[] reqeustBytes, BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic) {
        BluePackage setting=new BluePackage(reqeustBytes);
        if(setting.isCorrectMagic()&&setting.isCorrectXor()){
            if(setting.isStartPackage()){
                result= new byte[0];
            }
            result= ByteUtils.byteMerger(result,setting.getData());
            println("1.收到:" + Arrays.toString(setting.getData()));
            if(setting.isEndPackage()){
                if(result[0]== BleConfig.CMD.BLE_CMD_WIFI){
                    //配网
                    BleWifiInfo info=new BleWifiInfo(result);
                    println("配网消息：" +info.toString());
                    //模拟成功
                    sendToClient(new byte[]{2,1,0,0,0,0});
                }else {
                    println("未知指令:" + Arrays.toString(result));
                    //将收到的数组原样返回
                    result[0]=4;
                    println(Arrays.toString(result));
                }
            }
        }else {
            println("4.收到:" + Arrays.toString(reqeustBytes));
            reqeustBytes[0]=3;
        }
    }

    public void println(String data){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txData.append("\n");
                txData.append(data);
            }
        });

    }

    private void sendToClient(byte[] toClient){
        Log.e(TAG,"sendToClient:"+Arrays.toString(toClient));
        if(currentDevice==null){
            return;
        }
        characteristicNotify.setValue(toClient);
        bluetoothGattServer.notifyCharacteristicChanged(currentDevice, characteristicNotify, false);
    }
}
