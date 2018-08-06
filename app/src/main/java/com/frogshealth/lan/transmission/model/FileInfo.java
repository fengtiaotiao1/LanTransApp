package com.frogshealth.lan.transmission.model;

import com.google.gson.Gson;

/**********************************************************************
 *
 * 文件实体类
 *
 * @类名 FileInfo
 * @包名 com.frogshealth.lan.entity
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/3
 ***********************************************************************/
public class FileInfo {
    /**
     * 文件路径
     */
    private String mPath;
    /**
     * 文件名称
     */
    private String mFileName;
    /**
     * 文件的长度
     */
    private long mFileSize;

    public long getFileSize() {
        return mFileSize;
    }

    public void setFileSize(long mFileSize) {
        this.mFileSize = mFileSize;
    }

    public FileInfo(String mPath, String mFileName, long mFileSize) {
        this.mPath = mPath;
        this.mFileName = mFileName;
        this.mFileSize = mFileSize;
    }

    public FileInfo() {
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    /**
     * Object2Json
     *
     * @param fileInfo FileInfo
     * @return Json
     */
    public static String toJsonStr(FileInfo fileInfo) {
        Gson gson = new Gson();
        return gson.toJson(fileInfo);
    }

    /**
     * Json2Object
     *
     * @param jsonStr Json
     * @return Object
     */
    public static FileInfo toObject(String jsonStr) {
        Gson gson = new Gson();
        FileInfo user = gson.fromJson(jsonStr, FileInfo.class);
        return user;
    }
}
