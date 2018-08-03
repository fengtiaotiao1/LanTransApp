package com.frogshealth.lan.transmission;

import android.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**********************************************************************
 *
 * Application
 *
 * @类名 AppContext
 * @包名 com.frogshealth.lan.transmission
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/3
 ***********************************************************************/
public class AppContext extends Application {
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
    }
}
