package com.wpf.myviewtest.ui.aqs;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Author: feipeng.wang
 * Time:   2021/12/30
 * Description : 自定义独占锁
 */
public class MyLock {
    private Sync sync = new Sync();
    public void lock(){
        sync.tryAcquire(1);
    }
    public void unlock(){
        sync.tryRelease(0);
    }

    class Sync extends AbstractQueuedSynchronizer{
        @Override
        protected boolean tryAcquire(int arg) {
            return compareAndSetState(0,1);
        }

        @Override
        protected boolean tryRelease(int arg) {
            setState(0);
            return true;
        }
    }
}
