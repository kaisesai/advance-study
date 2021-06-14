package com.kaige.advance.concurrence;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/** 同步队列，内配没有真正的队列，一个线程给另一个线程下达任务 */
public class MySynchronousQueue {
  
  public static void main(String[] args) throws InterruptedException {
    
    // SynchronousQueue 公平实现的为：队列；非公平实现为：栈
    // 公平模式
    // BlockingQueue<Object> synchronousQueue = new SynchronousQueue<>(true);
    // 非公平模式
    BlockingQueue<Object> synchronousQueue = new SynchronousQueue<>(true);
    
    // 消费者线程
    new Thread(() -> {
      try {
        System.out.println("线程 1 睡 5 秒");
        Thread.sleep(2000);
        Object take = synchronousQueue.take();
        System.out.println("线程 1 take = " + take);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }, "线程 1").start();
    
    // 公平模式下，线程 2 无法 put 成功
    new Thread(() -> {
      try {
        System.out.println("线程 2 put");
        synchronousQueue.put("hello2");
        System.out.println("线程 2 put 成功");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }, "线程 2").start();
    
    Thread.sleep(1000);
    
    // 公平模式下，main 线程 put 成功
    System.out.println("main 线程 put 开始");
    // 如果 synchronousQueue 已经有一个线程在 put 并且没有线程消费，那么那个线程就会一直在阻塞，此时如果开启新的线程往
    // synchronousQueue 中继续 put 也会被阻塞，直到有一个线程 take 消息
    synchronousQueue.put("hello");
    System.out.println("main 线程 put success");
    
    
    /*
      公平模式下：new SynchronousQueue<>(true);
        线程 1 睡 5 秒
        线程 2 put
        main 线程 put 开始
        线程 2 put 成功
        线程 1 take = hello2

      非公平模式下：new SynchronousQueue<>(false);
        线程 1 睡 5 秒
        线程 2 put
        main 线程 put 开始
        线程 2 put 成功
        线程 1 take = hello2

     */
  }
  
}
