//
// Created by anker on 2021/4/28.
//

#include <jni.h>
#include "test/CTest.h"
#include "util/JniHelpTool.h"




JNIEXPORT jstring JNICALL
Java_com_wpf_ndktest_NdkTool_stringFromJNI(JNIEnv *env, jobject instance) {

    char* content = "Hello World";
    return (*env)->NewStringUTF(env, content);
}



jint JNICALL
Java_com_wpf_ndktest_NdkTool_add(JNIEnv *env, jobject thiz, jint a, jint b) {
    int *d = getRandom();
    showRandom(d,5);
    return a+b+*(d+1);//+2
}

JNIEXPORT void JNICALL
Java_com_wpf_ndktest_NdkTool_writeFile(JNIEnv *env, jobject thiz, jstring path) {
    char* filePath = jstringToChar(env,path);
    writeFileTest(filePath);
}