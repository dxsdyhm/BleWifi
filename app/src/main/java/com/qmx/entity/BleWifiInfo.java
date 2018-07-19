package com.qmx.entity;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.qmx.utils.ByteUtils;

/**
 * 蓝牙指令中的配网指令数据
 */
public class BleWifiInfo extends BaseBleData {
    private final static int WIFI_LEN_MAX=128;
    private String wifiSSID="";//128 byte
    private String wifiPwd="";//128 byte

    public BleWifiInfo(String wifiSSID, String wifiPwd) {
        super((byte) BleConfig.CMD.BLE_CMD_WIFI);
        this.wifiSSID = wifiSSID;
        this.wifiPwd = wifiPwd;
    }

    @Override
    public void paseData(byte[] realData) {
        if(realData.length>=WIFI_LEN_MAX*2){
            byte[] ssid= ByteUtils.subBytes(realData,0,WIFI_LEN_MAX);
            wifiPwd=new String(ssid).trim();
            byte[] pwd=ByteUtils.subBytes(realData,WIFI_LEN_MAX+1,WIFI_LEN_MAX);
            wifiPwd=new String(pwd).trim();
        }
    }

    @Override
    public byte[] setData() {
        byte[] ssid=new byte[WIFI_LEN_MAX];
        byte[] pwd=new byte[WIFI_LEN_MAX];
        if(!TextUtils.isEmpty(wifiSSID)){
            System.arraycopy(wifiSSID.getBytes(),0,ssid,0,WIFI_LEN_MAX);
        }
        if(!TextUtils.isEmpty(wifiPwd)){
            System.arraycopy(wifiPwd.getBytes(),0,pwd,0,WIFI_LEN_MAX);
        }
        return ByteUtils.byteMerger(ssid,pwd);
    }

    public String getWifiSSID() {
        return wifiSSID;
    }

    public void setWifiSSID(String wifiSSID) {
        this.wifiSSID = wifiSSID;
    }

    public String getWifiPwd() {
        return wifiPwd;
    }

    public void setWifiPwd(String wifiPwd) {
        this.wifiPwd = wifiPwd;
    }

    @Override
    public String toString() {
        return "BleWifiInfo{" +
                ", wifiSSID='" + wifiSSID + '\'' +
                ", wifiPwd='" + wifiPwd + '\'' +
                '}';
    }
}