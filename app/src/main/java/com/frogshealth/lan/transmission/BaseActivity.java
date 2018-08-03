package com.frogshealth.lan.transmission;

import android.support.v7.app.AppCompatActivity;

/**********************************************************************
 * BaseActivity
 *
 * @author ankie
 * @类名 BaseActivity
 * @包名 com.frogshealth.lan.transmission
 * @创建日期 2018/8/2
/**********************************************************************/

public class BaseActivity extends AppCompatActivity {
    /**
     * 搜索设备
     */
    public static final int DISCOVERY_DEVICE = 0x01;
    /**
     * 请求设备获取文件
     */
    public static final int REQUEST_GET_FILE_INFO = 0x02;

}
