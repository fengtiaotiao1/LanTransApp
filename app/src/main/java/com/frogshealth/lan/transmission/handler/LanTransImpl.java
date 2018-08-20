package com.frogshealth.lan.transmission.handler;

import android.content.Context;

import com.frogshealth.lan.transcore.JavaHelper;
import com.frogshealth.lan.transmission.model.FileInfo;
import com.frogshealth.lan.transmission.model.LanUser;

import java.util.List;

/**********************************************************************
 *
 *
 * @类名 LanTransImpl
 * @包名 com.frogshealth.lan.transmission.handler
 * @author yuanjf
 * @创建日期 18/8/20
 ***********************************************************************/
public class LanTransImpl extends LanTransAgent {
    /**
     * JAVA2C辅助类
     */
    private JavaHelper mJ2CHelper;

    public LanTransImpl(Context context) {
        super(context);
        mJ2CHelper = new JavaHelper(getHandler());
    }

    @Override
    public void init() {
        mJ2CHelper.udpInit();
    }

    @Override
    public void sendFiles(String address, List<FileInfo> files) {
    }

    @Override
    public void receiveFiles() {
    }

    @Override
    public List<LanUser> getLanUsers() {
        return null;
    }

    @Override
    public void sendFileRequest(String toAddress) {
        mJ2CHelper.sendFileReq(toAddress, null);
    }

    @Override
    public void receiveFile(String toAddress) {
    }

    @Override
    public void rejectFile(String toAddress) {
    }

    @Override
    public void onlineNotify() {
        mJ2CHelper.onlineNotify();
    }
}
