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
Java_com_frogshealth_lan_transcore_JavaHelper_rejectFileResp(JNIEnv *env, jobject obj,
                                                             jstring destAddr, jstring fileName);

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_receiveFileResp(JNIEnv *env, jobject obj,
                                                              jstring destAddr, jstring fileName);

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_onlineNotify(JNIEnv *env, jobject obj);

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_offlineNotify(JNIEnv *env, jobject obj);

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_sendFiles(JNIEnv *env, jobject object,
                                                        jstring destAddr, jstring path);

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_receiveFiles(JNIEnv *env, jobject object,
                                                           jstring path);

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved);
}

void notify(int cmd, string srcAddr, string msg);

void fileTransCallback(int state, int type, int process, string fileName);

#endif //UDPDEMO_NATIVE_LIB_H
