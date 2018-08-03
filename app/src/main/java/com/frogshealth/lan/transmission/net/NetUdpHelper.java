package com.frogshealth.lan.transmission.net;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.frogshealth.lan.transmission.model.LanUser;
import com.frogshealth.lan.transmission.model.UdpMsgProtocol;
import com.frogshealth.lan.transmission.utils.Const;
import com.frogshealth.lan.transmission.utils.WifiUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;

/**********************************************************************
 * UDP辅助类。
 *
 * @类名 NetUdpHelper
 * @包名 com.frogshealth.lan.transmission.net
 * @author yuanjf
 * @创建日期 18/8/1
 ***********************************************************************/
public class NetUdpHelper implements Runnable {
    /**
     * 日志标识
     */
    private static final String TAG = NetUdpHelper.class.getSimpleName();
    /**
     * 单例
     */
    private static NetUdpHelper sInstance;
    /**
     * 缓冲大小
     */
    private static final int BUFFER_LENGTH = 1024;
    /**
     * 线程工作标识
     */
    private boolean mOnWork = false;
    /**
     * 接收UDP数据线程
     */
    private Thread mUdpThread = null;
    /**
     * 用于接收和发送udp数据的socket
     */
    private DatagramSocket mUdpSocket = null;
    /**
     * 用于接收的udp数据包
     */
    private DatagramPacket mUdpRecvPacket = null;
    /**
     * 接收数据的缓存
     */
    private byte[] mRecvBuffer = new byte[BUFFER_LENGTH];
    /**
     * 搜索到的局域网用户列表
     */
    private List<LanUser> mUsers = new ArrayList<>();
    /**
     * 内网IP
     */
    private String mLocalIp;
    /**
     * 处理消息
     */
    private Handler mHandler;

    private NetUdpHelper() {
        mLocalIp = WifiUtils.getLocalIpAddress();
        if (mLocalIp == null) {
            Log.i(TAG, "Get local ip failed");
            mLocalIp = "";
        }
    }

