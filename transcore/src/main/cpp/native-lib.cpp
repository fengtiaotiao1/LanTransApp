#include <jni.h>
#include "udp.h"
#include "log.h"
#include "unistd.h"
#include "native-lib.h"
#include "const.h"
#include "tcpclient.h"
#include "tcpserver.h"

JavaVM *jvm = NULL;
jclass global_clazz = NULL;
jobject global_object = NULL;
jmethodID m_udp_method = NULL;
jmethodID m_file_method = NULL;


JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_udpInit(JNIEnv *env, jobject obj) {
    global_clazz = env->GetObjectClass(obj);
    if (global_clazz == NULL) {
        LOGE("global_clazz init failed");
    }
    global_object = env->NewGlobalRef(obj);
    m_udp_method = env->GetMethodID(global_clazz, "onMsgNotify",
                                    "(ILjava/lang/String;Ljava/lang/String;)V");
    m_file_method = env->GetMethodID(global_clazz, "onFileTransNotify",
                                     "(IIILjava/lang/String;)V");
    UDP::initUdp();
    TcpServer::initServerSocket();
}

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_release(JNIEnv *env, jobject obj) {
    UDP::release();
    TcpServer::release();
}

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_sendChatMsg(JNIEnv *env, jobject obj,
                                                          jstring destAddr, jstring chatMsg) {
    //发送空消息不处理
    if (destAddr == NULL || chatMsg == NULL) {
        return;
    }
    const char *dest = env->GetStringUTFChars(destAddr, (jboolean *) false);
    const char *msg = env->GetStringUTFChars(chatMsg, (jboolean *) false);
    if (dest == NULL || msg == NULL) {
        return;
    }
    UDP::sendUdpData(CMD_TYPE_SEND_CHAT_MSG, dest, msg);

    env->ReleaseStringUTFChars(destAddr, dest);
    env->ReleaseStringUTFChars(chatMsg, msg);
}

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_sendFileReq(JNIEnv *env, jobject obj,
                                                          jstring destAddr, jstring fileName) {
    if (destAddr == NULL) {
        return;
    }
    const char *dest = env->GetStringUTFChars(destAddr, (jboolean *) false);
    if (fileName == NULL) {
        fileName = env->NewStringUTF(NULL_MSG);
    }
    const char *name = env->GetStringUTFChars(fileName, (jboolean *) false);
    if (dest == NULL || fileName == NULL) {
        return;
    }
    UDP::sendUdpData(CMD_TYPE_SEND_FILE, dest, name);

    env->ReleaseStringUTFChars(destAddr, dest);
    env->ReleaseStringUTFChars(fileName, name);
}

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_rejectFileResp(JNIEnv *env, jobject obj,
                                                             jstring destAddr, jstring fileName) {
    if (destAddr == NULL) {
        return;
    }
    const char *dest = env->GetStringUTFChars(destAddr, (jboolean *) false);
    if (fileName == NULL) {
        fileName = env->NewStringUTF(NULL_MSG);
    }
    const char *name = env->GetStringUTFChars(fileName, (jboolean *) false);
    if (dest == NULL || fileName == NULL) {
        return;
    }
    UDP::sendUdpData(CMD_TYPE_REJECT_FILE, dest, name);

    env->ReleaseStringUTFChars(destAddr, dest);
    env->ReleaseStringUTFChars(fileName, name);
}

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_receiveFileResp(JNIEnv *env, jobject obj,
                                                              jstring destAddr, jstring fileName) {
    if (destAddr == NULL) {
        return;
    }
    const char *dest = env->GetStringUTFChars(destAddr, (jboolean *) false);
    if (fileName == NULL) {
        fileName = env->NewStringUTF(NULL_MSG);
    }
    const char *name = env->GetStringUTFChars(fileName, (jboolean *) false);
    if (dest == NULL || fileName == NULL) {
        return;
    }
    UDP::sendUdpData(CMD_TYPE_RECV_FILE, dest, name);

    env->ReleaseStringUTFChars(destAddr, dest);
    env->ReleaseStringUTFChars(fileName, name);
}

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_onlineNotify(JNIEnv *env, jobject obj) {

    UDP::sendUdpData(CMD_TYPE_ONLINE, BROADCAST_ADDR, NULL_MSG);

}

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_offlineNotify(JNIEnv *env, jobject obj) {

    UDP::sendUdpData(CMD_TYPE_OFFLINE, BROADCAST_ADDR, NULL_MSG);
}

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_sendFiles(JNIEnv *env, jobject object,
                                                        jstring destAddr, jstring path) {

    if (destAddr == NULL) {
        return;
    }
    const char *sIp = env->GetStringUTFChars(destAddr, (jboolean *) false);
    if (path == NULL) {
        path = env->NewStringUTF(NULL_MSG);
    }
    const char *sPath = env->GetStringUTFChars(path, (jboolean *) false);
    if (sIp == NULL || sPath == NULL) {
        return;
    }
    TcpClient::initClientSocket(sIp, sPath);
//    env->ReleaseStringUTFChars(destAddr, sIp);
//    env->ReleaseStringUTFChars(path, sPath);
}

JNIEXPORT void JNICALL
Java_com_frogshealth_lan_transcore_JavaHelper_receiveFiles(JNIEnv *env, jobject object,
                                                           jstring path) {
    if (path == NULL) {
        path = env->NewStringUTF(DEFAULT_SAVE_PATH);
    }
    const char *sPath = env->GetStringUTFChars(path, (jboolean *) false);
    LOGE("path is: %s", sPath);
    TcpServer::startReceive(sPath);
//    env->ReleaseStringUTFChars(path, sPath);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    jvm = vm;
    JNIEnv *env = NULL;
    if (jvm) {
        LOGD("jvm init success");
    } else {
        LOGD("jvm init failed");
    }
    if (jvm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}

JNIEnv *getJNIEnv(int *needDetach) {
    JNIEnv *env = NULL;
    if (jvm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        int status = jvm->AttachCurrentThread(&env, 0);
        if (status < 0) {
            LOGD("failed to attach current thread");
            return NULL;
        }
        *needDetach = 1;
    }
    return env;
}

void notify(int cmd, string srcAddr, string msg) {
    int needDetach;
    JNIEnv *env = getJNIEnv(&needDetach);
    env->CallVoidMethod(global_object, m_udp_method, cmd, env->NewStringUTF(srcAddr.c_str()),
                        env->NewStringUTF(msg.c_str()));
    jthrowable exception = env->ExceptionOccurred();
    if (exception) {
        env->ExceptionDescribe();
    }
    if (needDetach) {
        jvm->DetachCurrentThread();
    }
}

void fileTransCallback(int state, int type, int process, string fileName) {
    int needsDetach;
    JNIEnv *env = getJNIEnv(&needsDetach);

    env->CallVoidMethod(global_object, m_file_method, state, type, process,
                        env->NewStringUTF(fileName.c_str()));

    jthrowable exception = env->ExceptionOccurred();
    if (exception) {
        env->ExceptionDescribe();
    }

    if (needsDetach) {
        jvm->DetachCurrentThread();
    }
}
