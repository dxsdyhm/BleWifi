package com.qmx.entity;

import android.util.Log;

import java.util.Arrays;

public class BluePackage {
    public static byte MAGIC_VALUE = (byte) ('B' + 'T');
    private static byte INDEX_SBIT=0;
    private static byte INDEX_EBIT = 1;
    private static byte INDEX_EXH=2;
    private static byte INDEX_VER = 4;
    private static byte INDEX_RESERV = 5;
    private byte magic;//magic, 'B'+'T',判断数据有效性,不等于'B'+'T',则直接丢弃
    private byte xor;//xor, 本包数据除去magic和xor,之后的数据做异或运算,若不与xor相等直接丢弃
    private byte identification;//标识   sbit:bit1 | ebit:bit1 | exh:bit1 | ver:bit2 | reserv:bit3
    private byte[] data;//数据项,数据项长度为本次蓝牙接收到的数据减去 PROT_BT_COMMU_TOP_S长度

    public BluePackage() {
    }

    public BluePackage(byte magic, byte xor, byte identification, byte[] data) {
        this.magic = magic;
        this.xor = xor;
        this.identification = identification;
        this.data = data;
    }

    public BluePackage(byte[] data){
        if(data.length>3){
            this.magic=data[0];
            this.xor=data[1];
            this.identification=data[2];
            this.data= new byte[data.length-3];
            System.arraycopy(data,3,this.data,0,this.data.length);
        }else {
            Log.e("dxsTest","BluePackage init error:"+Arrays.toString(data));
        }
    }

    public boolean isCorrectMagic(){
        return this.magic == MAGIC_VALUE;
    }

    public void setMagic(byte magic) {
        this.magic = magic;
    }

    public void setStartPackage(boolean isStart){
        if (isStart) {
            this.identification = (byte) (this.identification | 1 << INDEX_SBIT);
        }
    }

    public void setEndPackage(boolean isEnd){
        if (isEnd) {
            this.identification = (byte) (this.identification | 1 << INDEX_EBIT);
        }
    }

    public boolean isStartPackage(){
        return ((identification >> INDEX_SBIT) & 0x1) == 1;
    }

    public boolean isEndPackage(){
        return ((identification >> INDEX_EBIT) & 0x1) == 1;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean isCorrectXor(){
        byte value;
        value = identification;
        if (data != null) {
            for (int i=0;i<data.length;i++) {
                value = (byte) (value ^ data[i]);
            }
        }
        return value == xor;
    }


    public byte[] getSettingData(){
        byte[] settingData;
        if (data == null) {
            settingData = new byte[3];
            settingData[0] = magic;
            settingData[2] = identification;
            xor = identification;
        }else {
            if (data.length < BleConfig.BLE_LE_SET_DATA_LENGTH) {
                settingData = new byte[3 + data.length];
                System.arraycopy(data, 0, settingData, 3, data.length);
            }else {
                settingData = new byte[BleConfig.BLE_LE_DATA_LENGTH];
                System.arraycopy(data, 0, settingData, 3, BleConfig.BLE_LE_SET_DATA_LENGTH);
            }
            settingData[0] = magic;
            settingData[2] = identification;
            for (int i=2;i<settingData.length;i++) {
                xor = (byte) (xor ^ settingData[i]);
            }
        }
        settingData[1] = xor;
        return settingData;
    }

    public boolean setSettingData(byte[] settingData){
        if (settingData == null || settingData.length > BleConfig.BLE_LE_DATA_LENGTH) {
            return false;
        }
        magic = settingData[0];
        xor = settingData[1];
        identification = settingData[2];
        if (settingData.length - 3 < BleConfig.BLE_LE_SET_DATA_LENGTH) {
            data = new byte[settingData.length - 3];
        }else {
            data = new byte[BleConfig.BLE_LE_SET_DATA_LENGTH];
        }
        System.arraycopy(settingData, 3, data, 0, data.length);
        return true;
    }
}