    /**
     * 获取单例
     *
     * @return 单例
     */
    public static NetUdpHelper getInstance() {
        if (sInstance == null) {
            synchronized (NetUdpHelper.class) {
                if (sInstance == null) {
                    sInstance = new NetUdpHelper();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void run() {
        while (mOnWork) {
            try {
                mUdpSocket.receive(mUdpRecvPacket);
            } catch (IOException e) {
                mOnWork = false;
                if (mUdpRecvPacket != null) {
                    mUdpRecvPacket = null;
                }
                if (mUdpSocket != null) {
                    mUdpSocket.close();
                    mUdpSocket = null;
                }
                mUdpThread = null;
                Log.e(TAG, "Receive data failed");
                break;
            }

            if (mUdpRecvPacket.getLength() == 0) {
                Log.i(TAG, "Receive data null");
                continue;
            }
            String msgStr = new String(mRecvBuffer, 0, mUdpRecvPacket.getLength());
            Log.i(TAG, "Received data: " + msgStr);
            UdpMsgProtocol msgPro = new Gson().fromJson(msgStr, UdpMsgProtocol.class);
            int commandNo = msgPro.getCommandNo();
            switch (commandNo) {
                case Const.MSG_USER_ONLINE:
                    addUser(mUdpRecvPacket.getAddress().getHostAddress());
                    UdpMsgProtocol msgProtocol = new UdpMsgProtocol(Const.MSG_ONLINE_ANSWER);
                    msgProtocol.setAddress(mLocalIp);
                    msgProtocol.setToAddress(mUdpRecvPacket.getAddress().getHostAddress());
                    msgProtocol.setToPort(mUdpRecvPacket.getPort());
                    sendUdpData(msgProtocol);
                    break;
                case Const.MSG_ONLINE_ANSWER:
                    addUser(mUdpRecvPacket.getAddress().getHostAddress());
                    break;
                case Const.MSG_USER_OFFLINE:
                    deleteUser(mUdpRecvPacket.getAddress().getHostAddress());
                    break;
                case Const.MSG_FILE_SEND_REQUEST:
                    mHandler.sendEmptyMessage(Const.MSG_FILE_SEND_REQUEST);
                    break;
                case Const.MSG_FILE_RECEIVE:
                    mHandler.sendEmptyMessage(Const.MSG_FILE_RECEIVE);
                    break;
                case Const.MSG_FILE_REJECT:
                    mHandler.sendEmptyMessage(Const.MSG_FILE_REJECT);
                    break;
                default:
                    break;
            }
            if (mUdpRecvPacket != null) {
                mUdpRecvPacket.setLength(BUFFER_LENGTH);
            }
        }

        if (mUdpRecvPacket != null) {
            mUdpRecvPacket = null;
        }
        if (mUdpSocket != null) {
            mUdpSocket.close();
            mUdpSocket = null;
        }
        mUdpThread = null;
    }

    /**
     * 获取局域网用户
     *
     * @return 局域网用户列表
     */
    public List<LanUser> getLanUsers() {
        return new ArrayList<>(mUsers);
    }

    /**
     * 文件发送请求
     *
     * @param toAddress 发送目的地址
     */
    public void sendFileRequest(String toAddress) {
        UdpMsgProtocol msg = new UdpMsgProtocol(Const.MSG_FILE_SEND_REQUEST);
        msg.setAddress(mLocalIp);
        msg.setToAddress(toAddress);
        msg.setToPort(Const.PORT);
        msg.setAdditionalSection("");
        sendUdpData(msg);
    }

    /**
     * 接收文件
     *
     * @param toAddress 发送目的地址
     */
    public void receiveFile(String toAddress) {
        UdpMsgProtocol msg = new UdpMsgProtocol(Const.MSG_FILE_RECEIVE);
        msg.setAddress(mLocalIp);
        msg.setToAddress(toAddress);
        msg.setToPort(Const.PORT);
        sendUdpData(msg);
    }

    /**
     * 拒绝文件
     *
     * @param toAddress 发送目的地址
     */
    public void rejectFile(String toAddress) {
        UdpMsgProtocol msg = new UdpMsgProtocol(Const.MSG_FILE_REJECT);
        msg.setAddress(mLocalIp);
        msg.setToAddress(toAddress);
        msg.setToPort(Const.PORT);
        sendUdpData(msg);
    }

    /**
     * 发送数据
     *
     * @param msgData 发送消息内容
     */
    private synchronized void sendUdpData(final UdpMsgProtocol msgData) {
        if (msgData == null) {
            return;
        }
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String sendData = new Gson().toJson(msgData);
                    byte[] sendBuffer = sendData.getBytes();
                    DatagramPacket udpSendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
                            InetAddress.getByName(msgData.getToAddress()), msgData.getToPort());
                    mUdpSocket.send(udpSendPacket);
                    Log.i(TAG, "成功向IP为" + msgData.getToAddress() + "发送UDP数据：" + sendData);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * 初始化
     */
    public void init(Handler handler) {
        try {
            mUdpSocket = new DatagramSocket(Const.PORT);
            mUdpRecvPacket = new DatagramPacket(mRecvBuffer, BUFFER_LENGTH);
            mOnWork = true;
            //启动线程接收UDP数据
            mUdpThread = new Thread(this);
            mUdpThread.start();
            mHandler = handler;
        } catch (SocketException e) {
            e.printStackTrace();
            this.release();
        }
//        noticeOnline();
    }

    /**
     * 资源释放
     */
    public void release() {
        noticeOffline();
        mOnWork = false;
        if (mUdpThread != null) {
            mUdpThread.interrupt();
            mUdpThread = null;
        }
    }

    /**
     * 发送上线广播
     */
    public void noticeOnline() {
        UdpMsgProtocol msg = new UdpMsgProtocol(Const.MSG_USER_ONLINE);
        msg.setAddress(mLocalIp);
        msg.setToAddress(Const.BROADCAST_ADDR);
        msg.setToPort(Const.PORT);
        sendUdpData(msg);
    }

    /**
     * 发送下线广播
     */
    private void noticeOffline() {
        UdpMsgProtocol msg = new UdpMsgProtocol(Const.MSG_USER_OFFLINE);
        msg.setAddress(mLocalIp);
        msg.setToAddress(Const.BROADCAST_ADDR);
        msg.setToPort(Const.PORT);
        sendUdpData(msg);
    }

    /**
     * 添加局域网用户
     *
     * @param name 局域网用户名称
     */
    private void addUser(String name) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        //局域网为自己则不添加
        if (name.equals(mLocalIp)) {
            return;
        }
        Iterator iterator = mUsers.iterator();
        while (iterator.hasNext()) {
            LanUser user = (LanUser) iterator.next();
            if (user.getUserName().equals(name)) {
                iterator.remove();
            }
        }
        LanUser user = new LanUser(name);
        mUsers.add(user);
        //新用户上线通知
        Message msg = mHandler.obtainMessage(Const.MSG_USER_ONLINE);
        msg.obj = user;
        mHandler.sendMessage(msg);

    }

    /**
     * 删除局域网用户
     *
     * @param name 局域网用户名称
     */
    private void deleteUser(String name) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        Iterator iterator = mUsers.iterator();
        while (iterator.hasNext()) {
            LanUser user = (LanUser) iterator.next();
            if (user.getUserName().equals(name)) {
                iterator.remove();
                //用户下线通知
                Message msg = mHandler.obtainMessage(Const.MSG_USER_OFFLINE);
                msg.obj = user;
                mHandler.sendMessage(msg);
            }
        }
    }

}
