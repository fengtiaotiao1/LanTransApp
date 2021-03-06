package com.frogshealth.lan.transmission.model;

/**********************************************************************
 * 局域网用户
 *
 * @类名 LanUser
 * @包名 com.frogshealth.lan.transmission.model
 * @author yuanjf
 * @创建日期 18/8/1
 ***********************************************************************/
public class LanUser {
    /**
     * 用户名
     */
    private String mUserName;
    /**
     * IP地址
     */
    private String mIp;

    public LanUser(String userName) {
        this.mUserName = userName;
    }

    public LanUser(String userName, String ip) {
        this.mUserName = userName;
        this.mIp = ip;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public String getIp() {
        return mIp;
    }

    public void setIp(String ip) {
        this.mIp = ip;
    }
}
