package com.frogshealth.lan.transmission.model;

import com.google.gson.annotations.SerializedName;

/**********************************************************************
 * UDP消息协议
 *
 * @类名 UdpMsgProtocol
 * @包名 com.frogshealth.lan.transmission.model
 * @author yuanjf
 * @创建日期 18/8/1
 ***********************************************************************/
public class UdpMsgProtocol {
    /**
     * 指令编号
     */
    @SerializedName("commandNo")
    private int mCommandNo;
    /**
     * 消息发送地址
     */
    @SerializedName("address")
    private String mAddress;
    /**
     * 消息接收地址
     */
    @SerializedName("toAddress")
    private String mToAddress;
    /**
     * 消息接收端口
     */
    @SerializedName("toPort")
    private int mToPort;
    /**
     * 附加数据
     */
    @SerializedName("additional")
    private String mAdditionalSection;

    public UdpMsgProtocol(int command) {
        this.mCommandNo = command;
    }

    public int getCommandNo() {
        return mCommandNo;
    }

    public void setCommandNo(int commandNo) {
        this.mCommandNo = commandNo;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String getToAddress() {
        return mToAddress;
    }

    public void setToAddress(String toAddress) {
        this.mToAddress = toAddress;
    }

    public int getToPort() {
        return mToPort;
    }

    public void setToPort(int toPort) {
        this.mToPort = toPort;
    }

    public String getAdditionalSection() {
        return mAdditionalSection;
    }

    public void setAdditionalSection(String additionalSection) {
        this.mAdditionalSection = additionalSection;
    }
}
