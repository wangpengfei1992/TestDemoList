//
// Created by anker on 2021/4/28.
//

#ifndef NDKTEST_CTEST_H
#define NDKTEST_CTEST_H

#endif //NDKTEST_CTEST_H

/*
 * c语言练习
 * */
int *getRandom();
void showRandom(int *p,int length);
void testHand();
void getSecond(unsigned long *p);


/*枚举*/

enum Day{
    First,Second,Third
};

/*结构体*/
struct Book{
    char name[20];
    double price;
};

/*文件*/
void writeFileTest(char* filePath);