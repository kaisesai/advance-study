package com.kaige.advance.concurrence;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport
 *
 * @author liukai 2021年06月07日23:51:34
 */
public class MyLockSupport {
  
  public static void main(String[] args) throws InterruptedException {
    
    Thread t1 = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        if (i == 5) {
          System.out.println("t1 线程暂停");
          LockSupport.park();
          System.out.println("t1 线程继续");
        }
        System.out.println("执行循环 i = " + i);
        try {
          TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(1000));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    
    t1.start();
    
    TimeUnit.SECONDS.sleep(5);
    
    LockSupport.unpark(t1);
    System.out.println("main 线程唤醒 t1 线程");

    /*
     执行循环 i = 0
     执行循环 i = 1
     执行循环 i = 2
     执行循环 i = 3
     执行循环 i = 4
     t1 线程暂停
     main 线程唤醒 t1 线程
     t1 线程继续
     执行循环 i = 5
     执行循环 i = 6
     执行循环 i = 7
     执行循环 i = 8
     执行循环 i = 9
    */
  }
  
}
