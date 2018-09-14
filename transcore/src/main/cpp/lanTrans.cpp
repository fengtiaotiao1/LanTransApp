//
// Created by 袁锦凤 on 2018/9/13.
//

#include "lanTrans.h"
#include "udp.h"
#include "tcpclient.h"
#include "tcpserver.h"
#include "const.h"

extern "C" {
#include "db/sqlitehelp.h"
#include "db/groupdb.h"
}

void LanTrans::initSocket() {
    UDP::initUdp();
    TcpServer::initServerSocket();
}

void LanTrans::sendUdpData(int cmd, const char *destAddr, const char *msg) {
    UDP::sendUdpData(cmd, destAddr, msg);
}

void LanTrans::onlineNotify() {
    UDP::sendUdpData(CMD_TYPE_ONLINE, BROADCAST_ADDR, NULL_MSG);
}

void LanTrans::offlineNotify() {
    UDP::sendUdpData(CMD_TYPE_OFFLINE, BROADCAST_ADDR, NULL_MSG);
}

void LanTrans::sendFileReq(const char *destAddr, const char *msg) {
    UDP::sendUdpData(CMD_TYPE_SEND_FILE, destAddr, msg);
}

void LanTrans::sendFileRecvResp(const char *destAddr, const char *msg) {
    UDP::sendUdpData(CMD_TYPE_RECV_FILE, destAddr, msg);
}

void LanTrans::sendFileRejectResp(const char *destAddr, const char *msg) {
    UDP::sendUdpData(CMD_TYPE_REJECT_FILE, destAddr, msg);
}

void LanTrans::sendFile(const char *address, const char *path) {
    TcpClient::initClientSocket(address, path);
}

void LanTrans::startReceive(const char *path) {
    TcpServer::startReceive(path);
}

void LanTrans::release() {
    UDP::release();
    TcpServer::release();
}

int LanTrans::initDb(const char *dbPath) {
    return openLanTransDb(dbPath);
}

int LanTrans::addGroupMember(int groupId, const char *memberName, const char *memberIp) {
    PGroupInfo pTransInfo = (PGroupInfo) malloc(sizeof(GroupInfo));
    strcpy(pTransInfo->member_name, memberName);
    strcpy(pTransInfo->member_addr, memberIp);
    pTransInfo->group_id = groupId;

    int ret = addGroupMemberData(pTransInfo);
    free(pTransInfo);
    return ret;
}

int LanTrans::deleteGroupMember(int groupId, const char *memberName) {
    PGroupInfo pGroupInfo = (PGroupInfo) malloc(sizeof(GroupInfo));
    strcpy(pGroupInfo->member_name, memberName);
    pGroupInfo->group_id = groupId;

    int ret = deleteGroupMemberData(pGroupInfo);
    free(pGroupInfo);
    return ret;
}

int LanTrans::deleteGroupMemberByName(const char *memberName) {
    return deleteMemberDataByName(memberName);
}

int LanTrans::deleteGroupById(int groupId) {
    return deleteGroupDataById(groupId);
}

int LanTrans::deleteGroups() {
    return deleteAllGroupData();
}

int LanTrans::updateMemberIpByName(const char *memberName, const char *newIp) {
    return updateMemberDataByName(memberName, newIp);;
}

int LanTrans::queryAllGroup() {
    return 0;
}

int LanTrans::queryGroupMemberById(int groupId) {
    return 0;
}

