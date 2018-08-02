package com.frogshealth.lan.transmission;

/**********************************************************************
 * UDP消息协议
 *
 * @类名 UdpMsgProtocol
 * @包名 com.frogshealth.lan.transmission
 * @author yuanjf
 * @创建日期 18/8/1
 ***********************************************************************/
public class UdpMsgProtocol {
    /**
     * 指令编号
     */
    private int mCommandNo;
    /**
     * 附加数据
     */
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

    public String getAdditionalSection() {
        return mAdditionalSection;
    }

    public void setAdditionalSection(String additionalSection) {
        this.mAdditionalSection = additionalSection;
    }
}
