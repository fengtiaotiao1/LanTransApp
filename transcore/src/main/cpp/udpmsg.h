//
// Created by 袁锦凤 on 18/8/16.
//
#ifndef UDPDEMO_UDPMSG_H
#define UDPDEMO_UDPMSG_H

using namespace std;

class UdpMsg {
public:
    void setSrcAddr(string srcAddr);

    void setDestAddr(string destAddr);

    void setPort(int port);

    void setCmd(int cmd);

    void setMsg(string msg);

    string getSrcAddr();

    string getDestAddr();

    int getPort();

    int getCmd();

    string getMsg();

    string toString();

private:
    string srcAddr;
    string destAddr;
    int port;
    int cmd;
    string msg;
};

#endif
