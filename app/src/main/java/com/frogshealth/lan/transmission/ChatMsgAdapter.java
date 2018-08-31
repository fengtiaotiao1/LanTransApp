package com.frogshealth.lan.transmission;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**********************************************************************
 * 聊天消息适配器。
 *
 * @author yuanjf
 * @类名 ChatMsgAdapter
 * @包名 com.frogshealth.lan.transmission
 * @创建日期 2018/8/31
/**********************************************************************/
public class ChatMsgAdapter extends BaseAdapter {
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 聊天list
     */
    private List<String> mChatList = new ArrayList<>();
    /**
     * 自己还是对方
     */
    private int mType;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public ChatMsgAdapter(Context context, int type) {
        this.mContext = context;
        this.mType = type;
    }

    /**
     * 增加数据源
     *
     * @param msg 消息
     */
    public void addChatMsg(String msg) {
        if (msg == null) {
            return;
        }
        mChatList.add(msg);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mChatList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);
            holder = new ViewHolder();
            holder.chatMsg = convertView.findViewById(R.id.chat_msg);
            if (mType == 1) {
                holder.chatMsg.setGravity(Gravity.RIGHT);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.chatMsg.setText(mChatList.get(position));
        return convertView;
    }

    /**
     * viewHolder
     */
    private class ViewHolder {
        TextView chatMsg;
    }
}
