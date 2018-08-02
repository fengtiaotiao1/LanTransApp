package com.frogshealth.lan.transmission;

import android.util.Log;

import com.google.gson.Gson;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**********************************************************************
 * UDP辅助类。
 *
 * @类名 NetUdpHelper
 * @包名 com.frogshealth.lan.transmission
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
     * 获取单例
     *
     * @return 单例
     */
    public NetUdpHelper getInstance() {
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

    }

    /**
     * 发送上线广播
     */
    public void noticeOnline() {
        UdpMsgProtocol msg = new UdpMsgProtocol(Const.MSG_USER_ONLINE);
        try {
            InetAddress broadcastAddr = InetAddress.getByName(Const.BROADCAST_ADDR);
            sendUdpData(new Gson().toJson(msg), broadcastAddr, Const.PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送下线广播
     */
    public void noticeOffline() {
        UdpMsgProtocol msg = new UdpMsgProtocol(Const.MSG_USER_OFFLINE);
        try {
            InetAddress broadcastAddr = InetAddress.getByName(Const.BROADCAST_ADDR);
            sendUdpData(new Gson().toJson(msg), broadcastAddr, Const.PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendUdpData(String sendStr, InetAddress sendToAddr, int sendPort) {
        try {
            byte[] sendBuffer = sendStr.getBytes();
            DatagramPacket udpSendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, sendToAddr, sendPort);
            mUdpSocket.send(udpSendPacket);
            Log.i(TAG, "成功向IP为" + sendToAddr.getHostAddress() + "发送UDP数据：" + sendStr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
