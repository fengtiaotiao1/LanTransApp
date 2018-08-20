package com.frogshealth.lan.transmission;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**********************************************************************
 *
 *
 * @类名 CommonAdapter
 * @包名 com.frogshealth.lan.transmission
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/13
 ***********************************************************************/

/**
 *
 * @param <T> 对象集合
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    /**
     * Context
     */
    private Context mContext;
    /**
     * 集合
     */
    private List<T> mDataList;

    public CommonAdapter(Context context, List<T> dataList){
        this.mContext = context;
        this.mDataList = dataList;
    }

    public Context getContext() {
        return mContext;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    /**
     * 添加数据源
     * @param mDataList 对象集合
     */
    public void addDataList(List<T> mDataList){
        this.mDataList.addAll(mDataList);
        notifyDataSetChanged();
    }

    /**
     * 清除数据
     */
    public void clear(){
        this.mDataList.clear();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_toolbar, null);
            viewHolder = new ViewHolder();
            viewHolder.tv = (TextView) convertView.findViewById(R.id.tv_address);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        */
//        convertView = getConvertView();
        convertView = convertView(position, convertView);
        return convertView;
    }

    /**
     * 重写convertView方法
     *
     * @param position  position
     * @param convertView View
     * @return View
     */
    public abstract View convertView(int position, View convertView);

}
