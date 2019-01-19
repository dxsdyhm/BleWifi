package com.qmx.entity;

import android.text.TextUtils;
import com.qmx.utils.ByteUtils;

/**
 * @Author: dxs
 * @time: 2018/8/21
 * @Email: duanxuesong12@126.com
 * 蓝牙广播数据
 *     8B    1B 1B
 * |--------|--|--|
 */
public class BleBroadData {
    public byte[] getByte(){
        byte[] deviceid=getDeviceID();
        byte[] subData=new byte[2];
        subData[0]=getBindState();
        subData[1]= (byte) 1;
        //广播数据长度有限制，超出时数据将被截取,具体限制值需要设备测试
        byte[] realData= ByteUtils.byteMerger(deviceid,subData);
        if(realData.length>29){
            return ByteUtils.subBytes(realData,0,29);
        }
        return realData;
    }

    private byte[] getDeviceID(){
        return ByteUtils.longToBytes(10001);
    }

    /**
     * 是否绑定用户
     * @return 0:未绑定，1:已绑定
     */
    private byte getBindState(){
       return 0;
    }
}
