//
// Created by 袁锦凤 on 18/8/22.
//

#ifndef LANTRANSAPP_TCPCLIENT_H
#define LANTRANSAPP_TCPCLIENT_H

#include <string>

using namespace std;

class TcpClient {
public:
    static void initClientSocket(string address, string path);
    static void *recvData(void *arg);
    static void sendFileProcess(int state, int process, string filename);
};

#endif //LANTRANSAPP_TCPCLIENT_H
