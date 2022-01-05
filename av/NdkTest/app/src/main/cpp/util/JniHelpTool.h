//
// Created by anker on 2021/4/29.
//

#ifndef NDKTEST_JNITOOL_H
#define NDKTEST_JNITOOL_H


#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

jstring charTojstring(JNIEnv* env, const char* pat);

char* jstringToChar(JNIEnv* env, jstring jstr);

#ifdef __cplusplus
}
#endif


#endif //NDKTEST_JNITOOL_H




