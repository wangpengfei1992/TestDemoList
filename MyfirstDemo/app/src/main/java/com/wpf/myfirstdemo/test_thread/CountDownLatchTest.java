package com.wpf.myfirstdemo.test_thread;

import android.util.Log;

import java.util.concurrent.CountDownLatch;

/**
 * Author: feipeng.wang
 * Time:   2021/7/21
 * Description : This is description.
 */
public class CountDownLatchTest {
    private final String TAG = "ReetrantLoackTest";
    private static CountDownLatchTest instance = null;

    public static CountDownLatchTest getInstance() {
        synchronized (CountDownLatchTest.class){
            if (instance == null){
                instance = new CountDownLatchTest();
            }
        }
        return instance;
    }

    //(先执行子线程，再执行主线程)
    public void test1() throws InterruptedException {
        int number = 5;
        CountDownLatch countDownLatch = new CountDownLatch(number);
        for (int i = 0; i < number; i++) {
            new Thread(()->{
                try {
                    Log.e(TAG,"子线程任务==="+Thread.currentThread().getName());
                }finally {
                    countDownLatch.countDown();
                }
            },i+"").start();
        }

        Log.e(TAG,"等待子线程任务结束===");
        countDownLatch.await();
        Log.e(TAG,"主程任务==="+Thread.currentThread().getName());
    }
    //(先执行主线程，再执行子线程)
    public void test2() throws InterruptedException {
        CountDownLatch awitLatch = new CountDownLatch(1);
        int number = 5;
        CountDownLatch countDownLatch = new CountDownLatch(number);
        for (int i = 0; i < number; i++) {
            new Thread(()->{
                try {
                    awitLatch.await();
                    Log.e(TAG,"子线程任务==="+Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            },i+"").start();
        }
        Log.e(TAG,"主程任务先执行==="+Thread.currentThread().getName());
        awitLatch.countDown();
        Log.e(TAG,"等待子线程任务结束===");
        countDownLatch.await();
        Log.e(TAG,"子线程任务结束===");
    }
}
