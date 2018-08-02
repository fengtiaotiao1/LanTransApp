package com.frogshealth.lan.transmission;

/**
 * 飞鸽协议常量
 *
 * @author ccf
 * 2012/2/10
 */
public class Const {
    /**
     * 端口号：默认端口2425
     */
    public static final int PORT = 0x0979;
    /**
     * 广播地址
     */
    public static final String BROADCAST_ADDR = "255.255.255.255";
    /**
     * 用户上线
     */
    public static final int MSG_USER_ONLINE = 0x00000001;
    /**
     * 用户离线
     */
    public static final int MSG_USER_OFFLINE = 0x00000002;
    /**
     * 用户上线应答
     */
    public static final int MSG_ONLINE_ANSWER = 0x00000003;

}
