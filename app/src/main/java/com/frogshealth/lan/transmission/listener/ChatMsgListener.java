package com.frogshealth.lan.transmission.listener;

/**********************************************************************
 * 聊天消息监听。
 *
 * @类名 ChatMsgListener
 * @包名 com.frogshealth.lan.transmission.listener
 * @author yuanjf
 * @创建日期 2018/8/30
 ***********************************************************************/
public interface ChatMsgListener {
    /**
     * 收到聊天消息
     *
     * @param msg 消息
     */
    void onChatMsg(String msg);
}
