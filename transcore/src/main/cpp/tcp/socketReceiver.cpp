/**********************************************************************
 * 
 * 
 * Author:  Ankie
 * Date:    2018/8/15
 **********************************************************************/

#include <malloc.h>
#include <pthread.h>

#include <string.h>
#include <endian.h>
#include <sys/socket.h>
#include <linux/in.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>

#include "socketReceiver.h"
#include "../const.h"
#include "../utils.h"
#include "../log.h"
#include "../native-lib.h"

using namespace std;
extern "C" {

socketReceiver::socketReceiver(string sPath) {
    // 为结构体申请空间，不需要ip
    PSocketInfo recvSocket = (PSocketInfo) malloc(sizeof(SocketInfo));
    recvSocket->sPath = (char *)malloc(std::strlen(sPath.c_str()) + 1);
    std::strcpy(recvSocket->sPath, sPath.c_str());
    // 创建线程
    if (pthread_create(&thread_id, NULL, (void *(*)(void *)) socketReceiver::serverThread,
                       (void *) recvSocket) != 0) {
        LOGE("thread create error!");
    }
}

void socketReceiver::serverThread(void *socketInfo) {
    PSocketInfo info = (PSocketInfo) socketInfo;
//    string sPath;
//    strcpy(sPath, info->sPath);
    char sPath[] = "/storage/emulated/0/download/";
    socketReceiver::initRecvSocket(sPath, FILE_TRANS_PORT);
}

void socketReceiver::initRecvSocket(string sPath, int port) {
    // socket
    int recvFd = socket(AF_INET, SOCK_STREAM, 0);
    if (recvFd < 0) {
        LOGE("recvFd is < 0: %d", recvFd);
        return;
    }

    // 定义sockaddr_in
    struct sockaddr_in serverAddr;
    memset(&serverAddr, 0, sizeof(serverAddr));
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(port);
    serverAddr.sin_addr.s_addr = htonl(INADDR_ANY);

    // 等待客户端连接，错误返回-1
    int rst = bind(recvFd, (struct sockaddr *) &serverAddr, sizeof(sockaddr_in));
    if (rst == -1) {
        LOGE("bind failed : %d", rst);
        close(recvFd);
        socketReceiver::recvFileProcess(TRANS_FAILED, 0, "wrong");
        return;
    }
    LOGE("bind server init suc!");

    // 设置监听
    int ret = listen(recvFd, 10);
    if (ret == -1) {
        LOGE("listen failed : %d", ret);
        close(recvFd);

        return;
    }
    LOGE("listen server init suc!");

    // 定义连接客户端的addr
    sockaddr_in clientAddr;
    int len = sizeof(sockaddr);

    // 开始接受文件
    LOGE("start accept! ");
    int sockConn = accept(recvFd, (struct sockaddr *) &clientAddr, (socklen_t *) &len);
    char recvBuf[1024] = {0};

    // 1. 接收文件名称和文件大小
    while (recv(sockConn, recvBuf, sizeof(recvBuf), 0) > 0) {
        if (recvBuf != NULL) {
            break;
        }
    }

    LOGE("11111111 %s", recvBuf);
    string p = strchr(recvBuf, ':');
    int tmp = (unsigned char) (strchr(recvBuf, ':') - recvBuf);
    string filename = (tmp > 0) ? strndup(recvBuf, (size_t) tmp) : strdup(recvBuf);
    LOGE("22222222 %s", filename.c_str());
    LOGE("33333333 %s", p.c_str());
    string fileSize;
    strncpy((char *) fileSize.c_str(), p.c_str() + 1, std::strlen(p.c_str()));
    LOGE("44444444 %s", fileSize.c_str());

    // 拼接文件名到路径下
    std::strcat((char *) sPath.c_str(), filename.c_str());
    LOGE("55555555 %s", sPath.c_str());

    // 2. 已经接收到文件名，文件大小，发送文件到client
    bzero(recvBuf, sizeof(recvBuf));
    strcpy(recvBuf, "START_SEND");
    LOGE("6666666 %s", recvBuf);
    send(sockConn, recvBuf, sizeof(recvBuf), 0);
    bzero(recvBuf, 1024);
    socketReceiver::recvFileProcess(TRANS_SUCCESS, 0, filename);

    // 3. 开始接收
    FILE *pFile = std::fopen(sPath.c_str(), "wb");
    int sendProcess = 0;
    int countTemp = 0;
    while (recv(sockConn, recvBuf, 1024, 0) > 0) {
        fwrite(recvBuf, sizeof(char), 1024, pFile);
        bzero(recvBuf, 1024);
        sendProcess = Utils::calculateProcess(1024 * countTemp++, std::atoi(fileSize.c_str()));
        socketReceiver::recvFileProcess(TRANS_SUCCESS, sendProcess, filename);
    }

    socketReceiver::recvFileProcess(TRANS_SUCCESS, 100, filename);

    close(recvFd);
    fclose(pFile);
    LOGE("server is over!!!!");
}

void socketReceiver::recvFileProcess(int type, int process, string filename) {
    if (TRANS_START == type) {
        // 开始传送
        fileTransCallback(TRANS_START, TYPE_RECEIVE, process, filename);
    } else if (TRANS_UPLOAD == type) {
        // 传送进度
        fileTransCallback(TRANS_UPLOAD, TYPE_RECEIVE, process, filename);
    } else if (TRANS_SUCCESS == type) {
        // 传送成功
        fileTransCallback(TRANS_SUCCESS, TYPE_RECEIVE, process, filename);
    } else if (TRANS_FAILED == type) {
        fileTransCallback(TRANS_FAILED, TYPE_RECEIVE, process, filename);
    }
}

}



