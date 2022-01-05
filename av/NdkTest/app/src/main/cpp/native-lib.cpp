/*
#include <jni.h>
#include <string>
#include "test/Test1.h"
extern "C"
JNIEXPORT jstring JNICALL
Java_com_wpf_ndktest_NdkTool_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_wpf_ndktest_NdkTool_add(JNIEnv *env, jobject thiz, jint a, jint b) {
    return a+b;
}
*/
