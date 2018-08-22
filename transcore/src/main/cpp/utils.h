//
// Created by 袁锦凤 on 18/8/17.
//

#ifndef UDPDEMO_UTILS_H
#define UDPDEMO_UTILS_H

#include <string>
#include <vector>
#include <jni.h>
#include "const.h"

using namespace std;

typedef struct stSocketInfo {
    char *sIp;
    char *sPath;
    int port;
} SocketInfo, *PSocketInfo;

class Utils {
public:
    static vector<string> split(const string &str, const string &delim);

    static string getLocalIp(int sockfd);

    static char *jstringToString(JNIEnv *env, jstring j_str);

    static int calculateProcess(long processSize, long fileSize);

    static void reverseByte(string s, int n);
};

#endif //UDPDEMO_UTILS_H
