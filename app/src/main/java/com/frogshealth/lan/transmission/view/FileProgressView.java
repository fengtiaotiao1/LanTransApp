package com.frogshealth.lan.transmission.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.frogshealth.lan.transmission.R;
import com.frogshealth.lan.transmission.utils.Const;

/**********************************************************************
 * 展示文件进度的控件（接收、发送）
 *
 * @类名 FileProgressView
 * @包名 com.frogshealth.lan.transmission.view
 * @author yuanjf
 * @创建日期 2018/8/31
 ***********************************************************************/
public class FileProgressView extends LinearLayout {
    /**
     * 根目录
     */
    private View mRootView;
    /**
     * 标题
     */
    private TextView mTitleView;
    /**
     * 传输进度
     */
    private ProgressBar mProgress;

    public FileProgressView(Context context) {
        super(context);
        init();
    }

    public FileProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mRootView = View.inflate(getContext(), R.layout.file_progress_item, this);
        mTitleView = mRootView.findViewById(R.id.file_progress_title);
        mProgress = mRootView.findViewById(R.id.progress);
    }

    /**
     * 设置标题和传输进度
     *
     * @param type     类型：发送or接收
     * @param fileName 文件名
     * @param progress 传输进度
     */
    public void setTitleAndProgress(int type, String fileName, int progress) {
        String title;
        if (type == Const.SEND) {
            title = String.format(getContext().getString(R.string.send_file_progress_title), fileName);
        } else {
            title = String.format(getContext().getString(R.string.recv_file_progress_title), fileName);
        }
        mTitleView.setText(title);
        mProgress.setProgress(progress);
    }

}
