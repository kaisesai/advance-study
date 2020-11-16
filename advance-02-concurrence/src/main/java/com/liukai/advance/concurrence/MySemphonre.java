package com.liukai.advance.concurrence;

import java.util.concurrent.Semaphore;

public class MySemphonre {
  
  public static void main(String[] args) throws InterruptedException {
    // 创建一个信号量，令牌数为 1
    Semaphore semaphore = new Semaphore(1);
    
    // 启动一个线程
    Thread t1 = new Thread(() -> {
      try {
        // 获取一个令牌
        semaphore.acquire();
        System.out.println(Thread.currentThread().getName() + " 获取到一个资源");
        // t1 线程睡眠 2 秒
        Thread.sleep(2000);
        System.out.println(Thread.currentThread().getName() + " 释放了一个资源");
        // 释放令牌
        semaphore.release();
        
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
    }, "线程 1");
    
    // 启动线程
    t1.start();
    
    // 主线程睡眠 1 秒
    Thread.sleep(1000);
    
    // 主线程获取令牌
    semaphore.acquire();
    System.out.println(Thread.currentThread().getName() + " 线程获取到了资源");
    // 主线程释放令牌
    semaphore.release();
    System.out.println(Thread.currentThread().getName() + " 线程释放到了资源");
    
  }
  
}
