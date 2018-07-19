package com.qmx.adapter;

import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.qmx.blewifi.R;
import com.qmx.blewifi.WifiConfigActivity;
import com.qmx.entity.bleViewMode;

import me.drakeet.multitype.ItemViewBinder;

public class BleDeviceAdapter extends ItemViewBinder<BleDevice,BleDeviceAdapter.TextHolder> {
    @NonNull
    @Override
    protected BleDeviceAdapter.TextHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_ble, parent, false);
        return new BleDeviceAdapter.TextHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull final TextHolder holder, @NonNull final BleDevice item) {
        holder.text.setText("hello: " + item.getDevice().getName());
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleManager.getInstance().connect(item, new BleGattCallback() {
                    @Override
                    public void onStartConnect() {
                        Log.e("dxsTest","onStartConnect:"+item.getName());
                    }

                    @Override
                    public void onConnectFail(BleDevice bleDevice, BleException exception) {
                        Log.e("dxsTest","onConnectFail:"+exception.getDescription());
                    }

                    @Override
                    public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                        Intent intent=new Intent();
                        intent.setClass(holder.text.getContext(), WifiConfigActivity.class);
                        intent.putExtra(item.getClass().getSimpleName(),item);
                        holder.text.getContext().startActivity(intent);
                    }

                    @Override
                    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                        Log.e("dxsTest","onDisConnected:"+status);
                    }
                });
            }
        });
    }

    static class TextHolder extends RecyclerView.ViewHolder {
        private @NonNull
        final TextView text;

        TextHolder(@NonNull View itemView) {
            super(itemView);
            this.text = (TextView) itemView.findViewById(R.id.tx_blename);
        }
    }
}
