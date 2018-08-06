package com.frogshealth.lan.transmission.listener;

import com.frogshealth.lan.transmission.model.LanUser;

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
     *
     * @param user 用户
     */
    void onSendRequest(LanUser user);

    /**
     * 接收
     */
    void onReceive();

    /**
     * 拒绝
     */
    void onReject();
}
