package com.frogshealth.lan.transmission.handler;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.frogshealth.lan.transmission.listener.FileOperateListener;
import com.frogshealth.lan.transmission.listener.FileStatusListener;
import com.frogshealth.lan.transmission.listener.UserStateListener;
import com.frogshealth.lan.transmission.model.LanUser;
import com.frogshealth.lan.transmission.utils.Const;

import java.util.ArrayList;
import java.util.List;

/**********************************************************************
 * 局域网消息处理类。
 *
 * @类名 LanMsgHandler
 * @包名 com.frogshealth.lan.transmission.handler
 * @author yuanjf
 * @创建日期 18/8/3
 ***********************************************************************/
public class LanMsgHandler extends Handler {
    /**
     * 监听列表
     */
    private final List<FileOperateListener> mFileListeners = new ArrayList<>();
    /**
     * 监听列表
     */
    private final List<UserStateListener> mUserListeners = new ArrayList<>();
    /**
     * 文件传输状态监听
     */
    private FileStatusListener mFileReceiveListener;
    /**
     * 文件传输状态监听
     */
    private FileStatusListener mFileSendListener;

    public LanMsgHandler(HandlerThread thread) {
        super(thread.getLooper());
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Const.MSG_FILE_RECEIVE:
            case Const.MSG_FILE_REJECT:
            case Const.MSG_FILE_SEND_REQUEST:
                handleFileMsg(msg.what, (LanUser) msg.obj);
                break;
            case Const.MSG_USER_ONLINE:
            case Const.MSG_USER_OFFLINE:
                handleUserMsg(msg.what, (LanUser) msg.obj);
                break;
            case Const.STARTT_FOR_RECEIVE:
                startForReceive();
                break;
            case Const.UPLOAD_FOR_RECEIVE:
                String receiveString = (String) msg.obj;
                String[] receive = receiveString.split(",");
                upLoadingForReceive(receive[0], receive[1], receive[2]);
                break;
            case Const.SUCCESS_FOR_RECEIVE:
                String receiveFleName = (String) msg.obj;
                successForReceive(receiveFleName);
                break;
            case Const.FAIL_FOR_RECEIVE:
                Exception receiveException = (Exception) msg.obj;
                fileFailForReceive(receiveException);
                break;
            case Const.STARTT_FOR_SEND:
                startForSend();
                break;
            case Const.UPLOAD_FOR_SEND:
                String sendString = (String) msg.obj;
                String[] send = sendString.split(",");
                upLoadingForSend(send[0], send[1], send[2]);
                break;
            case Const.SUCCESS_FOR_SEND:
                String sendFileName = (String) msg.obj;
                successForSend(sendFileName);
                break;
            case Const.FAIL_FOR_SEND:
                Exception sendException = (Exception) msg.obj;
                fileFailForSend(sendException);
                break;
            default:
                break;
        }
    }

    /**
     * 文件发送失败
     * @param sendException Exception
     */
    private void fileFailForSend(Exception sendException) {
        if (mFileSendListener != null) {
            mFileSendListener.fail(sendException);
        }
    }

    /**
     * 文件发送成功
     * @param sendFileName 文件名称
     */
    private void successForSend(String sendFileName) {
        if (mFileSendListener != null) {
            mFileSendListener.success(sendFileName);
        }
    }

    /**
     * 发送文件中
     * @param name 文件名称
     * @param current 当前进度
     * @param total 总文件长度
     */
    private void upLoadingForSend(String name, String current, String total) {
        if (mFileSendListener != null) {
            mFileSendListener.upload(name, Long.parseLong(current), Long.parseLong(total));
        }
    }

    /**
     * 开始发送
     */
    private void startForSend() {
        if (mFileSendListener != null) {
            mFileSendListener.startTransmission();
        }
    }

    /**
     * 注册文件接收状态监听
     * @param statusListener 监听
     */
    public void registerFileReceiveListener(FileStatusListener statusListener) {
        this.mFileReceiveListener = statusListener;
    }

    /**
     * 注册文件发送状态接听
     * @param statusListener 监听
     */
    public void registerFileSendListener(FileStatusListener statusListener) {
        this.mFileSendListener = statusListener;
    }

    /**
     * 文件上传中...
     * @param name 文件名称
     * @param alreadyReadBytes 已经传输的字节数
     * @param totalFileSize 文件总长度
     */
    private void upLoadingForReceive(String name, String alreadyReadBytes, String totalFileSize) {
        if (mFileReceiveListener != null) {
            mFileReceiveListener.upload(name, Long.parseLong(alreadyReadBytes), Long.parseLong(totalFileSize));
        }
    }

    /**
     * 文件传输成功
     * @param fileName 文件名
     */
    private void successForReceive(String fileName) {
        if (mFileReceiveListener != null) {
            mFileReceiveListener.success(fileName);
        }

    }

    /**
     * 文件传输出现异常
     * @param e Exception
     */
    private void fileFailForReceive(Exception e) {
        if (mFileReceiveListener != null) {
            mFileReceiveListener.fail(e);
        }

    }

    /**
     * 文件开始传输
     */
    private void startForReceive() {
        if (mFileReceiveListener != null) {
            mFileReceiveListener.startTransmission();
        }
    }


    /**
     * 注册监听
     *
     * @param listener 监听
     */
    public void registerFileListener(FileOperateListener listener) {
        synchronized (mFileListeners) {
            if (!mFileListeners.contains(listener)) {
                mFileListeners.add(listener);
            }
        }
    }

    /**
     * 反注册监听
     *
     * @param listener 监听
     */
    public void unregisterFileListener(FileOperateListener listener) {
        synchronized (mFileListeners) {
            if (mFileListeners.contains(listener)) {
                mFileListeners.remove(listener);
            }
        }
    }

    /**
     * 注册监听
     *
     * @param listener 监听
     */
    public void registerUserListener(UserStateListener listener) {
        synchronized (mUserListeners) {
            if (!mUserListeners.contains(listener)) {
                mUserListeners.add(listener);
            }
        }
    }

    /**
     * 反注册监听
     *
     * @param listener 监听
     */
    public void unregisterUserListener(UserStateListener listener) {
        synchronized (mUserListeners) {
            if (mUserListeners.contains(listener)) {
                mUserListeners.remove(listener);
            }
        }
    }

    /**
     * 处理文件消息
     *
     * @param msgType 消息类型
     * @param user    用户信息
     */
    private void handleFileMsg(int msgType, LanUser user) {
        synchronized (mFileListeners) {
            for (FileOperateListener listener : mFileListeners) {
                if (listener == null) {
                    return;
                }
                switch (msgType) {
                    case Const.MSG_FILE_RECEIVE:
                        listener.onReceive();
                        break;
                    case Const.MSG_FILE_REJECT:
                        listener.onReject();
                        break;
                    case Const.MSG_FILE_SEND_REQUEST:
                        listener.onSendRequest(user);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 处理用户消息
     *
     * @param msgType 消息类型
     * @param user    用户信息
     */
    private void handleUserMsg(int msgType, LanUser user) {
        synchronized (mUserListeners) {
            for (UserStateListener listener : mUserListeners) {
                if (listener == null) {
                    return;
                }
                switch (msgType) {
                    case Const.MSG_USER_ONLINE:
                        listener.online(user);
                        break;
                    case Const.MSG_USER_OFFLINE:
                        listener.offline(user);
                        break;
                    default:
                        break;
                }
            }
        }
    }


}
