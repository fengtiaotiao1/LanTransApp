////
//// Created by 袁锦凤 on 18/8/15.
////
//
#ifndef UDPDEMO_UDP_H
#define UDPDEMO_UDP_H

#include <string>

using namespace std;

class UDP {
public:
    static void initUdp();

    static void sendUdpData(int cmd, string destAddr, string msg);

    static void *recvData(void *arg);

    static void sendMsgNotify(int cmd, string srcAddr, string msg);

};

#endif
