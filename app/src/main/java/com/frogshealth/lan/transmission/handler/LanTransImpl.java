package com.frogshealth.lan.transmission.handler;

import android.content.Context;

import com.frogshealth.lan.transcore.JavaHelper;
import com.frogshealth.lan.transmission.model.FileInfo;
import com.frogshealth.lan.transmission.model.LanUser;
import com.frogshealth.lan.transmission.utils.FileUtils;

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
    /**
     * 上下文
     */
    private Context mContext;

    public LanTransImpl(Context context) {
        super(context);
        mContext = context;
        mJ2CHelper = new JavaHelper(getHandler());
    }

    @Override
    public void init() {
        mJ2CHelper.udpInit();
    }

    @Override
    public void sendFiles(String address, List<FileInfo> files) {
        //TODO 目前只发送单一文件
        if (files == null || files.isEmpty()) {
            return;
        }
        mJ2CHelper.sendFiles(address, files.get(0).getPath());
    }

    @Override
    public void receiveFiles() {
        mJ2CHelper.receiveFiles(FileUtils.getFileSavePath(this.mContext).getAbsolutePath());
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
        mJ2CHelper.receiveFileResp(toAddress, null);
    }

    @Override
    public void rejectFile(String toAddress) {
        mJ2CHelper.rejectFileResp(toAddress, null);
    }

    @Override
    public void onlineNotify() {
        mJ2CHelper.onlineNotify();
    }
}
