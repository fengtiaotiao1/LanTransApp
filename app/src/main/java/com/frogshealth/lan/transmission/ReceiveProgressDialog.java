package com.frogshealth.lan.transmission;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

/**********************************************************************
 * 文件进度的dialog
 *
 * @author ankie
 * @类名 ReceiveProgressDialog
 * @包名 com.frogshealth.lan.transmission
 * @创建日期 2018/8/3
/**********************************************************************/

public class ReceiveProgressDialog extends AlertDialog.Builder {
    /**
     * 进度
     */
    private TextView mProgressText;
    /**
     * progress
     */
    private ProgressBar mProgress;
    /**
     * dialog
     */
    private AlertDialog mDialog;
    /**
     * view
     */
    private View mView;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public ReceiveProgressDialog(@NonNull Context context) {
        super(context);
        init();
    }

    /**
     * 初始化创建dialog进度条
     */
    private void init() {
        mDialog = this.create();
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.height = dp2px(getContext());
        params.gravity = Gravity.CENTER;
        mDialog.getWindow().setAttributes(params);

        mView = LayoutInflater.from(getContext()).inflate(R.layout.progressbar_receivefile, null);
        mProgress = mView.findViewById(R.id.pb_process_receive_file);
        mProgressText = mView.findViewById(R.id.tv_text_receive_file);
        mDialog.show();
        mDialog.setContentView(mView);
        mDialog.setCancelable(true);
    }

    /**
     * 设置进度
     *
     * @param text 进度
     */
    public void setProgressText(String text) {
        mProgressText.setText(text);
    }

    /**
     * 设置progress
     *
     * @param progress 进度
     */
    public void setProgress(int progress) {
        mProgress.setProgress(progress);
    }

    /**
     * 解散dialog
     */
    public void dismiss() {
        mDialog.dismiss();
    }

    /**
     * 转换
     *
     * @param context 上下文
     * @return 高度
     */
    private int dp2px(Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((float) 130 * scale + 0.5f);
    }

}
