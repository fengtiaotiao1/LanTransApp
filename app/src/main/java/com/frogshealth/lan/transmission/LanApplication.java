package com.frogshealth.lan.transmission;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.frogshealth.lan.transmission.handler.LanTransAgent;
import com.frogshealth.lan.transmission.model.FileInfo;
import com.frogshealth.lan.transmission.utils.Const;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
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
     * log
     */
    private final String mTAG = LanApplication.class.getName();
    /**
     * 线程池
     */
    public static final Executor MAINEXECUTOR = Executors.newFixedThreadPool(5);

    /**
     * 文件发送单线程
     */
    public static final Executor FILE_SENDER_EXECUTOR = Executors.newSingleThreadExecutor();
    /**
     * LanApplication
     */
    private static LanApplication sAppContext;
    /**
     * FileInfo集合
     */
    private Map<String, FileInfo> mFileInfoMap = new HashMap<String, FileInfo>();
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO 这个this是整个进程的，APP退出，进程没有杀死，会导致Handler为null。
        LanTransAgent.getInstance(this).init();
        this.sAppContext = this;
    }

    /**
     * 添加info对象
     * @param fileInfo FileInfo
     */
    public void addFileInfo(FileInfo fileInfo){
        if(!mFileInfoMap.containsKey(fileInfo.getPath())){
            mFileInfoMap.put(fileInfo.getPath(), fileInfo);
        }
    }
    public Map<String, FileInfo> getFileInfoMap(){
        return mFileInfoMap;
    }

    /**
     * 删除一个FileInfo
     * @param fileInfo FileInfo
     */
    public void delFileInfo(FileInfo fileInfo){
        if(mFileInfoMap.containsKey(fileInfo.getPath())){
            mFileInfoMap.remove(fileInfo.getPath());
        }
    }
    /**
     * 获取全局的AppContext
     * @return LanApplication
     */
    public static LanApplication getAppContext(){
        return sAppContext;
    }
    /**
     * 是否存在FileInfo
     * @param fileInfo FileInfo
     * @return 是否包含
     */
    public boolean isExist(FileInfo fileInfo){
        if(mFileInfoMap == null) {
            return false;
        }
        return mFileInfoMap.containsKey(fileInfo.getPath());
    }


    /**
     * 文件保存路径
     * @param context Context
     * @return File
     */
    public File getIndividualCacheDirectory(Context context) {
        File cacheDir = getCacheDirectory(context, true);
        return new File(cacheDir, Const.SAVE_PATH);
    }

    /**
     * 文件保存路径
     * @param context Context
     * @param preferExternal 外部存储
     * @return File
     */
    private  File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens
            externalStorageState = "";
        }
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            Log.w(mTAG, "Can't define system cache directory! '" + cacheDirPath + "%s' will be used.");
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    /**
     * 文件保存路径
     * @param context Context
     * @return File
     */
    private File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                Log.w(mTAG, "Unable to create external cache directory");
                return null;
            }
        }
        return appCacheDir;
    }

}
