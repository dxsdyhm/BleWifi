package com.qmx.entity;

import android.bluetooth.BluetoothDevice;

public class bleViewMode {
    private int bleType;
    private BluetoothDevice device;

    public bleViewMode(int bleType, BluetoothDevice device) {
        this.bleType = bleType;
        this.device = device;
    }

    public int getBleType() {
        return bleType;
    }

    public void setBleType(int bleType) {
        this.bleType = bleType;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public int hashCode() {
        return device.getAddress().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null||!(obj instanceof bleViewMode)){
            return false;
        }
        bleViewMode ta= (bleViewMode) obj;
        if(ta.device.getAddress().equals(device.getAddress())){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "bleViewMode{" +
                "bleType=" + bleType +
                ", device=" + device +
                '}';
    }
}
