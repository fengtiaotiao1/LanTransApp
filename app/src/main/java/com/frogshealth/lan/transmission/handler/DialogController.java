package com.frogshealth.lan.transmission.handler;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.Toast;

import com.frogshealth.lan.transmission.LanApplication;
import com.frogshealth.lan.transmission.ProgressDialog;
import com.frogshealth.lan.transmission.R;
import com.frogshealth.lan.transmission.listener.FileOperateListener;
import com.frogshealth.lan.transmission.model.FileInfo;
import com.frogshealth.lan.transmission.model.LanUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**********************************************************************
 * 弹出窗管理类。
 *
 * @类名 DialogController
 * @包名 com.frogshealth.lan.transmission.handler
 * @author yuanjf
 * @创建日期 2018/8/31
 ***********************************************************************/
public class DialogController implements FileOperateListener {
    /**
     * 单例
     */
    private static DialogController sInstance;
    /**
     * 上下文
     */
    private Activity mContext;
    /**
     * 传输进度界面
     */
    private ProgressDialog mDialog;

    private DialogController(Activity context) {
        this.mContext = context;
    }

    public static DialogController getInstance(Activity context) {
        if (sInstance == null) {
            synchronized (DialogController.class) {
                if (sInstance == null) {
                    sInstance = new DialogController(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     */
    public void init() {
        LanTransAgent.getInstance(mContext).registerFileListener(this);
        mDialog = new ProgressDialog(mContext);
    }

    @Override
    public void onSendRequest(final LanUser user) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showReqDialog(user);
            }
        });
    }

    @Override
    public void onReceive(String address) {
        final List<FileInfo> fileInfos = new ArrayList<>();
        //发送文件
        if (LanApplication.getAppContext().getFileInfoMap() != null && LanApplication.getAppContext().getFileInfoMap().size() > 0) {
            Map<String, FileInfo> fileInfoMap = LanApplication.getAppContext().getFileInfoMap();
            Collection<FileInfo> values = fileInfoMap.values();
            for (FileInfo fileInfo : values) {
                fileInfos.add(fileInfo);
            }
        }
        LanTransAgent.getInstance(mContext).sendFiles(address, fileInfos);
    }

    @Override
    public void onReject() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, mContext.getString(R.string.reject_file), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 接受文件请求
     *
     * @param lanUser 用户信心
     */
    private void showReqDialog(final LanUser lanUser) {
        if (lanUser == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle(String.format(mContext.getString(R.string.receive_from), lanUser.getUserName()))
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LanTransAgent.getInstance(mContext).receiveFile(lanUser.getIp());
                        LanTransAgent.getInstance(mContext).receiveFiles();
                    }
                })
                .setNegativeButton(R.string.refuse, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LanTransAgent.getInstance(mContext).rejectFile(lanUser.getIp());
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

}
