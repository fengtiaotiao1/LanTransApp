/**********************************************************************
 * 
 * 
 * Author:  Ankie
 * Date:    2018/8/15
 **********************************************************************/

#ifndef LANTRANSSOAPP_SOCKETRECVER_H
#define LANTRANSSOAPP_SOCKETRECVER_H


#include <jni.h>
#include <sys/types.h>
#include <string>

using namespace std;

class socketReceiver {
public:

    // 构造方法
    socketReceiver(string sPath);

    // 启动接收线程
    static void serverThread(void *info);

    // socket接收
    static void initRecvSocket(string sPath, int port);

    // 接收callback处理
    static void recvFileProcess(int type, int process,  string filename);

private:
    pthread_t thread_id;
};


#endif //LANTRANSSOAPP_SOCKETRECVER_H
