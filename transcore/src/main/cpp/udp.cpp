////
//// Created by 袁锦凤 on 18/8/15.
////
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <stdio.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <net/if.h>
#include "udp.h"
#include "log.h"
#include "udpmsg.h"
#include "const.h"
#include "utils.h"
#include "native-lib.h"

int sockfd;
string localIp;
bool isRecv = true;

void UDP::initUdp() {
    //创建socket
    if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) == -1) {
        LOGD("create socket error.");
        return;
    }
    //允许发送广播数据
    const int opt = 1;
    int ret = setsockopt(sockfd, SOL_SOCKET, SO_BROADCAST, (char *) &opt, sizeof(opt));
    if (ret < 0) {
        LOGD("set socket option error...");
        ::close(sockfd);
        return;
    }
    //启动udp接收线程
    pthread_t id;
    ret = pthread_create(&id, NULL, recvData, &sockfd);
    if (ret < 0) {
        LOGD("create thread error...");
        ::close(sockfd);
        return;
    }
    //获取本机IP
    localIp = Utils::getLocalIp(sockfd);
//    sendUdpData(CMD_TYPE_ONLINE, BROADCAST_ADDR, NULL_MSG);
}

void UDP::sendUdpData(int cmd, string destAddr, string msg) {
    UdpMsg udpMsg;
    udpMsg.setSrcAddr(localIp);
    udpMsg.setDestAddr(destAddr);
    udpMsg.setPort(PORT);
    udpMsg.setCmd(cmd);
    udpMsg.setMsg(msg);

    sockaddr_in addrto;
    bzero(&addrto, sizeof(struct sockaddr_in));
    addrto.sin_family = AF_INET;
    addrto.sin_addr.s_addr = inet_addr(udpMsg.getDestAddr().c_str());
    addrto.sin_port = htons(udpMsg.getPort());
    char sendMsg[50];
    strcpy(sendMsg, udpMsg.toString().c_str());
    LOGD("send msg is: %s", sendMsg);
    int ret = sendto(sockfd, sendMsg, strlen(sendMsg), 0, (sockaddr *) &addrto, sizeof(addrto));
    if (ret < 0) {
        LOGD("send data error....");
    }
}

void *UDP::recvData(void *arg) {
    int sockfd = *(int *) arg;
    struct sockaddr_in addrto;
    bzero(&addrto, sizeof(addrto));
    addrto.sin_family = AF_INET;
    addrto.sin_port = htons(PORT);
    addrto.sin_addr.s_addr = htonl(INADDR_ANY);

    if (bind(sockfd, (struct sockaddr *) &(addrto), sizeof(struct sockaddr_in)) == -1) {
        LOGD("bind error...");
        return (void *) false;
    }
    int len = sizeof(sockaddr_in);
    char readMsg[50] = {0};

    while (isRecv) {
        memset(readMsg, 0, sizeof(readMsg));
        int ret = recvfrom(sockfd, readMsg, sizeof(readMsg), 0, (struct sockaddr *) &addrto,
                           (socklen_t *) &len);
        string fromAddr = inet_ntoa(addrto.sin_addr);
        LOGD("receive from %s, msg is: %s", fromAddr.c_str(), readMsg);
        if (ret <= 0) {
            LOGD("read data error....");
        } else {
            if (fromAddr.compare(localIp) == 0) {
                continue;
            }
            vector<string> array = Utils::split(readMsg, ":");
            int cmd = atoi(array[array.size() - 2].c_str());
            string msg = array[array.size() - 1];
            if (cmd == CMD_TYPE_ONLINE) {
                sendUdpData(CMD_TYPE_ANSWER, inet_ntoa(addrto.sin_addr), NULL_MSG);
            }
            //发送消息通知
            sendMsgNotify(cmd, fromAddr, msg);
        }
        sleep(1);
    }

    return (void *) 0;
}

void UDP::sendMsgNotify(int cmd, string srcAddr, string msg) {
    notify(cmd, srcAddr, msg);
}
