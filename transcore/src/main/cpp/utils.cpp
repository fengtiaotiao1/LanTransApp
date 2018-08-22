//
// Created by 袁锦凤 on 18/8/17.
//
#include <unistd.h>
#include <string.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <linux/if.h>
#include "utils.h"

vector<string> Utils::split(const string &str, const string &delim) {
    vector<string> res;
    if ("" == str) return res;
    //先将要切割的字符串从string类型转换为char*类型
    char *strs = new char[str.length() + 1];
    strcpy(strs, str.c_str());

    char *d = new char[delim.length() + 1];
    strcpy(d, delim.c_str());

    char *p = strtok(strs, d);
    while (p) {
        string s = p; //分割得到的字符串转换为string类型
        res.push_back(s); //存入结果数组
        p = strtok(NULL, d);
    }
    return res;
}

string Utils::getLocalIp(int sockfd) {
    string ip;
    struct ifconf ifconf;
    struct ifreq *ifreq;
    char buf[512];
    ifconf.ifc_len = 512;
    ifconf.ifc_buf = buf;

    int ret = ioctl(sockfd, SIOCGIFCONF, &ifconf);
    if (ret < 0) {
        return NULL;
    }
    ifreq = (struct ifreq *) ifconf.ifc_buf;
    for (int i = (ifconf.ifc_len / sizeof(struct ifreq)); i > 0; i++) {
        if (ifreq->ifr_flags == AF_INET) {
            string name = ifreq->ifr_name;
            if (name.compare("wlan0") == 0) {
                ip = inet_ntoa(((struct sockaddr_in *) &(ifreq->ifr_addr))->sin_addr);
                break;
            }
            ifreq++;
        }
    }
    return ip;
}

char *Utils::jstringToString(JNIEnv *env, jstring jStr) {
    char *ret = NULL;
    jclass classString = env->FindClass("java/lang/String");
    jstring strlenCode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(classString, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray byteArray = (jbyteArray) env->CallObjectMethod(jStr, mid, strlenCode);
    jsize alen = env->GetArrayLength(byteArray);
    jbyte *ba = env->GetByteArrayElements(byteArray, JNI_FALSE);
    if (alen > 0) {
        ret = (char *) malloc((size_t) (alen + 1));
        memset(ret, 0, strlen(ret));
        memcpy(ret, ba, alen);
        ret[alen] = 0;
    }
    env->ReleaseByteArrayElements(byteArray, ba, 0);
    return ret;
}


int Utils::calculateProcess(long processSize, long fileSize) {
    int ret = (int) (processSize * 100 / fileSize);
    if (ret >= 100) {
        return 99;
    }
    return ret;
}

// 自定义反转字节
void Utils::reverseByte(string s, int n) {
    for (int i = 0, j = n - 1; i < j; i++, j--) {
        char c = s[i];
        s[i] = s[j];
        s[j] = c;
    }
}

