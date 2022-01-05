package com.wpf.myfirstdemo.test_thread;

import android.util.Log;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Author: feipeng.wang
 * Time:   2021/7/21
 * Description : This is description.
 */
public class ReetrantLockTest {
    private final String TAG = "ReetrantLoackTest";
    private static ReetrantLockTest instance = null;

    public static ReetrantLockTest getInstance() {
        synchronized (ReetrantLockTest.class){
            if (instance == null){
                instance = new ReetrantLockTest();
            }
        }
        return instance;
    }
    //ReetrantLock的公平锁，实现交替打印
    private Lock lock = new ReentrantLock(true);
    public void test1(){
        new Thread(() -> {
            printTest();
        },"线程A").start();
        new Thread(() -> {
            printTest();
        },"线程B").start();
    }

    private void printTest() {
        for (int i = 0; i < 2; i++) {
            try {
                lock.lock();
                Log.e(TAG,"printTest==="+Thread.currentThread().getName()+","+i);
                Thread.sleep(2);
            }catch (Exception e){
                Log.e(TAG,"e==="+e.getMessage());
            }finally {
                lock.unlock();
            }
        }
    }
    /*
    * ReetrantLock和synchronized对比
    * 1.均可重，synchrnizeds使用更方便，自动施法锁；ReetranLock的lock和unlock成对出现，必须释放后其他线程才能获取
    * */
}
