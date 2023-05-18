//
// Created by anker on 2023/4/26.
//

#ifndef CBASESTUDY_ANDROIDLOG_H
#define CBASESTUDY_ANDROIDLOG_H

#define TAG "WPF"
#define info(...)    __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__);
#define debug(...)   __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__);
#define error(...)   __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__);


#endif //CBASESTUDY_ANDROIDLOG_H
