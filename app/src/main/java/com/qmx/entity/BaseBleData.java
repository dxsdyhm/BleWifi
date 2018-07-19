package com.qmx.entity;

import android.support.annotation.NonNull;

import com.qmx.utils.ByteUtils;


public abstract class BaseBleData {
    public byte cmd;
    public byte[] data;

    public BaseBleData() {
    }

    public BaseBleData(byte cmd) {
        this.cmd = cmd;
    }

    public BaseBleData(byte cmd, byte[] data) {
        this.cmd = cmd;
        this.data = data;
    }

    public BaseBleData(@NonNull byte[] data) {
        this.cmd = data[0];
        if(data.length>1){
            this.data = ByteUtils.subBytes(data,1,data.length-1);
        }else {
            this.data=new byte[0];
        }
    }

    public void init(){
        paseData(this.data);
    }
    public abstract void paseData(byte[] realData);
    public abstract byte[] setData();

    /**
     * 获取拼接完整的数据
     * @return
     */
    public byte[] getRealData() {
        byte[] cm=new byte[]{cmd};
        return ByteUtils.byteMerger(cm,setData());
    }
}
