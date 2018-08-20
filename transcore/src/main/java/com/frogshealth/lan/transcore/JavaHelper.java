package com.frogshealth.lan.transcore;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**********************************************************************
 *
 *
 * @类名 JavaHelper
 * @包名 com.frogshealth.lan.transcore
 * @author yuanjf
 * @创建日期 18/8/20
 ***********************************************************************/
public class JavaHelper {
    /**
     * 消息处理句柄
     */
    private Handler mHandler;

    static {
        System.loadLibrary("native-lib");
    }

    public JavaHelper(Handler handler) {
        this.mHandler = handler;
    }

    public void onMsgNotify(final int cmd, final String address, final String msg) {
        Log.e("JavaHelper== ", "onMsgNotify " + cmd + " // " + address + " // " + msg);
        if (mHandler == null) {
            return;
        }
        Message message = mHandler.obtainMessage(cmd);
        message.obj = new Object[]{address, msg};
        mHandler.sendMessage(message);
    }

    public native void udpInit();

    public native void sendFileReq(String destAddress, String fileName);

    public native void rejectFile(String destAddress, String fileName);

    public native void receiveFile(String destAddress, String fileName);

    public native void onlineNotify();

    public native void offlineNotify();

}
