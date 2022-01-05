#include <jni.h>
#include <string>
#include <string>
#include <malloc.h>
#include "vad_src/webrtc_vad.h"
#include "vad_src/vad_core.h"


extern "C" JNIEXPORT jstring JNICALL
Java_com_wpf_webrtcvaddemo_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}extern "C"
JNIEXPORT jboolean JNICALL
Java_com_wpf_webrtcvaddemo_MainActivity_webRtcVad_1Process(JNIEnv *env, jobject instance,
                                                           jshortArray audioData_,
                                                           jint offsetInshort,
                                                           jint readSize) {
    // demo for vad 。 add by tanyaping welcome contact
    VadInst *handle = WebRtcVad_Create();
    WebRtcVad_Init(handle);
    WebRtcVad_set_mode(handle, 2);
    int index = readSize / 160;
    jshort *pcm_data = env->GetShortArrayElements(audioData_, JNI_FALSE);
    bool b = JNI_FALSE;
    for (int i = 0; i < index; ++i) {
        int vad = WebRtcVad_Process(handle, 16000, pcm_data + offsetInshort + i * 160, 160);
        if (vad == 1) {
            b = JNI_TRUE;
        } else{
            b=JNI_FALSE;
        }
    }
    env->ReleaseShortArrayElements(audioData_, pcm_data, JNI_ABORT);
    WebRtcVad_Free(handle);
    return static_cast<jboolean>(b);
}