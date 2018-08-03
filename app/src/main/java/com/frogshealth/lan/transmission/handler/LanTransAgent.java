package com.frogshealth.lan.transmission.handler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.HandlerThread;
import android.os.IBinder;

import com.frogshealth.lan.transmission.listener.FileOperateListener;
import com.frogshealth.lan.transmission.listener.UserStateListener;
import com.frogshealth.lan.transmission.model.LanUser;
import com.frogshealth.lan.transmission.net.NetUdpHelper;
import com.frogshealth.lan.transmission.service.LanTransService;

import java.util.List;

/**********************************************************************
 * 局域网传输代理类。
 *
 * @类名 LanTransAgent
 * @包名 com.frogshealth.lan.transmission.handler
 * @author yuanjf
 * @创建日期 18/8/2
 ***********************************************************************/
public class LanTransAgent {
    /**
     * 单例
     */
    private static LanTransAgent sInstance;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 消息处理
     */
    private LanMsgHandler mHandler;

    private LanTransAgent(Context context) {
        this.mContext = context;
    }

    /**
     * 获取单例
     *
     * @return 单例
     */
    public static LanTransAgent getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LanTransAgent.class) {
                if (sInstance == null) {
                    sInstance = new LanTransAgent(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * 注册监听
     *
     * @param listener 监听
     */
    public void registerFileListener(FileOperateListener listener) {
        mHandler.registerFileListener(listener);
    }

    /**
     * 反注册监听
     *
     * @param listener 监听
     */
    public void unregisterFileListener(FileOperateListener listener) {
        mHandler.unregisterFileListener(listener);
    }

    /**
     * 注册监听
     *
     * @param listener 监听
     */
    public void registerUserListener(UserStateListener listener) {
        mHandler.registerUserListener(listener);
    }

    /**
     * 反注册监听
     *
     * @param listener 监听
     */
    public void unregisterUserListener(UserStateListener listener) {
        mHandler.unregisterUserListener(listener);
    }

    /**
     * 获取局域网用户
     *
     * @return 局域网用户列表
     */
    public List<LanUser> getLanUsers() {
        return NetUdpHelper.getInstance().getLanUsers();
    }

    /**
     * 文件发送请求
     *
     * @param toAddress 发送目的地址
     */
    public void sendFileRequest(String toAddress) {
        NetUdpHelper.getInstance().sendFileRequest(toAddress);
    }

    /**
     * 接收文件
     *
     * @param toAddress 发送目的地址
     */
    public void receiveFile(String toAddress) {
        NetUdpHelper.getInstance().receiveFile(toAddress);
    }

    /**
     * 拒绝文件
     *
     * @param toAddress 发送目的地址
     */
    public void rejectFile(String toAddress) {
        NetUdpHelper.getInstance().rejectFile(toAddress);
    }

    /**
     * 绑定服务
     */
    private void bindService() {
        Intent intent = new Intent(mContext, LanTransService.class);
        mContext.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    /**
     * 初始化
     */
    public void init() {
//        bindService();
        HandlerThread thread = new HandlerThread("lan-trans");
        thread.start();
        mHandler = new LanMsgHandler(thread);
        NetUdpHelper.getInstance().init(mHandler);
    }
}