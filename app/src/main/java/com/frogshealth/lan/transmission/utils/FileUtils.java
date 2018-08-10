package com.frogshealth.lan.transmission.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;

/**********************************************************************
 * 文件工具类。
 *
 * @类名 FileUtils
 * @包名 com.frogshealth.lan.transmission.utils
 * @author yuanjf
 * @创建日期 18/8/10
 ***********************************************************************/
public class FileUtils {
    /**
     * 日志标识
     */
    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 文件保存路径
     *
     * @param context Context
     * @return File
     */
    public static File getFileSavePath(Context context) {
        File cacheDir = getCacheDirectory(context, true);
        return new File(cacheDir, Const.SAVE_PATH);
    }

    /**
     * 文件保存路径
     *
     * @param context        Context
     * @param preferExternal 外部存储
     * @return File
     */
    private static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) {
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
            Log.w(TAG, "Can't define system cache directory! '" + cacheDirPath + "%s' will be used.");
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    /**
     * 文件保存路径
     *
     * @param context Context
     * @return File
     */
    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                Log.w(TAG, "Unable to create external cache directory");
                return null;
            }
        }
        return appCacheDir;
    }
}
