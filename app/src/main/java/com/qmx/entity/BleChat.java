package com.qmx.entity;

/**
 * 蓝牙通信
 */
public class BleChat {
    /**
     * 指令集合 1配网
     */
    private byte cmd;
    /**
     * 区分客户端和服务端
     */
    private byte user;
    private byte b1;//预留
    private byte b2;
    /**
     * 数据长度
     */
    private int dataLen;
    /**
     * 数据
     */
    private byte[] data;
}
