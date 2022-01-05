//
// Created by anker on 2021/4/28.
//

#include "CTest.h"
#include <stdio.h>
#include "../util/LogHelp.h"
#include <time.h>


int max(int a,int b){
    return a+b;
}

int * getRandom(){
    //注意这里返回指针，必须使用static
    static int data[6] = {1,2,3,4,5,6};
    for ( int i = 0; i < 6; ++i){
        LOGE( "r[%d] = %d\n", i, data[i]);
    }
//    enum Day myday = First;//枚举的使用
    testHand();
   /* struct Book book;
    strcpy(book.name, "english");
    LOGE("结构体信息 %s",book.name)
*/
    return data;
}

void showRandom(int *p,int length){
    for (int i = 0; i < length; ++i) {
//        LOGE("第%d个数据是%d");
        LOGE("第%d个数据是%d",i,*(p+i));
    }
}

/*指针的使用*/
/*与指针相关的概念*/
void testHand() {
    int *P = NULL;
    LOGE("指针的地址:%p \n",P);
    if (P){
        LOGE("不是空指针 \n");
    } else{
        LOGE("是空指针 \n");
    }


/*    *//*指针的算术运算*//*
    int *d1 = NULL;
    int numbers[3] = {1,2,3};
    d1 = numbers;
    for (int t= 0; t <3 ; t++) {
        LOGE("d指针的值:%d \n",*(d1+t));
    }
    *d1 = NULL;*/


    char *names[] = {"张三","李四","王二","麻子"};
    for (int i = 0; i <4 ; ++i) {
        LOGE("字符指针数组内容:%d ,值:%s \n",i,names[i]);
    }
    *names = NULL;
    /*指向变量的指针*/
    unsigned long second;
    getSecond(&second);
    LOGE("指向变量的指针值:%ld \n",second);//注意long占位符:ld

    /*函数指针*/
    int (*p)(int,int) = *max;
    int reslut = max(max(1,2),3);
    LOGE("函数指针返回值:%d \n",reslut);//注意long占位符:ld

}

void getSecond(unsigned long *p){
    *p = time(NULL);
    return;
}

/*文件读写*/

void writeFileTest(char* filePath){
    /* * mode: r、w、a、r+、w+、a+
     * 二进制：rb、wb、ab
     * */

    FILE *file = NULL;
    file = fopen(filePath,"a+");
    fprintf(file, " fprintf 我是添加进来的1\n");
    fprintf(file, "fprintf 我是添加进来的2\n");
    fputs("fputs 我是添加进来的1\n", file);
    fputs("fputs 我是添加进来的2\n", file);
    fclose(file);
}
