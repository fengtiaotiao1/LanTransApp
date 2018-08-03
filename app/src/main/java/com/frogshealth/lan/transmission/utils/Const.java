package com.frogshealth.lan.transmission.utils;

/**********************************************************************
 * 常量类。
 *
 * @类名 Const
 * @包名 com.frogshealth.lan.transmission
 * @author yuanjf
 * @创建日期 18/8/1
 ***********************************************************************/
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
    public static final int MSG_USER_ONLINE = 1;
    /**
     * 用户离线
     */
    public static final int MSG_USER_OFFLINE = 2;
    /**
     * 用户上线应答
     */
    public static final int MSG_ONLINE_ANSWER = 3;
    /**
     * 文件接收
     */
    public static final int MSG_FILE_RECEIVE = 4;
    /**
     * 文件拒绝接收
     */
    public static final int MSG_FILE_REJECT = 5;
    /**
     * 发送文件请求
     */
    public static final int MSG_FILE_SEND_REQUEST = 6;

}
