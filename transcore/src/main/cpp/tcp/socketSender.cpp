/**********************************************************************
 * socket相关
 * 
 * Author:  Ankie
 * Date:    2018/8/15
 **********************************************************************/
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <jni.h>
#include <assert.h>
#include <pthread.h>
#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <dirent.h>     // 文件目录相关
#include <fcntl.h>      // 文件IO相关

#include <algorithm>
#include "socketSender.h"
#include "../native-lib.h"
#include "../log.h"
#include "../utils.h"

using namespace std;

extern "C" {

socketSender::socketSender(string sPath, string sIp) {
    PSocketInfo sendSocket = (PSocketInfo) malloc(sizeof(SocketInfo));
    sendSocket->sIp = (char *) malloc(std::strlen(sIp.c_str()) + 1);
    std::strcpy(sendSocket->sIp, sIp.c_str());
    sendSocket->sPath = (char *) malloc(std::strlen(sPath.c_str()) + 1);
    std::strcpy(sendSocket->sPath, sPath.c_str());

    if (pthread_create(&thread_id, NULL, (void *(*)(void *)) socketSender::clientThread,
                       (void *) sendSocket) != 0) {
        LOGE("thread create error");
    }
}

void socketSender::clientThread(void *socketInfo) {
    PSocketInfo info = (PSocketInfo) socketInfo;
    string sIp = info->sIp;
    string sPath = info->sPath;
    LOGE("send cpp the files is : %s", sPath.c_str());
    LOGE("send cpp the ip is : %s", sIp.c_str());
    sleep(2);
    LOGE("sleep 1000 over");
    socketSender::initSendSocket(sIp, sPath, 3444);
}

void socketSender::initSendSocket(string sIp, string sPath, int port) {

    int sendfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sendfd < 0) {
        LOGE("socket send is error!");
    }
    int flags = fcntl(sendfd, F_GETFL, 0);

    fcntl(sendfd, F_SETFL, flags & ~O_NONBLOCK);    //设置成阻塞模式；

    // 定义sockaddr_in
    struct sockaddr_in clientaddr;
    memset(&clientaddr, 0, sizeof(clientaddr));
    clientaddr.sin_family = AF_INET;
    clientaddr.sin_port = htons(3444);
    clientaddr.sin_addr.s_addr = inet_addr(sIp.c_str());
    LOGE("$$$$$$$$ %s", sIp.c_str());

    // 连接服务器，错误返回-1
    LOGE("start connect server!");
    int ret = connect(sendfd, (struct sockaddr *) &clientaddr, sizeof(clientaddr));
    if (ret == -1) {
        LOGE("connect client is : %d", ret);
        close(sendfd);
        sendFileProcess(TRANS_FAILED, 0, "wrong");
        return;
    }

    // 创建文件路径
    int picfd = open(sPath.c_str(), O_RDONLY);
    if (picfd < 0) {
        LOGE("pic file open error!");
        return;
    }

    char buf[1024];
    struct stat st;
    fstat(picfd, &st);
    LOGE("file fileSize is : %d", (int) st.st_size);

    // 拼接文件名和文件大小，分隔符为":"
    string filename = socketSender::searchFileName(sPath);
    LOGE("######## %s", filename.c_str());
    off_t fileSize = st.st_size;
    char sizeString[32] = {0};
    sprintf(sizeString, "%ld", fileSize);
    LOGE("^^^^^^^^ %s", sizeString);
    char firstInfo[64] = {0};
    std::strncpy(firstInfo, filename.c_str(), std::strlen(filename.c_str()));
    strcat(firstInfo, ":");
    strcat(firstInfo, sizeString);
    LOGE("55555555 %s", firstInfo);

    // 1. 发送firstInfo到server端
    ret = (int) send(sendfd, firstInfo, sizeof(firstInfo), 0);
    LOGE("send ret %d", ret);

    // 2. 阻塞接收是否开始发送文件的命令
    while (recv(sendfd, buf, sizeof(buf), 0) > 0) {
        LOGE("???????? %s", buf);
        if (buf != NULL) {
            break;
        }
    }
    LOGE("client is wait for server send data?????");
    if (!strcmp(buf, "START_SEND")) {

        LOGE("start send file data!");
        // 3. 开始发送文件
        int sendProcess = 0;
        int countTemp = 0;
        while (1) {
            int r = (int) read(picfd, buf, 1024);
            if (0 == r) {
                LOGE("pic file send over!");
                break;
            } else if (-1 == r) {
                LOGE("pic file read err!");
                break;
            }
            bzero(buf, 1024);
            send(sendfd, buf, (size_t) r, 0);
            sendProcess = Utils::calculateProcess(1024 * countTemp++, fileSize);
            sendFileProcess(TRANS_UPLOAD, sendProcess, filename);
        }
    }

    sendFileProcess(TRANS_SUCCESS, 100, filename);

    close(sendfd);
    close(picfd);
    LOGE("send to server over! ");
}

// 文件发送进度callback
void socketSender::sendFileProcess(int type, int process, string filename) {
    if (TRANS_START == type) {
        // 开始传送
        fileTransCallback(TRANS_START, TYPE_SEND, process, filename);
    } else if (TRANS_UPLOAD == type) {
        // 传送进度
        fileTransCallback(TRANS_UPLOAD, TYPE_SEND, process, filename);
    } else if (TRANS_SUCCESS == type) {
        // 传送成功
        fileTransCallback(TRANS_SUCCESS, TYPE_SEND, process, filename);
    } else if (TRANS_FAILED == type) {
        fileTransCallback(TRANS_FAILED, TYPE_SEND, process, filename);
    }
}

// 获取文件名称
string socketSender::searchFileName(string sPath) {
    LOGE("======== %s", sPath.c_str());
    // 第一次反转字符串
    Utils::reverseByte(sPath, (int) (std::strlen(sPath.c_str())));
    LOGE("++++++++ %s", sPath.c_str());
    // 从'/'处截取用户名
    std::strchr(sPath.c_str(), '/');
    int tmp = (unsigned char) (strchr(sPath.c_str(), '/') - sPath.c_str());
    char * q = (tmp > 0) ? strndup(sPath.c_str(), (size_t) tmp) : strdup(sPath.c_str());
    LOGE("!!!!!!!! %s", q);
    // 二次反转
    Utils::reverseByte(q, (int) strlen(q));
    LOGE("@@@@@@@@ %s", q);
    return q;
}

}



