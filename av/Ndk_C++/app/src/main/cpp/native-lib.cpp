#include <jni.h>
#include <string>
#include "test.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_wpf_ndk_1c_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}