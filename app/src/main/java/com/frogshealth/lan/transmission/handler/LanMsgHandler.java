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
    private FileStatusListener mFileStatusListener;

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
            case Const.STARTT_RANSMISSION:
                startTransmission();
                break;
            case Const.UPLOAD:
                String s = (String) msg.obj;
                String[] split = s.split(",");
                upLoading(split[0], split[1]);
                break;
            case Const.SUCCESS:
                String fileName = (String) msg.obj;
                success(fileName);
                break;
            case Const.FAIL:
                Exception e = (Exception) msg.obj;
                fileFail(e);
                break;
            default:
                break;
        }
    }

    /**
     * 注册文件传输状态监听
     * @param statusListener 监听
     */
    public void registerFileStatusListener(FileStatusListener statusListener) {
        this.mFileStatusListener = statusListener;
    }

    /**
     * 文件上传中...
     * @param alreadyReadBytes 已经传输的字节数
     * @param totalFileSize 文件总长度
     */
    private void upLoading(String alreadyReadBytes, String totalFileSize) {
        if (mFileStatusListener != null) {
            mFileStatusListener.upload(Long.parseLong(alreadyReadBytes), Long.parseLong(totalFileSize));
        }
    }

    /**
     * 文件传输成功
     * @param fileName 文件名
     */
    private void success(String fileName) {
        if (mFileStatusListener != null) {
            mFileStatusListener.success(fileName);
        }

    }

    /**
     * 文件传输出现异常
     * @param e Exception
     */
    private void fileFail(Exception e) {
        if (mFileStatusListener != null) {
            mFileStatusListener.fail(e);
        }

    }

    /**
     * 文件开始传输
     */
    private void startTransmission() {
        if (mFileStatusListener != null) {
            mFileStatusListener.startTransmission();
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
