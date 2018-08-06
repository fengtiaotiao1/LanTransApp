package com.frogshealth.lan.transmission.listener;

/**********************************************************************
 *
 * 文件上传状态回调
 *
 * @类名 FileStatus
 * @包名 com.frogshealth.lan.transmission.listener
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/6
 ***********************************************************************/
public interface FileStatusListener {
    /**
     * 文件开始上传
     */
    void startTransmission();

    /**
     * 文件上传进度
     * @param schedule 上传进度
     * @param fileName 文件名称
     * @param fileSize 文件总大小
     */
    void upload(String fileName, long schedule, long fileSize);

    /**
     * 文件上传成功过
     */
    void success();

    /**
     * 文件上传失败
     * @param e Exception
     */
    void fail(Exception e);


}