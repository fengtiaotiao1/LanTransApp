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
     * @param mHandler LanMsgHandler
     */
    public void init(LanMsgHandler mHandler) {
        this.mHandler = mHandler;
    }
    /**
     * 文件开始传输
     */
    public void startForReceive() {
        Message msg = mHandler.obtainMessage(Const.STARTT_FOR_RECEIVE);
        mHandler.sendMessage(msg);
    }
    /**
     * 文件传输出现异常
     * @param e Exception
     */
    public void failForReceive(Exception e) {
        Message msg = mHandler.obtainMessage(Const.FAIL_FOR_RECEIVE);
        msg.obj = e;
        mHandler.sendMessage(msg);
    }
    /**
     * 文件接收进度
     * @param name 文件名称
     * @param alreadyReadBytes 已经上传字节数
     * @param fileSize 文件总大小
     */
    public void uploadForReceive(String name, long alreadyReadBytes, long fileSize) {
        Message msg = mHandler.obtainMessage(Const.UPLOAD_FOR_RECEIVE);
        msg.obj = name + "," + alreadyReadBytes + "," + fileSize;
        mHandler.sendMessage(msg);
    }
    /**
     * 文件传输成功回调
     * @param fileName 文件名称
     */
    public void successForReceive(String fileName) {
        Message msg = mHandler.obtainMessage(Const.SUCCESS_FOR_RECEIVE);
        msg.obj = fileName;
        mHandler.sendMessage(msg);
    }
    /**
     * 文件上传进度
     * @param name 文件名称
     * @param alreadyReadBytes 已经上传字节数
     * @param fileSize 文件总大小
     */
    public void uploadForSend(String name, long alreadyReadBytes, long fileSize) {
        Message msg = mHandler.obtainMessage(Const.UPLOAD_FOR_SEND);
        msg.obj = name + "," + alreadyReadBytes + "," + fileSize;
        mHandler.sendMessage(msg);
    }

    /**
     * 文件传输成功回调
     * @param fileName 文件名称
     */
    public void successForSend(String fileName) {
        Message msg = mHandler.obtainMessage(Const.SUCCESS_FOR_SEND);
        msg.obj = fileName;
        mHandler.sendMessage(msg);
    }
    /**
     * 文件开始传输
     */
    public void startForSend() {
        Message msg = mHandler.obtainMessage(Const.STARTT_FOR_SEND);
        mHandler.sendMessage(msg);
    }
    /**
     * 文件传输出现异常
     * @param e Exception
     */
    public void failForSend(Exception e) {
        Message msg = mHandler.obtainMessage(Const.FAIL_FOR_SEND);
        msg.obj = e;
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
