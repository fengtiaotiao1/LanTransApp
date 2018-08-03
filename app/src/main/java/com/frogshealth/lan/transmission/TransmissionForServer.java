package com.frogshealth.lan.transmission;

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
     * 开启服务
     */
    public void startServer() {
        new Thread(new ServerRunnable(Const.DEFAULT_SERVER_PORT)).start();
    }

    /**
     * 服务线程
     */
    class ServerRunnable implements Runnable {
        /**
         * 端口号
         */
        private int mPort;

        ServerRunnable(int port) {
            this.mPort = port;
        }

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(this.mPort);
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();
                    FileReceiver fileReceiver = new FileReceiver(socket);
                    AppContext.MAINEXECUTOR.execute(fileReceiver);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
