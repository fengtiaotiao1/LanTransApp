package com.frogshealth.lan.transmission;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.frogshealth.lan.transmission.handler.LanTransAgent;
import com.frogshealth.lan.transmission.listener.FileOperateListener;
import com.frogshealth.lan.transmission.listener.UserStateListener;
import com.frogshealth.lan.transmission.model.LanUser;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

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
     * 默认文件选择路径
     */
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/com.tencent.ma.app/log/";
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
                .withStartPath(PATH)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
            if (list != null) {
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.selected_file_size), list.size()), Toast.LENGTH_SHORT).show();
                mTvFileInfo.setText(list.get(0));
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
        AlertDialog.Builder fileReqDialog = new AlertDialog.Builder(this);
        fileReqDialog.setCancelable(false);
        fileReqDialog.setTitle(String.format(getString(R.string.receive_from), lanUser.getUserName()))
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LanTransAgent.getInstance(MainActivity.this).receiveFile(lanUser.getIp());
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
     * Handler
     */
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DISCOVERY_DEVICE:
                    mProgressLoadDialog.dismiss();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

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
