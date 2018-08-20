#include <sstream>
#include "udpmsg.h"

//
// Created by 袁锦凤 on 18/8/16.
//

void UdpMsg::setSrcAddr(string srcAddr) {
    this->srcAddr = srcAddr;
}

void UdpMsg::setDestAddr(string destAddr) {
    this->destAddr = destAddr;
}

void UdpMsg::setPort(int port) {
    this->port = port;
}

void UdpMsg::setCmd(int cmd) {
    this->cmd = cmd;
}

void UdpMsg::setMsg(string msg) {
    this->msg = msg;
}

string UdpMsg::getSrcAddr() {
    return srcAddr;
}

string UdpMsg::getDestAddr() {
    return destAddr;
}

int UdpMsg::getPort() {
    return port;
}

int UdpMsg::getCmd() {
    return cmd;
}

string UdpMsg::getMsg() {
    return msg;
}

string UdpMsg::toString() {
    stringstream ss;
    ss << this->srcAddr
       << ":" << this->destAddr
       << ":" << this->port
       << ":" << this->cmd
       << ":" << this->msg;
    return ss.str();
}

