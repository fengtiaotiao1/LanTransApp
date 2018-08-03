package com.frogshealth.lan.transmission;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.frogshealth.lan.transmission.handler.LanTransAgent;
import com.frogshealth.lan.transmission.listener.UserStateListener;
import com.frogshealth.lan.transmission.model.LanUser;

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
public class MainActivity extends BaseActivity implements View.OnClickListener, UserStateListener{
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
     * 是否同意接受的dialog
     */
    private AlertDialog.Builder mReceiveDialog;
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
        mReceiveDialog = new AlertDialog.Builder(this);

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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                String path = getAbsolutePath(mContext, uri);
                Log.e(TAG, "onActivityResult: " + path);
                File file = new File(path);
                Log.e(TAG, "onActivityResult: " + file.length());
                mTvFileInfo.setText(path);
            }
        }
    }

    /**
     * 发送文件
     */
    private void sendFile() {

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
    private void receiveFile(LanUser lanUser) {
        mReceiveDialog.show();
        mReceiveDialog.setCancelable(false);
        String title = getString(R.string.receive_from) + lanUser.getUserName() + getString(R.string.from_file);
        mReceiveDialog.setTitle(title)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "onClick: " + "start accept file");

                    }
                })
                .setNegativeButton(R.string.refuse, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "onClick: " + "refuse accept file");
                    }
                });
    }

    private void startReceiveFile() {

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
     * 根据uri获取文件绝对路径
     *
     * @param context  上下文
     * @param uri uri
     * @return 绝对路径
     */
    private String getAbsolutePath(Context context, Uri uri) {
        if (context == null || uri == null) {
            return null;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 获取路径
     *
     * @param context       上下文
     * @param uri           uri
     * @param selection     select语句
     * @param selectionArgs select参数
     * @return 绝对路径
     */
    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * 判断是否是SD卡uri
     *
     * @param uri uri
     * @return 是否正确
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * 是否是下载文件
     *
     * @param uri uri
     * @return 是否正确
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * 是否为媒体文件
     *
     * @param uri uri
     * @return 是否正确
     */
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 是否为照片文件
     *
     * @param uri uri
     * @return 是否正确
     */
    private boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private void initListener() {
        LanTransAgent.getInstance(this).registerUserListener(this);
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
}
