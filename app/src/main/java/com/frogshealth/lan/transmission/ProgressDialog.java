package com.frogshealth.lan.transmission;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.frogshealth.lan.transmission.handler.LanTransAgent;
import com.frogshealth.lan.transmission.listener.FileStatusListener;
import com.frogshealth.lan.transmission.utils.Const;
import com.frogshealth.lan.transmission.view.FileProgressView;

/**********************************************************************
 * 文件进度的dialog
 *
 * @author ankie
 * @类名 ProgressDialog
 * @包名 com.frogshealth.lan.transmission
 * @创建日期 2018/8/3
/**********************************************************************/
public class ProgressDialog extends Dialog {
    /**
     * 发送进度
     */
    private FileProgressView mSendProgress;
    /**
     * 接收进度
     */
    private FileProgressView mRecvProgress;
    /**
     * 上下文
     */
    private Context mContext;

    public ProgressDialog(@NonNull Context context) {
        super(context, R.style.cumstem_dialog);
        mContext = context;
        initView();
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    /**
     * 布局初始化
     */
    private void initView() {
        setSendListener();
        setReceiveListener();

        View view = LayoutInflater.from(getContext()).inflate(R.layout.progress_dialog, null);
        setContentView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mSendProgress = view.findViewById(R.id.send_progress);
        mRecvProgress = view.findViewById(R.id.recv_progress);
    }

    /**
     * 设置接收监听
     */
    private void setReceiveListener() {
        LanTransAgent.getInstance(mContext).registerFileReceiveListener(new FileStatusListener() {
            @Override
            public void startTransmission(int flag) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecvProgress.setVisibility(View.VISIBLE);
                        show();
                    }
                });
            }

            @Override
            public void upload(int flag, final String name, final int percent) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecvProgress.setTitleAndProgress(Const.RECEIVE, name, percent);
                    }
                });
            }

            @Override
            public void success(int flag, final String fileName) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecvProgress.setVisibility(View.GONE);
                        if (mSendProgress.getVisibility() != View.VISIBLE) {
                            dismiss();
                        }
                    }
                });
            }

            @Override
            public void fail(int flag, Exception e) {

            }
        });
    }

    /**
     * 设置发送监听
     */
    private void setSendListener() {
        LanTransAgent.getInstance(mContext).registerFileSendListener(new FileStatusListener() {
            @Override
            public void startTransmission(int flag) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSendProgress.setVisibility(View.VISIBLE);
                        show();
                    }
                });
            }

            @Override
            public void upload(int flag, final String name, final int percent) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSendProgress.setTitleAndProgress(Const.SEND, name, percent);
                    }
                });
            }

            @Override
            public void success(int flag, final String fileName) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSendProgress.setVisibility(View.GONE);
                        if (mRecvProgress.getVisibility() != View.VISIBLE) {
                            dismiss();
                        }
                    }
                });
            }

            @Override
            public void fail(int flag, Exception e) {

            }
        });
    }


}
