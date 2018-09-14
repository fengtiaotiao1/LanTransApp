//
// Created by 袁锦凤 on 2018/9/13.
//

#ifndef LANTRANSAPP_LANAPI_H
#define LANTRANSAPP_LANAPI_H


class LanTrans {

public:
    static void initSocket();
    static void sendUdpData(int cmd, const char *destAddr, const char *msg);
    static void onlineNotify();
    static void offlineNotify();
    static void sendFileReq(const char *destAddr, const char *msg);
    static void sendFileRecvResp(const char *destAddr, const char *msg);
    static void sendFileRejectResp(const char *destAddr, const char *msg);
    static void sendFile(const char *address, const char *path);
    static void startReceive(const char *path);
    static void release();

    //DB相关
    static int initDb(const char *dbPath);
    static int addGroupMember(int groupId, const char *memberName, const char *memberIp);
    static int deleteGroupMember(int groupId, const char *memberName);
    static int deleteGroupMemberByName(const char *memberName);
    static int deleteGroupById(int groupId);
    static int deleteGroups();
    static int updateMemberIpByName(const char *memberName, const char *newIp);
    static int queryAllGroup();
    static int queryGroupMemberById(int groupId);

};


#endif //LANTRANSAPP_LANAPI_H
