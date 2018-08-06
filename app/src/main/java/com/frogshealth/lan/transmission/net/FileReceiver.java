package com.frogshealth.lan.transmission.net;


import com.frogshealth.lan.transmission.listener.FileStatusListener;
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
    /**
     * 文件传输状态监听
     */
    private FileStatusListener mFileStatusListener;

    public FileReceiver(Socket mSocket, File file) {
        this.mSocket = mSocket;
        this.mFile = file;
    }

    /**
     * 设置文件传输监听
     *
     * @param statusListener 传输监听
     */
    public void setFileStatusListener(FileStatusListener statusListener) {
        this.mFileStatusListener = statusListener;
    }


    @Override
    public void run() {
        try {
            if (mFileStatusListener != null) {
                mFileStatusListener.startTransmission();
            }
            init();
        } catch (Exception e) {
            if (mFileStatusListener != null) {
                mFileStatusListener.fail(e);
            }
        }
        try {
            parseHeader();
        } catch (IOException e) {
            if (mFileStatusListener != null) {
                mFileStatusListener.fail(e);
            }
        }
        try {
            saveFile();
        } catch (Exception e) {
            if (mFileStatusListener != null) {
                mFileStatusListener.fail(e);
            }
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
        String[] strArray = jsonStr.split(Const.SPERATOR);
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
        int alreadyReadBytes = 0;
        while ((len = mInputStream.read(bytes)) != -1) {
            bos.write(bytes, 0, len);
            alreadyReadBytes += len;
            if(mFileStatusListener != null) {
                mFileStatusListener.upload(mFileInfo.getFileName(), alreadyReadBytes, mFileInfo.getFileSize());
            }
        }
        if(mFileStatusListener != null) {
            mFileStatusListener.success();
        }
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
