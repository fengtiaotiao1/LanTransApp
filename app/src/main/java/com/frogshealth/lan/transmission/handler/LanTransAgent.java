package com.frogshealth.lan.transmission.handler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import com.frogshealth.lan.transmission.listener.ChatMsgListener;
import com.frogshealth.lan.transmission.listener.FileOperateListener;
import com.frogshealth.lan.transmission.listener.FileStatusListener;
import com.frogshealth.lan.transmission.listener.UserStateListener;
import com.frogshealth.lan.transmission.model.FileInfo;
import com.frogshealth.lan.transmission.model.LanUser;
import com.frogshealth.lan.transmission.net.NetTcpHelper;
import com.frogshealth.lan.transmission.net.NetUdpHelper;
import com.frogshealth.lan.transmission.service.LanTransService;
import com.frogshealth.lan.transmission.utils.Const;
import com.frogshealth.lan.transmission.utils.FileUtils;

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
    private static final String TAG = "LanTransAgent";
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
    /**
     * 消息处理线程
     */
    private HandlerThread mThread;

    public LanTransAgent(Context context) {
        this.mContext = context;
        mThread = new HandlerThread("lan-trans");
        mThread.start();
        mHandler = new LanMsgHandler(mThread);
    }

    /**
     * 获取单例
     *
     * @param context Context
     * @return 单例
     */
    public static LanTransAgent getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LanTransAgent.class) {
                if (sInstance == null) {
                    //TODO 默认采用so方式
                    sInstance = new LanTransImpl(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取消息处理句柄
     *
     * @return 消息处理句柄
     */
    public Handler getHandler() {
        return mHandler;
    }

    /**
     * 发送文件
     *
     * @param address 地址
     * @param files   文件列表
     */
    public void sendFiles(String address, List<FileInfo> files) {
        Log.e(TAG, "sendFiles: " + "sendFile address");
        TransmissionForSend transmissionForSend = new TransmissionForSend();
        transmissionForSend.setFileInfoList(files);
        transmissionForSend.sendFiles(address, Const.DEFAULT_SERVER_PORT);
    }

    /**
     * 开启接受文件
     */
    public void receiveFiles() {
        TransmissionForServer server = new TransmissionForServer();
        server.startServer(FileUtils.getFileSavePath(mContext));
    }

    /**
     * 设置文件接收监听
     *
     * @param statusListener 传输监听
     */
    public void registerFileReceiveListener(FileStatusListener statusListener) {
        mHandler.registerFileReceiveListener(statusListener);
    }

    /**
     * 设置文件发送监听
     *
     * @param statusListener 传输监听
     */
    public void registerFileSendListener(FileStatusListener statusListener) {
        mHandler.registerFileSendListener(statusListener);
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
     * 注册监听
     *
     * @param listener 监听
     */
    public void registerChatListener(ChatMsgListener listener) {
        mHandler.registerChatListener(listener);
    }

    /**
     * 反注册监听
     *
     * @param listener 监听
     */
    public void unregisterChatListener(ChatMsgListener listener) {
        mHandler.unregisterChatListener(listener);
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
     * 发送聊天消息
     *
     * @param address 地址
     * @param msg     消息
     */
    public void sendChatMsg(String address, String msg) {

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
        NetUdpHelper.getInstance().init(mHandler);
        NetTcpHelper.getInstance().init(mHandler);
    }

    /**
     * 资源释放
     */
    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mThread != null) {
            mThread.quit();
            mThread = null;
        }

        NetUdpHelper.getInstance().release();
        NetTcpHelper.getInstance().release();
        sInstance = null;
        mContext = null;
        System.exit(0);
    }


    /**
     * 上线通知
     */
    public void onlineNotify() {
        NetUdpHelper.getInstance().noticeOnline();
    }
}
