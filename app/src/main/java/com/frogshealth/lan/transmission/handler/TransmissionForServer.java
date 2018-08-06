package com.frogshealth.lan.transmission.handler;


import com.frogshealth.lan.transmission.LanApplication;
import com.frogshealth.lan.transmission.listener.FileStatusListener;
import com.frogshealth.lan.transmission.net.FileReceiver;
import com.frogshealth.lan.transmission.utils.Const;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**********************************************************************
 *
 * 接收
 *
 * @类名 TransmissionForServer
 * @包名 com.frogshealth.lan.transmission
 * @author hanchao@frogshealth.com
 * @创建日期 2018/8/3
 ***********************************************************************/
public class TransmissionForServer {
    /**
     * 开始保存
     * @param file 文件保存路径 调用LanApplication的getIndividualCacheDirectory方法获得
     */
    public void startServer(File file) {
        new Thread(new ServerRunnable(Const.DEFAULT_SERVER_PORT, file)).start();
    }

    /**
     * 服务线程
     */
    class ServerRunnable implements Runnable {
        /**
         * 端口号
         */
        private int mPort;
        /**
         * File
         */
        private File mFile;

        ServerRunnable(int port, File file) {
            this.mPort = port;
            this.mFile = file;
        }

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(this.mPort);
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();
                    FileReceiver fileReceiver = new FileReceiver(socket, mFile);
                    fileReceiver.setFileStatusListener(new FileStatusListener() {

                        @Override
                        public void startTransmission() {
                            System.out.println("XXX startTransmission");
                        }

                        @Override
                        public void upload(String fileName, long schedule, long fileSize) {
//                            System.out.println("XXX fileName = " + fileName + ", schedule = " + schedule + ", fileSize = " + fileSize);
                        }

                        @Override
                        public void success() {
                            System.out.println("XXX success");
                        }

                        @Override
                        public void fail(Exception e) {
                            System.out.println("XXX fail");
                        }
                    });
                    LanApplication.MAINEXECUTOR.execute(fileReceiver);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
