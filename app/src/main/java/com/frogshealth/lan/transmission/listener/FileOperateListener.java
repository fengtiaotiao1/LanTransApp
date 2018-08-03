package com.frogshealth.lan.transmission.listener;

/**********************************************************************
 * 文件操作监听类。
 *
 * @类名 FileOperateListener
 * @包名 com.frogshealth.lan.transmission.listener
 * @author yuanjf
 * @创建日期 18/8/2
 ***********************************************************************/
public interface FileOperateListener {
    /**
     * 发送请求
     */
    void onSendRequest();

    /**
     * 接收
     */
    void onReceive();

    /**
     * 拒绝
     */
    void onReject();
}
