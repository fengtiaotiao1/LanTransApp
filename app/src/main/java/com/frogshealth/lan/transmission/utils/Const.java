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
    /**
     * UTF-8
     */
    public static final String UTF8 = "UTF-8";

    /**
     * 头部分割字符
     */
    public static final String SPERATOR = "::";

    /**
     * 每次读取字节数组长度
     */
    public static final int BYTE_SIZE_DATA = 1024 * 4;

    /**
     * 传输文件类型
     */
    public static final int TYPE_FILE = 1; //文件类型

    /**
     * 文件传输监听 默认端口
     */
    public static final int DEFAULT_SERVER_PORT = 8080;

    /**
     * 文件保存路径
     */
    public static final String SAVE_PATH = "file-save";
}
