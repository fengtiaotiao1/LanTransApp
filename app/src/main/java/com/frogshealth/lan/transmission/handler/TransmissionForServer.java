package com.frogshealth.lan.transmission.handler;

import android.content.Context;

import com.frogshealth.lan.transmission.LanApplication;
import com.frogshealth.lan.transmission.net.FileReceiver;
import com.frogshealth.lan.transmission.utils.Const;

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
    public void startServer(Context context) {
        new Thread(new ServerRunnable(Const.DEFAULT_SERVER_PORT, context)).start();
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
         * Context
         */
        private Context mContext;

        ServerRunnable(int port, Context context) {
            this.mPort = port;
            this.mContext = context;
        }

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(this.mPort);
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();
                    FileReceiver fileReceiver = new FileReceiver(socket, mContext);
                    LanApplication.MAINEXECUTOR.execute(fileReceiver);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
