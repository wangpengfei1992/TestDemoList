//
// Created by anker on 2022/3/28.
//
#include <stdio.h>
#include <string.h>
/**
 * C 语言入口程序
 * @return
 * 该文件运行语句：1.进入此目录  2.执行：gcc Test1.c   3.执行：a.exe
 */
#define name "wpf"
#define message(a,b)\
    printf(#a" + "#b)
#if !defined(AGE)
#define AGE 18
#endif
#define M(x,y)((x)>(y)?(x):(y))


int max(int a, int b) {
    return a + b;
}

int funCallBack(int a, int b, int(*p)(int, int)) {
    return p(a, b);
}

int main() {//主函数，程序从这里开始执行
    printf("C  Hello World! \n");

    const int age = 18;
    printf("%s , %d \n", name, age);

    //函数指针变量可以作为某个函数的参数来使用的，回调函数就是一个通过函数指针调用的函数。
    //函数指针
    int (*p)(int, int) = max;
    printf("1===%d \n", p(p(1, 1), 2));
    //回调函数
    printf("2===%d \n", funCallBack(1, 2, max));

    //字符串操作
    char s1[12] = "wpf";
    char s2[12] = "is good boy";
    char s3[12] = "";
    strcpy(s3, s2);
    printf("s1======%s\n", s3);
    printf("s2======%s\n", strcat(s1, s2));
    printf("s3======%d\n", strlen(s1));
    printf("s4======%d\n", strcmp(s1, s2));
//....................................................

    //输入输出
/*    printf("please enter float number:\n");
    float f;
    scanf("%f",&f);
    printf("input is: %f \n",f);*/

    //getchar()、putchar()
/*    int c;
    printf("please enter :\n");
    c = getchar();
    printf("enter  frist:\n");
    putchar(c);*/

//gets()、puts()

//....................................................
    //文件读写

    //写文件
/*    FILE *file = NULL;
    file = fopen("D:\\wpf\\test\\TestDemoList\\CBaseStudy\\app\\src\\main\\cpp\\ctest\\a.text","a+");
    fprintf(file,"111111111111\n");
    fprintf(file,"2222222\n");
    fputs("puts1111111111\n",file);
    fclose(file);*/
    //读文件
    FILE *fp = NULL;
    fp = fopen("D:\\wpf\\test\\TestDemoList\\CBaseStudy\\app\\src\\main\\cpp\\ctest\\a.text","r");
    char buff[255];
    fgets(buff,255,fp);
    printf("r: %s\n", buff);
    fclose(fp);
    //....................................................
//系统自带宏定义
    printf("Date:%s\n",__DATE__);
    printf("Time:%s\n",__TIME__);
    printf("Flie:%s\n",__FILE__);

    message(wpf,lyy);
    printf("\nAGE:%d\n",AGE);
    printf("m:%d\n",M(1,2));

    return 0;
}
