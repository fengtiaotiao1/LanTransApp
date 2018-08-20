package com.frogshealth.lan.transmission.net;


import com.frogshealth.lan.transmission.model.FileInfo;
import com.frogshealth.lan.transmission.utils.Const;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

/**********************************************************************
 *
 * 文件发送
 *
 * @类名 FileSender
 * @包名 com.frogshealth.lan.transmission
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/3
 ***********************************************************************/
public class FileSender implements Runnable {

    /**
     * log
     */
    private static final String TAG = FileSender.class.getName();
    /**
     * 发送的文件
     */
    private FileInfo mFile;
    /**
     * 服务器Socket地址
     */
    private String mServerIpAddress;
    /**
     * 服务器的端口号
     */
    private int mPort;
    /**
     * 输出流
     */
    private BufferedOutputStream mOutputStream;

    /**
     * Socket
     */
    private Socket mSocket;


    public FileSender(FileInfo mFile, String mServerIpAddress, int mPort) {
        this.mFile = mFile;
        this.mServerIpAddress = mServerIpAddress;
        this.mPort = mPort;
    }

    @Override
    public void run() {
        try {
            NetTcpHelper.getInstance().startTransmission(Const.SEND);
            initSocket();
        } catch (Exception e) {
            NetTcpHelper.getInstance().failForReceiveOrSend(e, Const.SEND);
        }
        try {
            writeFileInfo();
        } catch (UnsupportedEncodingException e) {
            NetTcpHelper.getInstance().failForReceiveOrSend(e, Const.SEND);
        } catch (IOException e) {
            NetTcpHelper.getInstance().failForReceiveOrSend(e, Const.SEND);
        }
        try {
            sendFile();
        } catch (Exception e) {
            NetTcpHelper.getInstance().failForReceiveOrSend(e, Const.SEND);
        }

        finish();
    }

    /**
     * 写入文件信息
     *
     * @throws IOException 异常
     */
    private void writeFileInfo() throws IOException {
        String jsonStr = FileInfo.toJsonStr(mFile);

        StringBuffer headerSb = new StringBuffer();

        jsonStr = Const.TYPE_FILE + Const.SEPARATOR + jsonStr;

        int length = jsonStr.getBytes(Const.UTF8).length;

        String s = length + "e";

        mOutputStream.write(s.getBytes());

        mOutputStream.write(jsonStr.getBytes(Const.UTF8));

    }

    /**
     * 结束
     */
    private void finish() {
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
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
     * 发送文件
     *
     * @throws Exception 异常
     */
    private void sendFile() throws Exception {
        InputStream fis = new FileInputStream(mFile.getPath());
        byte[] bytes = new byte[Const.BYTE_SIZE_DATA];
        int len = 0;
        long sTime = System.currentTimeMillis();
        long alreadyReadBytes = 0;
        long eTime = 0;
        while ((len = fis.read(bytes)) != -1) {
            alreadyReadBytes += len;
            eTime = System.currentTimeMillis();
            mOutputStream.write(bytes, 0, len);
            if (eTime - sTime > 100) {
                sTime = eTime;
                NetTcpHelper.getInstance().upLoading(Const.SEND, mFile.getFileName(), alreadyReadBytes, mFile.getFileSize());
            }
        }
        mOutputStream.flush();
        NetTcpHelper.getInstance().successForReceiveOrSend(mFile.getFileName(), Const.SEND);
    }

    /**
     * 初始化Socket
     *
     * @throws Exception 异常
     */
    private void initSocket() throws Exception {
        mSocket = new Socket(mServerIpAddress, mPort);
        OutputStream os = mSocket.getOutputStream();
        mOutputStream = new BufferedOutputStream(os);
    }
}
