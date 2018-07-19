package com.qmx.entity;

import java.util.UUID;

public class BleConfig {
    public static String UUID_SERVER = "00001101-0000-1000-8000-00805F9B34FB";
    public static String UUID_CHARREAD = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHARWRITE = "0000fff2-0000-1000-8000-00805f9b34fb";
    public static String UUID_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";
    /**
     * 分包数据长度
     **/
    public static final int BLE_LE_DATA_LENGTH = 20;
    /****/
    public static final int BLE_LE_SET_DATA_LENGTH = 17;
}
