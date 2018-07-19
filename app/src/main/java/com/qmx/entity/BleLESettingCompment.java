package com.qmx.entity;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class BleLESettingCompment {
    private byte[] allData;
    private Map<Integer,BluePackage> list=new HashMap<Integer, BluePackage>();

    public BleLESettingCompment(byte[] allData) {
        this.allData = allData;
        initData();
    }

    private void initData() {
        if(allData!=null&&allData.length>0){
            int temp=allData.length/BleConfig.BLE_LE_SET_DATA_LENGTH;
            int sendTimes=allData.length%BleConfig.BLE_LE_SET_DATA_LENGTH==0?temp:temp+1;
            for(int i=0;i<sendTimes;i++){
                int currentLen=BleConfig.BLE_LE_SET_DATA_LENGTH;
                if((i+1)*BleConfig.BLE_LE_SET_DATA_LENGTH>allData.length){
                    //这一次取不到完整的一帧
                    currentLen=allData.length%BleConfig.BLE_LE_SET_DATA_LENGTH;
                }
                byte[] send=subBytes(allData,i*BleConfig.BLE_LE_SET_DATA_LENGTH,currentLen);
                BluePackage setting=new BluePackage(send,1);
                if(i==0){
                    setting.setStartPackage(true);
                }
                if(i==sendTimes-1){
                    setting.setEndPackage(true);
                }
                list.put(i,setting);
            }
            Log.e("dxsTest","list:"+list.size());
        }
    }

    public byte[] getAllData() {
        return allData;
    }

    public void setAllData(byte[] allData) {
        this.allData = allData;
    }

    public Map<Integer, BluePackage> getList() {
        return list;
    }

    public void setList(Map<Integer, BluePackage> list) {
        this.list = list;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }
}
