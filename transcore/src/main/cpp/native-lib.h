//
// Created by 袁锦凤 on 18/8/17.
//

#ifndef UDPDEMO_NATIVE_LIB_H
#define UDPDEMO_NATIVE_LIB_H

#include <string>
#include <jni.h>

using namespace std;

extern "C" {
JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_udpInit(JNIEnv *env, jobject obj);

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_sendFileReq(JNIEnv *env, jobject obj,
                                                          jstring destAddr, jstring fileName);
JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_rejectFile(JNIEnv *env, jobject obj,
                                                         jstring destAddr, jstring fileName);

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_receiveFile(JNIEnv *env, jobject obj,
                                                          jstring destAddr, jstring fileName);

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_onlineNotify(JNIEnv *env, jobject obj);

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_offlineNotify(JNIEnv *env, jobject obj);

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved);
}

void notify(int cmd, string srcAddr, string msg);

#endif //UDPDEMO_NATIVE_LIB_H
