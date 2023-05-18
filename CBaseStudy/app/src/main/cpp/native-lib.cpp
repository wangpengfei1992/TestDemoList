#include <jni.h>
#include <string>
#include "c1test/helloword.h"
#include "c1test/AccInterfaceFasten.h"
extern "C" JNIEXPORT jstring JNICALL
Java_com_wpf_cbasestudy_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    helloword *hw = new helloword();
    hw->main();
    AccInterfaceFasten *fasten = new AccInterfaceFasten();
    //创建并初始化 map 容器
    // 拼接前的Key,Value map[app_secret:EGOPM4613EASFGE country:US language:en terminal-id:2711b781-b925-4b5c-94ac-1ed1358b130c timestamp:1682496373 user-agent:Ankerwork-Windows-v2.0.1 x-request-nonce:180906de-daad-4d8e-91ef-5e65b72d2502]
    map<char*, char*>myMap{ {"app_secret","EGOPM4613EASFGE"}
    ,{"country","US"}
            ,{"language","en"}
            ,{"terminal-id","2711b781-b925-4b5c-94ac-1ed1358b130c"}
            ,{"timestamp","1682496373"}
            ,{"user-agent","Ankerwork-Windows-v2.0.1"}
            ,{"x-request-nonce","180906de-daad-4d8e-91ef-5e65b72d2502"}};

    fasten->signature(myMap);
    return env->NewStringUTF(hello.c_str());
}


std::map<char*,char*> jmap2cmap(JNIEnv* env,jobject jobj){
    std::map<char*,char*> cmap;
    jclass jmapclass = env->FindClass("java/util/HashMap");
    jmethodID jkeysetmid = env->GetMethodID(jmapclass, "keySet", "()Ljava/util/Set;");
    jmethodID jgetmid = env->GetMethodID(jmapclass, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
    jobject jsetkey = env->CallObjectMethod(jobj,jkeysetmid);
    jclass jsetclass = env->FindClass("java/util/Set");
    jmethodID jtoArraymid = env->GetMethodID(jsetclass, "toArray", "()[Ljava/lang/Object;");
    jobjectArray jobjArray = (jobjectArray)env->CallObjectMethod(jsetkey,jtoArraymid);
    if(jobjArray==NULL){

    }
    jsize arraysize = env->GetArrayLength(jobjArray);
    int i=0;
    for( i=0; i < arraysize; i++ ){
        jstring jkey = (jstring)env->GetObjectArrayElement(jobjArray, i );
        jstring jvalue = (jstring)env->CallObjectMethod(jobj,jgetmid,jkey);
        char* key = (char*)env->GetStringUTFChars(jkey,0);
        char* value = (char*)env->GetStringUTFChars(jvalue,0);
        cmap[key]=value;
    }
    return cmap;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_wpf_cbasestudy_MainActivity_signMap(JNIEnv *env, jobject thiz, jobject map) {
    AccInterfaceFasten *fasten = new AccInterfaceFasten();
    return env->NewStringUTF(fasten->signature(jmap2cmap(env,map)));

}