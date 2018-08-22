/**********************************************************************
 * 关于socket的
 * 
 * Author:  Ankie
 * Date:    2018/8/15
 **********************************************************************/

#ifndef LANTRANSSOAPP_SOCKET_H
#define LANTRANSSOAPP_SOCKET_H


#include <jni.h>
#include <sys/types.h>
#include <string>

using namespace std;

class socketSender {
public:
    // 构造方法
    socketSender(string path, string ip);

    static void initSendSocket(string sIp, string sPath, int port);

    static void clientThread(void * info);

    static void sendFileProcess(int type, int process,  string filename);

    static string searchFileName(string sPath);

private:
    pthread_t thread_id;


};


#endif //LANTRANSSOAPP_SOCKET_H
