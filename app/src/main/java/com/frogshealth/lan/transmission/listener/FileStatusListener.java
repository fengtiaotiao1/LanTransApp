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
     *
     * @param flag 类型
     */
    void startTransmission(int flag);

    /**
     * 文件上传进度
     *
     * @param flag    类型
     * @param name    文件名称
     * @param percent 上传进度
     */
    void upload(int flag, String name, int percent);

    /**
     * 文件上传成功过
     *
     * @param flag     类型
     * @param fileName 文件名称
     */
    void success(int flag, String fileName);

    /**
     * 文件上传失败
     *
     * @param flag 类型
     * @param e    Exception
     */
    void fail(int flag, Exception e);


}
