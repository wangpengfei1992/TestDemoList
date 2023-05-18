//
// Created by anker on 2023/4/26.
//

#include "AccInterfaceFasten.h"
#include <iostream>
#include <map>
#include <android/log.h>
#include "../tools/AndroidLog.h"
#include "string"
#include <vector>
#include <algorithm>
#include "../tools/sha256.h"

using namespace std;
std::string toLower(std::string s) {
    std::transform(s.begin(), s.end(), s.begin(),
                   [](unsigned char c){ return std::tolower(c); });
    return s;
}
typedef pair<string, string> PAIR;

bool cmp_by_value(const PAIR& lhs, const PAIR& rhs) {
    return lhs.second < rhs.second;
}

struct CmpByValue {
    bool operator()(const PAIR& lhs, const PAIR& rhs) {
        return lhs.second < rhs.second;
    }
};
struct CmpByKey {
    bool operator()(const PAIR& lhs, const PAIR& rhs) {
        string key1 = toLower(lhs.first);
        string key2 = toLower(rhs.first);
//        error("内容是：：：%s,%s",key1.c_str(),key2.c_str())
        int length = 0;
        bool camp = false;
        if (key1.length()>key2.length()){
            camp = false;
            length = key2.length();
        } else if (key1.length()<key2.length()){
            camp = true;
            length = key2.length();
        } else{
            length = key1.length();
        }

        for (int i = 0; i < length; ++i) {
//            error("内容是：：：%s,%c,%c",tolower(key1[i]),tolower(key2[i]))
            if (key1[i]<key2[i]){
                camp = true;
                break;
            } else if (key1[i]>key2[i]){
                camp = false;
                break;
            }
        }
//        error("比较结果：：：%d",camp)
        return camp;
    }
};


char* AccInterfaceFasten::signature(map<char*, char*> myMap) {
    //把map中元素转存到vector中
    vector<PAIR> vec(myMap.begin(), myMap.end());
    sort(vec.begin(), vec.end(), CmpByKey());
    error("数组长度：：：%d",vec.size());

    string bf = "";
    for (int i = 0; i != vec.size(); ++i) {
//        cout << vec[i] << endl;
        bf.append(vec[i].second);
        if (i < vec.size()-1){
            bf.append("&");
        }
    }
    // 拼接后的字符串 EGOPM4613EASFGE&US&en&2711b781-b925-4b5c-94ac-1ed1358b130c&1682496373&Ankerwork-Windows-v2.0.1&180906de-daad-4d8e-91ef-5e65b72d2502
    error("输出：：：%s",bf.c_str());

    string message_digest = toLower(Ly::Sha256::getInstance().getHexMessageDigest(bf));
    // 得到的签名结果：be44fa407a798342d98434fd3ffee4975348f994f53d79a8232353f8a9d36c3c
    error("加密结果：：：%s",message_digest.c_str());
/*    //调用 begin()/end() 组合，遍历 map 容器
    for (map<string, string>::iterator iter = myMap.begin(); iter != myMap.end(); ++iter) {
        string data = iter->first + "," + iter->second;
        const char *name = "wpf";
        error("%s",data.c_str());
    }*/
    return const_cast<char *>(message_digest.c_str());
}

void AccInterfaceFasten::digest() {

}
