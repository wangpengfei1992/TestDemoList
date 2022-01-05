#include <jni.h>
#include <string>
#include "test/helloword.h"
extern "C" JNIEXPORT jstring JNICALL
Java_com_wpf_cbasestudy_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    helloword *hw = new helloword();
    hw->main();
    return env->NewStringUTF(hello.c_str());
}