package com.frogshealth.lan.transmission.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.frogshealth.lan.transmission.R;
import com.frogshealth.lan.transmission.model.FileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.os.Environment;

import static android.os.Environment.MEDIA_MOUNTED;
/**********************************************************************
 *
 *
 * @类名 FileUtils
 * @包名 com.frogshealth.lan.transmission.utils
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/13
 ***********************************************************************/
public class FileUtils {
    /**
     * 小数的格式化
     */
    public static final DecimalFormat FORMAT = new DecimalFormat("####.##");
    /**
     * 小数的格式化
     */
    public static final DecimalFormat FORMAT_ONE = new DecimalFormat("####.#");

    /**
     * 得到以后缀名结尾的所有文件集合
     *
     * @param context   Context
     * @param extension 后缀名
     * @return 得到以后缀名结尾的所有文件集合
     */
    public static List<FileInfo> getSpecificTypeFiles(Context context, String[] extension) {
        List<FileInfo> fileInfoList = new ArrayList<FileInfo>();

        //内存卡文件的Uri
        Uri fileUri = MediaStore.Files.getContentUri("external");
        //筛选列，这里只筛选了：文件路径和含后缀的文件名
        String[] projection = new String[]{MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE};

        //构造筛选条件语句
        String selection = "";
        for (int i = 0; i < extension.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'";
        }
        //按时间降序条件
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;

        Cursor cursor = context.getContentResolver().query(fileUri, projection, selection, null, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String data = cursor.getString(0);
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setPath(data);

                    long size = 0;
                    try {
                        File file = new File(data);
                        size = file.length();
                        fileInfo.setFileSize(size);
                    } catch (Exception e) {

                    }
                    fileInfoList.add(fileInfo);
                } catch (Exception e) {
                    Log.i("FileUtils", "------>>>" + e.getMessage());
                }

            }
        }
        return fileInfoList;
    }

    /**
     * 处理图片
     *
     * @param context      Context
     * @param fileInfoList List<FileInfo>
     * @param type         类型
     * @return List<FileInfo>
     */
    public static List<FileInfo> getDetailFileInfos(Context context, List<FileInfo> fileInfoList, int type) {

        if (fileInfoList == null || fileInfoList.size() <= 0) {
            return fileInfoList;
        }

        for (FileInfo fileInfo : fileInfoList) {
            if (fileInfo != null) {
                fileInfo.setFileName(getFileName(fileInfo.getPath()));
                fileInfo.setSizeDesc(getFileSize(fileInfo.getFileSize()));
                if (type == Const.TYPE_APK) {
                    Bitmap bitmap = FileUtils.drawableToBitmap(FileUtils.getApkThumbnail(context, fileInfo.getPath()));
                    if (bitmap == null) {
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                    } else {

                    }
                    fileInfo.setBitmap(bitmap);
                } else if (type == Const.TYPE_MP4) {
                    fileInfo.setBitmap(FileUtils.getScreenshotBitmap(context, fileInfo.getPath(), Const.TYPE_MP4));
                } else if (type == Const.TYPE_MP3) { //mp3不需要缩略图

                } else if (type == Const.TYPE_JPG) {//由Glide图片加载框架加载

                }
                fileInfo.setFileType(type);
            }
        }
        return fileInfoList;
    }

    /**
     * 格式化内存大小
     *
     * @param size 字节
     * @return 内存大小
     */
    public static String getFileSize(long size) {
        if (size < 0) { //小于0字节则返回0
            return "0B";
        }

        double value = 0f;
        if ((size / 1024) < 1) { //0 ` 1024 byte
            return size + "B";
        } else if ((size / (1024 * 1024)) < 1) {//0 ` 1024 kbyte

            value = size / 1024f;
            return FORMAT.format(value) + "KB";
        } else if (size / (1024 * 1024 * 1024) < 1) {                  //0 ` 1024 mbyte
            value = (size * 100 / (1024 * 1024)) / 100f;
            return FORMAT.format(value) + "MB";
        } else {                  //0 ` 1024 mbyte
            value = (size * 100L / (1024L * 1024L * 1024L)) / 100f;
            return FORMAT.format(value) + "GB";
        }
    }

    /**
     * 获取缩略图的Bitmap
     *
     * @param context  Context
     * @param filePath 文件地址
     * @param type     类型
     * @return 获取缩略图的Bitmap
     */
    public static Bitmap getScreenshotBitmap(Context context, String filePath, int type) {
        Bitmap bitmap = null;
        switch (type) {
            default: {
                break;
            }
            case Const.TYPE_APK: {
                Drawable drawable = getApkThumbnail(context, filePath);
                if (drawable != null) {
                    bitmap = drawableToBitmap(drawable);
                } else {
//                    bitmap = drawableToBitmap()
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                }
                break;
            }
            case Const.TYPE_JPG: {
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(filePath)));
                } catch (FileNotFoundException e) {
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_jpg);
                }
                bitmap = ScreenshotUtils.extractThumbnail(bitmap, 100, 100);
                break;
            }
            case Const.TYPE_MP3: {
                /*
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(filePath)));
                } catch (FileNotFoundException e) {
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_mp3);
                }
                */
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_mp3);
                bitmap = ScreenshotUtils.extractThumbnail(bitmap, 100, 100);
                break;
            }
            case Const.TYPE_MP4: {
                try {
                    bitmap = ScreenshotUtils.createVideoThumbnail(filePath);
                } catch (Exception e) {
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_mp4);
                }
                bitmap = ScreenshotUtils.extractThumbnail(bitmap, 100, 100);
            }


        }

        return bitmap;
    }

    /**
     * 得到apk的Drawable
     * @param context Context
     * @param apk_path 地址
     * @return 得到apk的Drawable
     */
    public static Drawable getApkThumbnail(Context context, String apk_path) {
        if (context == null) {
            return null;
        }

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageArchiveInfo(apk_path, PackageManager.GET_ACTIVITIES);
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            /**获取apk的图标 */
            appInfo.sourceDir = apk_path;
            appInfo.publicSourceDir = apk_path;
            if (appInfo != null) {
                Drawable apkIcon = appInfo.loadIcon(pm);
                return apkIcon;
            }
        } catch (Exception e) {

        }

        return null;
    }

    /**
     * Drawable转Bitmap
     *
     * @param drawable Drawable
     * @return Drawable转Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        //建立对应的Bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);

        return bitmap;
    }


    /**
     * 得到文件名
     * @param filePath 地址
     * @return 得到文件名
     */
    public static String getFileName(String filePath) {
        if (filePath == null || filePath.equals("")) {
            return "";
        }
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }
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


