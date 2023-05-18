//
// Created by anker on 2023/4/26.
//
#include <map>
#include <iostream>
#include <list>
using namespace std;
class InterfaceC{

    void test(map<string,string> headMap){
        //调用 begin()/end() 组合，遍历 map 容器
        for (map<string, string>::iterator iter = headMap.begin(); iter != headMap.end(); ++iter) {
            cout << iter->first << " " << iter->second << endl;
        }

    }


};

