package com.frogshealth.lan.transmission.listener;

import com.frogshealth.lan.transmission.model.LanUser;

/**********************************************************************
 * 用户状态监听类。
 *
 * @类名 UserStateListener
 * @包名 com.frogshealth.lan.transmission.listener
 * @author yuanjf
 * @创建日期 18/8/2
 ***********************************************************************/
public interface UserStateListener {
    /**
     * 用户上线
     *
     * @param user 用户
     */
    void online(LanUser user);

    /**
     * 用户离线
     *
     * @param user 用户
     */
    void offline(LanUser user);
}
