//
// Created by 袁锦凤 on 18/8/22.
//

#include "tcpclient.h"
#include "log.h"
#include "const.h"
#include "utils.h"
#include "native-lib.h"
#include <sys/socket.h>
#include <fcntl.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <strings.h>
#include <sstream>
#include <sys/stat.h>

static int sendProcess = -1;

void TcpClient::initClientSocket(string address, string path) {
    int clientSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (clientSocket < 0) {
        LOGE("client socket create error!");
    }

//    int flags = fcntl(clientSocket, F_GETFL, 0);
//    fcntl(clientSocket, F_SETFL, flags & ~O_NONBLOCK);    //设置成阻塞模式；

    //启动接收线程:接收来自服务端的消息
    PSocketInfo socketInfo = (PSocketInfo) malloc(sizeof(SocketInfo));
    socketInfo->path = (char *) malloc(path.size() + 1);
    strcpy(socketInfo->path, path.c_str());
    socketInfo->ip = (char *) malloc(address.size() + 1);
    strcpy(socketInfo->ip, address.c_str());
    socketInfo->socket = clientSocket;
    pthread_t id;
    int ret = pthread_create(&id, NULL, recvData, socketInfo);
    if (ret < 0) {
        LOGD("create thread error...");
        ::close(clientSocket);
    }
}

void *TcpClient::recvData(void *arg) {
    PSocketInfo socketInfo = (PSocketInfo) arg;
    int clientSocket = socketInfo->socket;
    string path = socketInfo->path;
    string address = socketInfo->ip;

    struct sockaddr_in serverAddr;
    bzero(&serverAddr, sizeof(serverAddr));
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(FILE_TRANS_PORT);
    serverAddr.sin_addr.s_addr = inet_addr(address.c_str());

    LOGE("start connect server, address is : %s", address.c_str());
    int ret = connect(clientSocket, (struct sockaddr *) &serverAddr, sizeof(serverAddr));
    if (ret == -1) {
        LOGE("connect client is : %d", ret);
        close(clientSocket);
        return (void *) 0;
    }
    LOGE("client connect success");

    //获取文件名
    vector<string> array = Utils::split(path, "/\\");
    if (array.size() == 0) {
        close(clientSocket);
        return (void *) 0;
    }
    string fileName = array[array.size() - 1];
    //获取文件大小
    struct stat st;
    if (stat(path.c_str(), &st) < 0) {
        close(clientSocket);
        return (void *) 0;
    }
    long fileSize = st.st_size;

    stringstream fileInfo;
    fileInfo << SEND_FILEINFO
             << ":"
             << fileName
             << ":"
             << fileSize;

    LOGE("send fileInfo: %s", fileInfo.str().c_str());
    send(clientSocket, fileInfo.str().c_str(), fileInfo.str().size(), 0);
    //向服务端发送文件名和文件大小
    char buffer[1024];
    bzero(buffer, sizeof(buffer));
    while (recv(clientSocket, buffer, sizeof(buffer), 0) > 0) {
        LOGE("from server msg: %s", buffer);
        if (!strcmp(buffer, READY_TO_RECEIVE)) {
            break;
        }
    }
    //向服务端发送文件
    FILE *fp = fopen(path.c_str(), "rb");
    if (fp == NULL) {
        LOGE("file open failed");
        close(clientSocket);
        return (void *) 0;
    }
    bzero(buffer, sizeof(buffer));
    sendFileProcess(TRANS_START, 0, fileName);
    long sendSize = 0;
    while ((ret = (int) fread(buffer, sizeof(char), sizeof(buffer), fp)) > 0) {
        if ((send(clientSocket, buffer, ret, 0)) < 0) {
            break;
        }
        sendSize += ret;
        sendFileProcess(TRANS_UPLOAD, Utils::calculateProcess(sendSize, fileSize), fileName);
        bzero(buffer, sizeof(buffer));
    }
    fclose(fp);
    free(socketInfo->path);
    free(socketInfo->ip);
    free(socketInfo);
    close(clientSocket);
    sendFileProcess(TRANS_SUCCESS, -1, fileName);
    LOGE("client send success");

    return (void *) 0;
}

void TcpClient::sendFileProcess(int state, int process, string filename) {
    if (process != sendProcess) {
        sendProcess = process;
        fileTransCallback(state, TYPE_SEND, process, filename);
    }
}
