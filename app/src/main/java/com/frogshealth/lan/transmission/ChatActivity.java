package com.frogshealth.lan.transmission;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.frogshealth.lan.transmission.handler.LanTransAgent;
import com.frogshealth.lan.transmission.listener.ChatMsgListener;
import com.frogshealth.lan.transmission.utils.Const;

/**********************************************************************
 * 聊天界面。
 *
 * @类名 ChatActivity
 * @包名 com.frogshealth.lan.transmission
 * @author yuanjf
 * @创建日期 2018/8/30
 ***********************************************************************/
public class ChatActivity extends BaseActivity implements ChatMsgListener {
    /**
     * 发送消息
     */
    private Button mSendMsg;
    /**
     * 选择文件
     */
    private Button mSelectFile;
    /**
     * 用户ID
     */
    private String mUserIp;
    /**
     * 要发送的消息
     */
    private EditText mEditMsg;
    /**
     * 聊天对象
     */
    private TextView mChatUser;
    /**
     * 左聊天
     */
    private ChatMsgAdapter mLeftChatAdaper;
    /**
     * 左聊天
     */
    private ChatMsgAdapter mRightChatAdaper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mUserIp = getIntent().getStringExtra("ip");
        if (mUserIp == null) {
            return;
        }

        LanTransAgent.getInstance(this).registerChatListener(this);

        mSelectFile = findViewById(R.id.select_file);
        mSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, ChooseFileActivity.class);
                intent.putExtra("ip", mUserIp);
                startActivityForResult(intent, Const.requestCode);
            }
        });
        mSendMsg = findViewById(R.id.send_msg);
        mSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LanTransAgent.getInstance(ChatActivity.this).sendChatMsg(mUserIp, mEditMsg.getText().toString());
                mRightChatAdaper.addChatMsg("我: " + mEditMsg.getText().toString());
            }
        });
        mEditMsg = findViewById(R.id.edit_msg);
        mChatUser = findViewById(R.id.chat_user_id);
        mChatUser.setText(String.format(getString(R.string.chat_with_title), mUserIp));
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mLeftChatAdaper = new ChatMsgAdapter(this, 0);
        ListView leftChat = findViewById(R.id.chat_left);
        leftChat.setAdapter(mLeftChatAdaper);

        mRightChatAdaper = new ChatMsgAdapter(this, 1);
        ListView rightChat = findViewById(R.id.chat_right);
        rightChat.setAdapter(mRightChatAdaper);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Const.resultCode && requestCode == Const.requestCode) {
            String userIp = data.getStringExtra("ip");
            sendFile(userIp);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onChatMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLeftChatAdaper.addChatMsg(mUserIp + ": " + msg);
            }
        });
    }

    /**
     * 发送文件
     *
     * @param ip ip地址
     */
    private void sendFile(String ip) {
        LanTransAgent.getInstance(this).sendFileRequest(ip);
    }
}
