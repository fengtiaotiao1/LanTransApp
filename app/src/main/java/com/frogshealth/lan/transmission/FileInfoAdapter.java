package com.frogshealth.lan.transmission;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.frogshealth.lan.transmission.model.FileInfo;
import com.frogshealth.lan.transmission.utils.Const;

import java.util.List;

/**********************************************************************
 *
 *
 * @类名 FileInfoAdapter
 * @包名 com.frogshealth.lan.transmission
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/13
 ***********************************************************************/
class FileInfoAdapter extends CommonAdapter<FileInfo> {
    /**
     * 文件类型的标识
     */
    private int mType = Const.TYPE_APK;

    FileInfoAdapter(Context context, List<FileInfo> dataList) {
        super(context, dataList);
    }

    FileInfoAdapter(Context context, List<FileInfo> dataList, int type) {
        super(context, dataList);
        this.mType = type;
    }

    @Override
    public View convertView(int position, View convertView) {
        FileInfo fileInfo = getDataList().get(position);

        if (mType == Const.TYPE_APK) { //APK convertView
            ApkViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_apk, null);
                viewHolder = new ApkViewHolder();
                viewHolder.mIvShortcut = convertView.findViewById(R.id.iv_shortcut);
                viewHolder.mIvOkTick = convertView.findViewById(R.id.iv_ok_tick);
                viewHolder.mTvName = convertView.findViewById(R.id.tv_name);
                viewHolder.mTvSize = convertView.findViewById(R.id.tv_size);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ApkViewHolder) convertView.getTag();
            }

            if (getDataList() != null && getDataList().get(position) != null) {
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                byte[] bytes = fileInfo.getBytes();
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                viewHolder.mIvShortcut.setImageBitmap(fileInfo.getBitmap());
                viewHolder.mTvName.setText(fileInfo.getFileName() == null ? "" : fileInfo.getFileName());
                viewHolder.mTvSize.setText(fileInfo.getSizeDesc() == null ? "" : fileInfo.getSizeDesc());

                //全局变量是否存在FileInfo
                if (LanApplication.getAppContext().isExist(fileInfo)) {
                    viewHolder.mIvOkTick.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mIvOkTick.setVisibility(View.GONE);
                }
            }
        } else if (mType == Const.TYPE_JPG) { //JPG convertView
            JpgViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_jpg, null);
                viewHolder = new JpgViewHolder();
                viewHolder.mIvOkTick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                viewHolder.mIvShortcut = (ImageView) convertView.findViewById(R.id.iv_shortcut);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (JpgViewHolder) convertView.getTag();
            }

            if (getDataList() != null && getDataList().get(position) != null) {

//                viewHolder.mIvShortcut.setImageBitmap(fileInfo.getBitmap());
                Glide
                        .with(getContext())
                        .load(fileInfo.getPath())
                        .centerCrop()
                        .placeholder(R.mipmap.icon_jpg)
                        .crossFade()
                        .into(viewHolder.mIvShortcut);

                //全局变量是否存在FileInfo
                if (LanApplication.getAppContext().isExist(fileInfo)) {
                    viewHolder.mIvOkTick.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mIvOkTick.setVisibility(View.GONE);
                }
            }
        } else if (mType == Const.TYPE_MP3) { //MP3 convertView
            Mp3ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_mp3, null);
                viewHolder = new Mp3ViewHolder();
                viewHolder.mIvOkTick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                viewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.mTvSize = (TextView) convertView.findViewById(R.id.tv_size);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (Mp3ViewHolder) convertView.getTag();
            }

            if (getDataList() != null && getDataList().get(position) != null) {
                viewHolder.mTvName.setText(fileInfo.getFileName() == null ? "" : fileInfo.getFileName());
                viewHolder.mTvSize.setText(fileInfo.getSizeDesc() == null ? "" : fileInfo.getSizeDesc());

                //全局变量是否存在FileInfo
                if (LanApplication.getAppContext().isExist(fileInfo)) {
                    viewHolder.mIvOkTick.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mIvOkTick.setVisibility(View.GONE);
                }
            }
        } else if (mType == Const.TYPE_MP4) { //MP4 convertView
            Mp4ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_mp4, null);
                viewHolder = new Mp4ViewHolder();
                viewHolder.mIvShortcut = convertView.findViewById(R.id.iv_shortcut);
                viewHolder.mIvOkTick = convertView.findViewById(R.id.iv_ok_tick);
                viewHolder.mTvName = convertView.findViewById(R.id.tv_name);
                viewHolder.mTvSize = convertView.findViewById(R.id.tv_size);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (Mp4ViewHolder) convertView.getTag();
            }

            if (getDataList() != null && getDataList().get(position) != null) {
                viewHolder.mIvShortcut.setImageBitmap(fileInfo.getBitmap());
                viewHolder.mTvName.setText(fileInfo.getFileName() == null ? "" : fileInfo.getFileName());
                viewHolder.mTvSize.setText(fileInfo.getSizeDesc() == null ? "" : fileInfo.getSizeDesc());

                //全局变量是否存在FileInfo
                if (LanApplication.getAppContext().isExist(fileInfo)) {
                    viewHolder.mIvOkTick.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mIvOkTick.setVisibility(View.GONE);
                }
            }
        }

        return convertView;
    }

    static class ApkViewHolder {
        /**
         * ImageView图片
         */
        private ImageView mIvShortcut;
        /**
         * 选中
         */
        private ImageView mIvOkTick;
        /**
         * 名称
         */
        private TextView mTvName;
        /**
         * 大小
         */
        private TextView mTvSize;
    }

    static class JpgViewHolder {
        /**
         * 头像
         */
        private ImageView mIvShortcut;
        /**
         * 选中
         */
        private ImageView mIvOkTick;
    }

    static class Mp3ViewHolder {
        /**
         * 头像
         */
        private ImageView mIvOkTick;
        /**
         * 名称
         */
        private TextView mTvName;
        /**
         * 大小
         */
        private TextView mTvSize;
    }

    static class Mp4ViewHolder {
        /**
         * 头像
         */
        private ImageView mIvShortcut;
        /**
         * 选中
         */
        private ImageView mIvOkTick;
        /**
         * 名称
         */
        private TextView mTvName;
        /**
         * 大小
         */
        private TextView mTvSize;
    }
}
