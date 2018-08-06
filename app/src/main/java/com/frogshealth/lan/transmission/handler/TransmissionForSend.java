package com.frogshealth.lan.transmission.handler;

import com.frogshealth.lan.transmission.model.FileInfo;
import com.frogshealth.lan.transmission.LanApplication;
import com.frogshealth.lan.transmission.net.FileSender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**********************************************************************
 *
 * 发送
 *
 * @类名 TransmissionForSend
 * @包名 com.frogshealth.lan.transmission
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/3
 ***********************************************************************/
public class TransmissionForSend {
    /**
     * 文件集合
     */
    private List<FileInfo> mFiles = new ArrayList<>();
    /**
     * 发送文件
     * @param address 地址
     * @param port 端口号
     */
    public void sendFiles(String address, int port) {
        for (FileInfo file : mFiles) {
            FileSender fileSender = new FileSender(file, address, port);
            LanApplication.FILE_SENDER_EXECUTOR.execute(fileSender);
        }
    }

    /**
     * 文件转换为FileInfo
     * @param files 文件列表
     */
    public void files2FileInfo(List<File> files) {
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            FileInfo info = new FileInfo(file.getAbsolutePath(), getFileName(file.getAbsolutePath()), file.length());
            mFiles.add(info);
        }
    }

    /**
     * 得到文件名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    public static String getFileName(String filePath) {
        if (filePath == null || filePath.equals("")) {
            return "";
        }
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

}
