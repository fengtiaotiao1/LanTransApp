//
// Created by 袁锦凤 on 18/8/22.
//

#ifndef LANTRANSAPP_TCPSERVER_H
#define LANTRANSAPP_TCPSERVER_H

#include <string>

using namespace std;

class TcpServer {
public:
    static void initServerSocket();
    static void startReceive(string path);
    static void *recvData(void *arg);
    static void sendFileProcess(int state, int process, string filename);
};

#endif //LANTRANSAPP_TCPSERVER_H
