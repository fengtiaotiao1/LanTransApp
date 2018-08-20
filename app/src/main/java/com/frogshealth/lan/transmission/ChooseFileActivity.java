package com.frogshealth.lan.transmission;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.frogshealth.lan.transmission.utils.Const;

/**********************************************************************
 *
 *
 * @author ankie
 * @类名 ChooseFileActivity
 * @包名 com.frogshealth.lan.transmission
 * @创建日期 2018/8/2
/**********************************************************************/

public class ChooseFileActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 当前Fragment
     */
    private FileInfoFragment mCurrentFragment;
    /**
     * Apk Fragment
     */
    private FileInfoFragment mApkInfoFragment;
    /**
     * Jpg Fragment
     */
    private FileInfoFragment mJpgInfoFragment;
    /**
     * Mp3 Fragment
     */
    private FileInfoFragment mMp3InfoFragment;
    /**
     * Mp4 Fragment
     */
    private FileInfoFragment mMp4InfoFragment;
    /**
     * ViewPager
     */
    private ViewPager mViewPager;
    /**
     * TabLayout
     */
    private TabLayout mTabLayout;
    /**
     * 选中
     */
    private Button mButton;
    /**
     * 发送
     */
    private Button mSendFile;
    /**
     * IP地址
     */
    private String mIp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_file);
        initView();
        initData();
    }

    /**
     * 初始化
     */
    private void initView() {
        Intent intent = getIntent();
        mIp = intent.getStringExtra("ip");
        findViewById(R.id.tv_layout_choose_file_back).setOnClickListener(this);
        mTabLayout = findViewById(R.id.tl_activity_tab_layout);
        mViewPager = findViewById(R.id.vp_activity_choose_file);
        mButton = findViewById(R.id.has_selected);
        mSendFile = findViewById(R.id.send_file);
        mSendFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.send_file:
                Intent intent = new Intent();
                intent.putExtra("ip", mIp);
                setResult(Const.resultCode, intent);
                finish();
                break;
            case R.id.tv_layout_choose_file_back:
                finish();
                break;

        }
    }
    /**
     * 初始化
     */
    private void initData(){
        mApkInfoFragment = FileInfoFragment.newInstance(Const.TYPE_APK);
        mJpgInfoFragment = FileInfoFragment.newInstance(Const.TYPE_JPG);
        mMp3InfoFragment = FileInfoFragment.newInstance(Const.TYPE_MP3);
        mMp4InfoFragment = FileInfoFragment.newInstance(Const.TYPE_MP4);
        mCurrentFragment = mApkInfoFragment;
        String[] titles = getResources().getStringArray(R.array.array_res);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(new ResPagerAdapter(getSupportFragmentManager(), titles));

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * 资源的PagerAdapter
     */
    class ResPagerAdapter extends FragmentPagerAdapter {
        /**
         * 标题
         */
        private String[] mTitleArray;

        ResPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        ResPagerAdapter(FragmentManager fm, String[] sTitleArray) {
            this(fm);
            this.mTitleArray = sTitleArray;
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){ //应用
                mCurrentFragment = mApkInfoFragment;
            }else if(position == 1){ //图片
                mCurrentFragment = mJpgInfoFragment;
            }else if(position == 2){ //音乐
                mCurrentFragment = mMp3InfoFragment;
            }else if(position == 3){ //视频
                mCurrentFragment = mMp4InfoFragment;
            }
            return mCurrentFragment;
        }

        @Override
        public int getCount() {
            return mTitleArray.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleArray[position];
        }
    }


    /**
     * 获取选中文件的View
     * @return
     */
    public void getSelectedView(){
        //获取SelectedView的时候 触发选择文件
        if(LanApplication.getAppContext().getFileInfoMap() != null && LanApplication.getAppContext().getFileInfoMap().size() > 0 ){
            int size = LanApplication.getAppContext().getFileInfoMap().size();
            mButton.setText(getResources().getString(R.string.str_has_selected_detail, size));
        }else{
            mButton.setText(getResources().getString(R.string.str_has_selected));
        }
    }
}
