package com.frogshealth.lan.transmission;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.frogshealth.lan.transmission.model.FileInfo;
import com.frogshealth.lan.transmission.utils.Const;
import com.frogshealth.lan.transmission.utils.FileUtils;

import java.util.List;

/**********************************************************************
 *
 *
 * @类名 FileInfoFragment
 * @包名 com.frogshealth.lan.transmission
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/13
 ***********************************************************************/
@SuppressLint("ValidFragment")
public class FileInfoFragment extends Fragment{
    /**
     * 当前类型
     */
    private int mType = Const.TYPE_APK;
    /**
     * GridView
     */
    private GridView mGv;
    /**
     * FileInfo集合
     */
    private List<FileInfo> mFileInfoList;
    /**
     * Adapter
     */
    private FileInfoAdapter mFileInfoAdapter;
    /**
     * 加载Layout
     */
    private RelativeLayout mLoadingLayout;

    @SuppressLint("ValidFragment")
    public FileInfoFragment(int mType) {
        this.mType = mType;
    }

    /**
     * Fragment实例
     * @param type 类型
     * @return Fragment
     */
    public static FileInfoFragment newInstance(int type) {
        FileInfoFragment fragment = new FileInfoFragment(type);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_apk, container, false);
        mGv = rootView.findViewById(R.id.gv);
        mLoadingLayout = rootView.findViewById(R.id.loading);
        init();
        return rootView;
    }
    /**
     * 初始化
     */
    private void init() {
        if(mType == Const.TYPE_APK){
            new GetFileInfoListTask(getContext(), Const.TYPE_APK).executeOnExecutor(LanApplication.MAINEXECUTOR);
        }else if(mType == Const.TYPE_JPG){
            new GetFileInfoListTask(getContext(), Const.TYPE_JPG).executeOnExecutor(LanApplication.MAINEXECUTOR);
        } else if (mType == Const.TYPE_MP3) {
            new GetFileInfoListTask(getContext(), Const.TYPE_MP3).executeOnExecutor(LanApplication.MAINEXECUTOR);
        } else if (mType == Const.TYPE_MP4) {
            new GetFileInfoListTask(getContext(), Const.TYPE_MP4).executeOnExecutor(LanApplication.MAINEXECUTOR);
        }

        mGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInfo fileInfo = mFileInfoList.get(position);
                if (LanApplication.getAppContext().isExist(fileInfo)) {
                    LanApplication.getAppContext().delFileInfo(fileInfo);
                    updateSelectedView();
                } else {
                    //1.添加任务
                    LanApplication.getAppContext().addFileInfo(fileInfo);
                    updateSelectedView();
                }
                mFileInfoAdapter.notifyDataSetChanged();
            }
        });
    }
    /**
     * 更新ChoooseActivity选中View
     */
    private void updateSelectedView(){
        if(getActivity() != null && (getActivity() instanceof ChooseFileActivity)){
            ChooseFileActivity chooseFileActivity = (ChooseFileActivity) getActivity();
            chooseFileActivity.getSelectedView();
        }
    }
    @Override
    public void onResume() {
        updateFileInfoAdapter();
        super.onResume();
    }

    /**
     * 更新FileInfoAdapter
     */
    public void updateFileInfoAdapter(){
        if(mFileInfoAdapter != null){
            mFileInfoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取ApkInfo列表任务
     */
    class GetFileInfoListTask extends AsyncTask<String, Integer, List<FileInfo>> {
        /**
         * Context
         */
        private Context mContext = null;
        /**
         * 当前类型
         */
        private int mType = Const.TYPE_APK;
        /**
         * FileInfo集合
         */
        private List<FileInfo> mFileInfoList = null;

        GetFileInfoListTask(Context sContext, int type) {
            this.mContext = sContext;
            this.mType = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List doInBackground(String... params) {
            //FileUtils.getSpecificTypeFiles 只获取FileInfo的属性 filePath与size
            if(mType == Const.TYPE_APK){
                mFileInfoList = FileUtils.getSpecificTypeFiles(mContext, new String[]{ Const.EXTEND_APK});
                mFileInfoList = FileUtils.getDetailFileInfos(mContext, mFileInfoList, Const.TYPE_APK);
            }else if(mType == Const.TYPE_JPG){
                mFileInfoList = FileUtils.getSpecificTypeFiles(mContext, new String[]{ Const.EXTEND_JPG, Const.EXTEND_JPEG});
                mFileInfoList = FileUtils.getDetailFileInfos(mContext, mFileInfoList, Const.TYPE_JPG);
            }else if(mType == Const.TYPE_MP3){
                mFileInfoList = FileUtils.getSpecificTypeFiles(mContext, new String[]{ Const.EXTEND_MP3});
                mFileInfoList = FileUtils.getDetailFileInfos(mContext, mFileInfoList, Const.TYPE_MP3);

            }else if(mType == Const.TYPE_MP4){
                mFileInfoList = FileUtils.getSpecificTypeFiles(mContext, new String[]{ Const.EXTEND_MP4});
                mFileInfoList = FileUtils.getDetailFileInfos(mContext, mFileInfoList, Const.TYPE_MP4);
            }

            FileInfoFragment.this.mFileInfoList = mFileInfoList;
            return mFileInfoList;
        }


        @Override
        protected void onPostExecute(List<FileInfo> list) {

            if (mLoadingLayout != null) {
                mLoadingLayout.setVisibility(View.GONE);
            }

            if(mFileInfoList != null && mFileInfoList.size() > 0){
                if(FileInfoFragment.this.mType == Const.TYPE_APK){ //应用
                    mFileInfoAdapter = new FileInfoAdapter(mContext, mFileInfoList, Const.TYPE_APK);
                    mGv.setAdapter(mFileInfoAdapter);
                }else if(FileInfoFragment.this.mType == Const.TYPE_JPG){ //图片
                    mFileInfoAdapter = new FileInfoAdapter(mContext, mFileInfoList, Const.TYPE_JPG);
                    mGv.setAdapter(mFileInfoAdapter);
                }else if(FileInfoFragment.this.mType == Const.TYPE_MP3){ //音乐
                    mFileInfoAdapter = new FileInfoAdapter(mContext, mFileInfoList, Const.TYPE_MP3);
                    mGv.setAdapter(mFileInfoAdapter);
                }else if(FileInfoFragment.this.mType == Const.TYPE_MP4){ //视频
                    mFileInfoAdapter = new FileInfoAdapter(mContext, mFileInfoList, Const.TYPE_MP4);
                    mGv.setAdapter(mFileInfoAdapter);
                }
            }
        }
    }
}
