//
// Created by 袁锦凤 on 18/8/22.
//

#include "tcpserver.h"
#include "log.h"
#include "const.h"
#include "utils.h"
#include "native-lib.h"
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <strings.h>
#include <pthread.h>
#include <stdlib.h>
#include <sys/stat.h>

static int serverSocket;
static int recvProcess;

void TcpServer::initServerSocket() {
    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket < 0) {
        LOGE("socket create error");
    }
    int opt = 1;
    setsockopt(serverSocket, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

    struct sockaddr_in serverAddr;
    bzero(&serverAddr, sizeof(serverAddr));
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = htonl(INADDR_ANY);
    serverAddr.sin_port = htons(FILE_TRANS_PORT);

    if (bind(serverSocket, (struct sockaddr *) &serverAddr, sizeof(sockaddr_in))) {
        LOGE("bind error");
        close(serverSocket);
        return;
    }
    if (listen(serverSocket, 10)) {
        LOGE("server listen error");
        close(serverSocket);
        return;
    }
}

void TcpServer::startReceive(string path) {
    PSocketInfo socketInfo = (PSocketInfo) malloc(sizeof(SocketInfo));
    socketInfo->path = (char *) malloc(strlen(path.c_str()) + 1);
    strcpy(socketInfo->path, path.c_str());
    socketInfo->socket = serverSocket;
    //启动接收线程
    pthread_t id;
    pthread_attr_t attr;
    pthread_attr_init(&attr);
    pthread_attr_setdetachstate(&attr, 1);
    int ret = pthread_create(&id, &attr, recvData, socketInfo);
    if (ret < 0) {
        LOGD("create thread error...");
//        close(serverSocket);
        return;
    }
}

void *TcpServer::recvData(void *arg) {
    PSocketInfo socketInfo = (PSocketInfo) arg;
    int serverSocket = socketInfo->socket;
    string path = socketInfo->path;

    struct sockaddr_in clientAddr;
    bzero(&clientAddr, sizeof(clientAddr));
    int len = sizeof(clientAddr);
    LOGE("waiting to receive connection");
    int clientSocket = accept(serverSocket, (struct sockaddr *) &clientAddr,
                              (socklen_t *) &len);
    if (clientSocket == -1) {
        LOGE("accept error");
        return (void *) 0;
    }
    LOGE("client connect success");

    char buffer[1024];
    while (recv(clientSocket, buffer, sizeof(buffer), 0) > 0) {
        vector<string> array = Utils::split(buffer, ":");
        if (array.size() > 0 && !strcmp(array[0].c_str(), SEND_FILEINFO)) {
            LOGE("from client msg: %s", buffer);
            //向客户端发送准备好接收的消息
            string msg = READY_TO_RECEIVE;
            send(clientSocket, msg.c_str(), msg.size(), 0);
            break;
        }
    }
    vector<string> array = Utils::split(buffer, ":");
    string fileName = array[1];
    long fileSize = atoi(array[2].c_str());
    bzero(buffer, 1024);

    if (access(path.c_str(), 6) < 0) {
        mkdir(path.c_str(), 6);
    }
    char *savePath = (char *) malloc(path.size() + fileName.size() + 2);
    strcpy(savePath, path.c_str());
    strcat(savePath, "/");
    strcat(savePath, fileName.c_str());
    //释放不用的path
    free(socketInfo->path);

    FILE *fp = fopen(savePath, "wb");
    if (fp == NULL) {
        LOGE("open file failed");
//        close(serverSocket);
        return (void *) 0;
    }

    //接收到的消息存入文件
    recvProcess = 0;
    sendFileProcess(TRANS_START, recvProcess, fileName);
    int ret;
    long recvSize = 0;
    while ((ret = (int) recv(clientSocket, buffer, sizeof(buffer), 0)) > 0) {
        if (fwrite(buffer, sizeof(char), ret, fp) <= 0) {
            break;
        }
        bzero(buffer, 1024);
        recvSize += ret;
        sendFileProcess(TRANS_UPLOAD, Utils::calculateProcess(recvSize, fileSize), fileName);
    }
    fclose(fp);
    close(clientSocket);
    free(savePath);
    free(socketInfo);
    LOGE("server receive success");
    return (void *) 0;
}

void TcpServer::sendFileProcess(int state, int process, string filename) {
    if (process != recvProcess) {
        recvProcess = process;
        fileTransCallback(state, TYPE_RECEIVE, process, filename);
    }
}

