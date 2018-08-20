//
// Created by 袁锦凤 on 18/8/16.
//

#ifndef UDPDEMO_LOG_H
#define UDPDEMO_LOG_H

#include "android/log.h"

static const char *TAG = "udp";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

#endif
