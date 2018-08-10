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
            case Const.IS_SEND_OR_RECEIVE_START:
                receiveOrSend_Start(msg.arg1);
                break;
            case Const.IS_SEND_OR_RECEIVE_FAIL:
                receiveOrSend_Fail(msg.arg1, (Exception) msg.obj);
                break;
            case Const.IS_SEND_OR_RECEIVE_SUCCESS:
                receiveOrSend_Success(msg.arg1, (String) msg.obj);
                break;
            case Const.IS_SEND_OR_RECEIVE_UPLOAD:
                String sendString = (String) msg.obj;
                String[] parameter = sendString.split(",");
                receiveOrSend_UpLoading(msg.arg1, parameter[0], parameter[1], parameter[2]);
                break;
            default:
                break;
        }
    }

    /**
     * 文件上传中...
     *
     * @param flag    发送或者接收
     * @param name    文件名称
     * @param current 已经传输的字节数
     * @param total   文件总长度
     */
    private void receiveOrSend_UpLoading(int flag, String name, String current, String total) {
        if (Const.SEND == flag) {
            if (mFileSendListener != null) {
                mFileSendListener.upload(flag, name, Long.parseLong(current), Long.parseLong(total));
            }
        } else if (Const.RECEIVE == flag) {
            if (mFileReceiveListener != null) {
                mFileReceiveListener.upload(flag, name, Long.parseLong(current), Long.parseLong(total));
            }
        }
    }

    /**
     * 文件发送成功
     *
     * @param flag     发送或者接收
     * @param fileName 文件名称
     */
    private void receiveOrSend_Success(int flag, String fileName) {
        if (Const.SEND == flag) {
            if (mFileSendListener != null) {
                mFileSendListener.success(flag, fileName);
            }
        } else if (Const.RECEIVE == flag) {
            if (mFileReceiveListener != null) {
                mFileReceiveListener.success(flag, fileName);
            }
        }
    }

    /**
     * 文件传输出现异常
     *
     * @param flag 发送或者接收
     * @param e    Exception
     */
    private void receiveOrSend_Fail(int flag, Exception e) {
        if (Const.SEND == flag) {
            if (mFileSendListener != null) {
                mFileSendListener.fail(flag, e);
            }
        } else if (Const.RECEIVE == flag) {
            if (mFileReceiveListener != null) {
                mFileReceiveListener.fail(flag, e);
            }
        }
    }

    /**
     * 开始发送或者开始接收
     *
     * @param flag 发送或者接收
     */
    private void receiveOrSend_Start(int flag) {
        if (Const.SEND == flag) {
            if (mFileSendListener != null) {
                mFileSendListener.startTransmission(flag);
            }
        } else if (Const.RECEIVE == flag) {
            if (mFileReceiveListener != null) {
                mFileReceiveListener.startTransmission(flag);
            }
        }
    }

    /**
     * 注册文件接收状态监听
     *
     * @param statusListener 监听
     */
    public void registerFileReceiveListener(FileStatusListener statusListener) {
        this.mFileReceiveListener = statusListener;
    }

    /**
     * 注册文件发送状态接听
     *
     * @param statusListener 监听
     */
    public void registerFileSendListener(FileStatusListener statusListener) {
        this.mFileSendListener = statusListener;
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
