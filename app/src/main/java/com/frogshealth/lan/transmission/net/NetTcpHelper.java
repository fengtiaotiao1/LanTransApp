package com.frogshealth.lan.transmission.net;

import android.os.Message;

import com.frogshealth.lan.transmission.handler.LanMsgHandler;
import com.frogshealth.lan.transmission.utils.Const;

/**********************************************************************
 *
 * TCP辅助类
 *
 * @类名 NetTcpHelper
 * @包名 com.frogshealth.lan.transmission.net
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/9
 ***********************************************************************/
public class NetTcpHelper {
    /**
     * 单例
     */
    private static NetTcpHelper sInstance;
    /**
     * Handler对象
     */
    private LanMsgHandler mHandler;

    /**
     * 获取单例
     *
     * @return 单例
     */
    public static NetTcpHelper getInstance() {
        if (sInstance == null) {
            synchronized (NetTcpHelper.class) {
                if (sInstance == null) {
                    sInstance = new NetTcpHelper();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     *
     * @param mHandler LanMsgHandler
     */
    public void init(LanMsgHandler mHandler) {
        this.mHandler = mHandler;
    }

    /**
     * 文件上传进度
     *
     * @param flag             7为开始发送，8为开始接收
     * @param name             文件名称
     * @param alreadyReadBytes 已经上传字节数
     * @param fileSize         文件总大小
     */
    public void upLoading(int flag, String name, long alreadyReadBytes, long fileSize) {
        Message msg = mHandler.obtainMessage(Const.IS_SEND_OR_RECEIVE_UPLOAD);
        msg.arg1 = flag;
        msg.arg2 = (int) (alreadyReadBytes * 100f / fileSize);
        msg.obj = name;
        mHandler.sendMessage(msg);
    }

    /**
     * 文件开始传输
     *
     * @param flag 7为开始发送，8为开始接收
     */
    public void startTransmission(int flag) {
        Message msg = mHandler.obtainMessage(Const.IS_SEND_OR_RECEIVE_START);
        msg.arg1 = flag;
        mHandler.sendMessage(msg);
    }

    /**
     * 文件传输出现异常
     *
     * @param e    Exception
     * @param flag 7为开始发送，8为开始接收
     */
    public void failForReceiveOrSend(Exception e, int flag) {
        Message msg = mHandler.obtainMessage(Const.IS_SEND_OR_RECEIVE_FAIL);
        msg.arg1 = flag;
        msg.obj = e;
        mHandler.sendMessage(msg);
    }

    /**
     * 文件传输成功
     *
     * @param fileName 文件名称
     * @param flag     7为开始发送，8为开始接收
     */
    public void successForReceiveOrSend(String fileName, int flag) {
        Message msg = mHandler.obtainMessage(Const.IS_SEND_OR_RECEIVE_SUCCESS);
        msg.arg1 = flag;
        msg.obj = fileName;
        mHandler.sendMessage(msg);
    }

    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        sInstance = null;
    }
}
