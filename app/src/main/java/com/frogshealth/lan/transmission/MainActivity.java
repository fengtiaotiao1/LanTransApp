package com.frogshealth.lan.transmission;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.frogshealth.lan.transmission.handler.LanTransAgent;
import com.frogshealth.lan.transmission.listener.FileOperateListener;
import com.frogshealth.lan.transmission.listener.FileStatusListener;
import com.frogshealth.lan.transmission.listener.UserStateListener;
import com.frogshealth.lan.transmission.model.LanUser;
import com.frogshealth.lan.transmission.utils.Const;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**********************************************************************
 * MainActivity
 *
 * @类名 LanUser
 * @包名 com.frogshealth.lan.transmission
 * @author yuanjf
 * @创建日期 18/8/1
 ***********************************************************************/
public class MainActivity extends BaseActivity implements View.OnClickListener, UserStateListener, FileOperateListener {
    /**
     * TAG
     */
    private static final String TAG = "MainActivity";

    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 设备信息
     */
    private TextView mTvDeviceInfo;
    /**
     * 文件信息
     */
    private TextView mTvFileInfo;
    /**
     * 设备List
     */
    private List<LanUser> mLanUserList = new ArrayList<>();
    /**
     * 搜索的dialog
     */
    private AlertDialog mProgressLoadDialog;
    /**
     * 接受文件进度的dialog
     */
    private ReceiveProgressDialog mReceiveProgressDialog;
    /**
     * 用户列表适配
     */
    private LanUserAdapter mAdapter;
    /**
     * 用户选中待发送文件集合
     */
    private List<String> mListFiles;
    /**
     * 发送的布局
     */
    private LinearLayout mSendLl;
    /**
     * 接收的布局
     */
    private LinearLayout mReceiveLl;
    /**
     * 发送的TextView
     */
    private TextView mSendTv;
    /**
     * 接收的TextView
     */
    private TextView mReceiveTv;
    /**
     * 发送的ProgressBar
     */
    private ProgressBar mSendPb;
    /**
     * 接收的ProgressBar
     */
    private ProgressBar mReceivePb;
    /**
     * 权限组
     */
    private String[] mPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    /**
     * LanUser
     */
    private LanUser mlanUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        bindViews();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LanTransAgent.getInstance(this).release();
        
    }

    /**
     * 绑定view
     */
    private void bindViews() {
        mSendLl = findViewById(R.id.ll_send);
        mReceiveLl = findViewById(R.id.ll_receive);
        mSendTv = findViewById(R.id.tv_send);
        mReceiveTv = findViewById(R.id.tv_receive);
        mSendPb = findViewById(R.id.pb_send);
        mReceivePb = findViewById(R.id.pb_receive);

        setSendListener();
        setReceiveListener();

        findViewById(R.id.bt_start_discovery).setOnClickListener(this);
        findViewById(R.id.bt_find_file).setOnClickListener(this);
        findViewById(R.id.bt_send_file).setOnClickListener(this);
        mTvDeviceInfo = findViewById(R.id.tv_send_device_info);
        mTvFileInfo = findViewById(R.id.tv_send_file_info);
        ListView mLvDeviceList = findViewById(R.id.lv_device_list);
        mProgressLoadDialog = new AlertDialog.Builder(this, R.style.dialog).create();

        mAdapter = new LanUserAdapter(mContext, mLanUserList);
        mLvDeviceList.setAdapter(mAdapter);
        mLvDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String deviceInfo = mLanUserList.get(position).getUserName();
                mTvDeviceInfo.setText(deviceInfo);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int flag = -1;
        switch (requestCode) {
            case Const.PERMISSION: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            flag = 0;
                        } else {
                            flag = -1;
                            break;
                        }
                    }
                }
                if (flag == 0) {
                    LanTransAgent.getInstance(MainActivity.this).receiveFile(mlanUser.getIp());
                    LanTransAgent.getInstance(MainActivity.this).receiveFiles();
                } else {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.allow), Toast.LENGTH_LONG).show();
                    LanTransAgent.getInstance(MainActivity.this).rejectFile(mlanUser.getIp());
                }

            }
        }
    }

    /**
     * 设置接收监听
     */
    private void setReceiveListener() {
        LanTransAgent.getInstance(MainActivity.this).registerFileReceiveListener(new FileStatusListener() {
            @Override
            public void startTransmission() {
                Message msg = mHandler.obtainMessage(Const.STARTT_FOR_RECEIVE);
                mHandler.sendMessage(msg);
            }

            @Override
            public void upload(final String name, final long schedule, final long fileSize) {
                Message msg = mHandler.obtainMessage(Const.UPLOAD_FOR_RECEIVE);
                int percent = (int)(schedule *  100 / fileSize);
                msg.arg1 = percent;
                msg.obj = name;
                mHandler.sendMessage(msg);
            }

            @Override
            public void success(final String fileName) {
                Message msg = mHandler.obtainMessage(Const.SUCCESS_FOR_RECEIVE);
                msg.obj = fileName;
                mHandler.sendMessage(msg);
            }

            @Override
            public void fail(Exception e) {

            }
        });
    }


    /**
     * Handler
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DISCOVERY_DEVICE:
                    mProgressLoadDialog.dismiss();
                    break;
                case Const.STARTT_FOR_RECEIVE:
                    mReceiveLl.setVisibility(View.VISIBLE);
                    break;
                case Const.UPLOAD_FOR_RECEIVE:
                    String fileName = (String) msg.obj;
                    mReceiveTv.setText(fileName);
                    mReceivePb.setProgress(msg.arg1);
                    break;
                case Const.SUCCESS_FOR_RECEIVE:
                    mReceiveTv.setText(MainActivity.this.getString(R.string.receive_success));
                    mReceivePb.setProgress(100);
                    break;

                case Const.STARTT_FOR_SEND:
                    mSendLl.setVisibility(View.VISIBLE);
                    break;
                case Const.UPLOAD_FOR_SEND:
                    mSendTv.setText((String) msg.obj);
                    mSendPb.setProgress(msg.arg1);
                    break;
                case Const.SUCCESS_FOR_SEND:
                    mSendTv.setText(MainActivity.this.getString(R.string.send_success));
                    mSendPb.setProgress(100);
                    break;
                default:
                    break;
            }
            return false;
        }
    });


    /**
     * 设置发送监听
     */
    private void setSendListener() {

        LanTransAgent.getInstance(MainActivity.this).registerFileSendListener(new FileStatusListener() {
            @Override
            public void startTransmission() {
                Message msg = mHandler.obtainMessage(Const.STARTT_FOR_SEND);
                mHandler.sendMessage(msg);
            }

            @Override
            public void upload(final String name, final long schedule, final long fileSize) {
                Message msg = mHandler.obtainMessage(Const.UPLOAD_FOR_SEND);
                int percent = (int)(schedule *  100 / fileSize);
                msg.obj = name;
                msg.arg1 = percent;
                mHandler.sendMessage(msg);
            }

            @Override
            public void success(final String fileName) {
                Message msg = mHandler.obtainMessage(Const.SUCCESS_FOR_SEND);
                msg.obj = fileName;
                mHandler.sendMessage(msg);
            }

            @Override
            public void fail(Exception e) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start_discovery:
                startDiscovery();
                break;
            case R.id.bt_find_file:
                chooseFile();
                break;
            case R.id.bt_send_file:
                if (mTvDeviceInfo.getText().length() == 0) {
                    Toast.makeText(mContext, R.string.please_choose_device, Toast.LENGTH_SHORT).show();
                } else if (mTvFileInfo.getText().length() == 0) {
                    Toast.makeText(mContext, R.string.please_choose_file, Toast.LENGTH_SHORT).show();
                } else {
                    sendFile();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 选择文件
     */
    private void chooseFile() {
        new LFilePicker()
                .withActivity(MainActivity.this)
                .withChooseMode(true)
                .withNotFoundBooks(getString(R.string.no_select_file))
                .withStartPath(Const.PATH)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mListFiles = data.getStringArrayListExtra(Constant.RESULT_INFO);
            if (mListFiles != null) {
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.selected_file_size), mListFiles.size()), Toast.LENGTH_SHORT).show();
                mTvFileInfo.setText(mListFiles.get(0));
            }
        }
    }

    /**
     * 发送文件
     */
    private void sendFile() {
        LanTransAgent.getInstance(this).sendFileRequest(mTvDeviceInfo.getText().toString());
    }

    /**
     * 开始扫描
     */
    private void startDiscovery() {
        mProgressLoadDialog.show();
        mProgressLoadDialog.setCancelable(true);
        mProgressLoadDialog.setContentView(R.layout.progressbar_finddevice);
        mHandler.sendEmptyMessageDelayed(DISCOVERY_DEVICE, 3000);
        mLanUserList = LanTransAgent.getInstance(this).getLanUsers();
        mAdapter.setUserLst(mLanUserList);
    }

    /**
     * 接受文件
     *
     * @param lanUser 设备model
     */
    private void receiveFile(final LanUser lanUser) {
        if (lanUser == null) {
            return;
        }
        this.mlanUser = lanUser;
        AlertDialog.Builder fileReqDialog = new AlertDialog.Builder(this);
        fileReqDialog.setCancelable(false);
        fileReqDialog.setTitle(String.format(getString(R.string.receive_from), lanUser.getUserName()))
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean allPermissions = false;
                        for (int i = 0; i < mPermissions.length; i++) {
                            if (ContextCompat.checkSelfPermission(MainActivity.this, mPermissions[i]) == PackageManager.PERMISSION_GRANTED) {
                                allPermissions = true;
                            } else {
                                allPermissions = false;
                                break;
                            }
                        }
                        if (allPermissions == false) {
                            //应该请求授权
                            ActivityCompat.requestPermissions(MainActivity.this, mPermissions, Const.PERMISSION);
                        }else {
                            LanTransAgent.getInstance(MainActivity.this).receiveFile(mlanUser.getIp());
                            LanTransAgent.getInstance(MainActivity.this).receiveFiles();
                        }
                        System.out.println("XXX  1111");

                    }
                })
                .setNegativeButton(R.string.refuse, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LanTransAgent.getInstance(MainActivity.this).rejectFile(lanUser.getIp());
                    }
                })
                .show();
    }


    /**
     * 初始化监听
     */
    private void initListener() {
        LanTransAgent.getInstance(this).registerUserListener(this);
        LanTransAgent.getInstance(this).registerFileListener(this);
        LanTransAgent.getInstance(this).onlineNotify();
    }

    @Override
    public void online(final LanUser user) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.addUser(user);
            }
        });
    }

    @Override
    public void offline(final LanUser user) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.deleteUser(user);
            }
        });
    }

    @Override
    public void onSendRequest(final LanUser user) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                receiveFile(user);
            }
        });
    }

    @Override
    public void onReceive() {
        //发送文件
        List<File> list = new ArrayList<>();
        for (String mListFile : mListFiles) {
            list.add(new File(mListFile));
        }
        LanTransAgent.getInstance(MainActivity.this).sendFiles(mTvDeviceInfo.getText().toString(), list);
    }

    @Override
    public void onReject() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, getString(R.string.reject_file), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
