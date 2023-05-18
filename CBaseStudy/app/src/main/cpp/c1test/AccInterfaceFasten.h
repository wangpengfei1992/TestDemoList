//
// Created by anker on 2023/4/26.
//


#ifndef CBASESTUDY_ACCINTERFACEFASTEN_H
#define CBASESTUDY_ACCINTERFACEFASTEN_H
#include <map>

using namespace std;
class AccInterfaceFasten {
public:
    char* signature(map<char*, char*>myMap);

    void digest();
};


#endif //CBASESTUDY_ACCINTERFACEFASTEN_H
