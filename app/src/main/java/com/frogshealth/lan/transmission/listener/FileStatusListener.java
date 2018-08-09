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
     * @param name 文件名称
     * @param schedule 上传进度
     * @param fileSize 文件总大小
     */
    void upload(String name, long schedule, long fileSize);

    /**
     * 文件上传成功过
     * @param fileName 文件名称
     */
    void success(String fileName);

    /**
     * 文件上传失败
     * @param e Exception
     */
    void fail(Exception e);


}
