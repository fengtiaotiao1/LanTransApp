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

    /**
     * udp消息通知
     *
     * @param cmd     命令
     * @param address 地址
     * @param msg     携带消息
     */
    public void onMsgNotify(final int cmd, final String address, final String msg) {
        Log.e("JavaHelper== ", "onMsgNotify " + cmd + " // " + address + " // " + msg);
        if (mHandler == null) {
            return;
        }
        Message message = mHandler.obtainMessage(cmd);
        message.obj = new Object[]{address, msg};
        mHandler.sendMessage(message);
    }

    /**
     * 文件传输通知
     *
     * @param status   开始 or 进行中 or 结束
     * @param flag     发送 or 接收
     * @param fileName 文件名
     * @param percent  传输百分比
     */
    public void onFileTransNotify(final int status, final int flag, final int percent, String fileName) {
        Log.e("JavaHelper== ", "onFileTransNotify " + status + " // " + flag + " // " + fileName + " // " + percent);
        if (mHandler == null) {
            return;
        }
        if (status == 7) {
            return;
        }
        Message message = mHandler.obtainMessage(status);
        message.arg1 = flag;
        message.arg2 = percent;
        message.obj = fileName;
        mHandler.sendMessage(message);
    }

    public native void udpInit();

    public native void sendFileReq(String destAddress, String fileName);

    public native void rejectFileResp(String destAddress, String fileName);

    public native void receiveFileResp(String destAddress, String fileName);

    public native void onlineNotify();

    public native void offlineNotify();

    public native void sendFiles(String destAddress, String path);

    public native void receiveFiles(String path);

}
