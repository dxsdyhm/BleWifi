package com.qmx.entity;

import android.util.Log;

import com.qmx.utils.ByteUtils;

import java.util.Arrays;

/**
 * 联网结果反馈
 * 结果中SSID不再携带
 */
public class ConnectResult extends BaseBleData {

    private byte connectState=0;//正在联网 1 success 其他值:error
    private int errorCode;

    public ConnectResult(byte connectState, int errorCode) {
        this.connectState = connectState;
        this.errorCode = errorCode;
    }

    @Override
    public void paseData(byte[] realData) {
        if(realData.length>=5){
            connectState=realData[0];
            errorCode=ByteUtils.byteArrayToInt(realData,1);
        }
    }

    @Override
    public byte[] setData() {
        byte[] code=ByteUtils.intToByteArray(errorCode);
        return ByteUtils.byteMerger(new byte[]{connectState},code);
    }

    @Override
    public String toString() {
        return "ConnectResult{" +
                "connectState=" + connectState +
                ", errorCode=" + errorCode +
                '}';
    }
}
