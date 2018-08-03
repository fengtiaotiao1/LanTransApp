package com.frogshealth.lan.transmission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.frogshealth.lan.transmission.model.LanUser;

import java.util.ArrayList;

/**********************************************************************
 * 显示设备信息的Adapter
 *
 * @author ankie
 * @类名 LanUserAdapter
 * @包名 com.frogshealth.lan.transmission
 * @创建日期 2018/8/2
/**********************************************************************/

public class LanUserAdapter extends BaseAdapter {
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * LanUser模型list
     */
    private ArrayList<LanUser> mLanUserList = new ArrayList<>();

    /**
     * 构造方法
     *
     * @param context  上下文
     * @param lanUsers 用户List
     */
    public LanUserAdapter(Context context, ArrayList<LanUser> lanUsers) {
        this.mContext = context;
        this.mLanUserList = lanUsers;
    }

    @Override
    public int getCount() {
        return mLanUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return mLanUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item, parent, false);
            holder = new ViewHolder();
            holder.userName = convertView.findViewById(R.id.tv_device_item_name);
            holder.userIp = convertView.findViewById(R.id.tv_device_item_ip);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.userName.setText(mLanUserList.get(position).getUserName());
        holder.userIp.setText(mLanUserList.get(position).getIp());
        return convertView;
    }

    /**
     * viewHolder
     */
    private class ViewHolder {
        TextView userName;
        TextView userIp;
    }
}
