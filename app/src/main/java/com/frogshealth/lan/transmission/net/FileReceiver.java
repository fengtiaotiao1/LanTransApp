package com.frogshealth.lan.transmission.net;


import com.frogshealth.lan.transmission.model.FileInfo;
import com.frogshealth.lan.transmission.utils.Const;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**********************************************************************
 *
 * 文件接收
 *
 * @类名 FileReceiver
 * @包名 com.frogshealth.lan.transmission
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/3
 ***********************************************************************/
public class FileReceiver implements Runnable {
    /**
     * Log
     */
    private static final String TAG = FileReceiver.class.getName();
    /**
     * Socket
     */
    private Socket mSocket;
    /**
     * 文件读取流
     */
    private InputStream mInputStream;

    /**
     * FileInfo对象
     */
    private FileInfo mFileInfo;
    /**
     * File
     */
    private File mFile;

    public FileReceiver(Socket mSocket, File file) {
        this.mSocket = mSocket;
        this.mFile = file;
    }

    @Override
    public void run() {
        try {
            NetTcpHelper.getInstance().startTransmission(Const.RECEIVE);
            init();
        } catch (Exception e) {
            NetTcpHelper.getInstance().failForReceiveOrSend(e, Const.RECEIVE);
        }
        try {
            parseHeader();
        } catch (IOException e) {
            NetTcpHelper.getInstance().failForReceiveOrSend(e, Const.RECEIVE);
        }
        try {
            saveFile();
        } catch (Exception e) {
            NetTcpHelper.getInstance().failForReceiveOrSend(e, Const.RECEIVE);
        }
        finish();
    }

    /**
     * 解析附加信息
     *
     * @throws IOException 异常
     */
    private void parseHeader() throws IOException {
        byte[] headerBytes = new byte[Const.BYTE_SIZE_DATA];
        int headTotal = 0;
        int readByte = -1;
        //开始读取header
        while ((readByte = mInputStream.read()) != -1) {
            headerBytes[headTotal] = (byte) readByte;
            headTotal++;
            if (headTotal == headerBytes.length) {
                break;
            }
        }
        String jsonStr = new String(headerBytes, "UTF-8");
        String[] strArray = jsonStr.split(Const.SEPARATOR);
        jsonStr = strArray[1].trim();
        mFileInfo = FileInfo.toObject(jsonStr);
    }

    /**
     * 结束
     */
    private void finish() {
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mSocket != null && mSocket.isConnected()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 保存文件
     *
     * @throws IOException 异常
     */
    private void saveFile() throws IOException {
        if (!mFile.exists()) {
            mFile.mkdirs();
        }
        File savePath = new File(mFile, mFileInfo.getFileName());
        OutputStream bos = new FileOutputStream(savePath);
        byte[] bytes = new byte[Const.BYTE_SIZE_DATA];
        int len = 0;
        long alreadyReadBytes = 0;
        long sTime = System.currentTimeMillis();
        long eTime = 0;
        while ((len = mInputStream.read(bytes)) != -1) {
            bos.write(bytes, 0, len);
            alreadyReadBytes += len;
            eTime = System.currentTimeMillis();
            if (eTime - sTime > 100) {
                sTime = eTime;
                NetTcpHelper.getInstance().upLoading(Const.RECEIVE, mFileInfo.getFileName(), alreadyReadBytes, mFileInfo.getFileSize());
            }
        }
        NetTcpHelper.getInstance().successForReceiveOrSend(mFileInfo.getFileName(), Const.RECEIVE);
        bos.close();
    }

    /**
     * 初始化
     *
     * @throws Exception 异常
     */
    private void init() throws Exception {
        if (this.mSocket != null) {
            mInputStream = this.mSocket.getInputStream();
        }
    }
}
