package com.frogshealth.lan.transmission;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.frogshealth.lan.transmission.handler.LanTransAgent;
import com.frogshealth.lan.transmission.utils.Const;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.os.Environment.MEDIA_MOUNTED;

/**********************************************************************
 *
 * Application
 *
 * @类名 AppContext
 * @包名 com.frogshealth.lan.transmission
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/3
 ***********************************************************************/
public class LanApplication extends Application {
    /**
     * 线程池
     */
    public static final Executor MAINEXECUTOR = Executors.newFixedThreadPool(5);

    /**
     * 文件发送单线程
     */
    public static final Executor FILE_SENDER_EXECUTOR = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO 这个this是整个进程的，APP退出，进程没有杀死，会导致Handler为null。
        LanTransAgent.getInstance(this).init();
    }

}
