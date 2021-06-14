package com.kaige.advance.concurrence;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

/**
 * 传输队列
 *
 * @author liukai 2021年06月14日18:13:45
 */
public class MyTransferQueue {
  
  public static void main(String[] args) throws InterruptedException {
    // 创建一个无界转换队列
    LinkedTransferQueue<Object> linkedTransferQueue = new LinkedTransferQueue<>();
    
    // 消费数据
    new Thread(() -> {
      try {
        TimeUnit.SECONDS.sleep(2);
        Object take = linkedTransferQueue.take();
        System.out.println("线程 1 消费队列数据 take = " + take);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }, "线程 1").start();
    
    // 生产数据
    new Thread(() -> {
      try {
        System.out.println("线程 2 准备生产数据 transfer");
        linkedTransferQueue.transfer("hello!");
        System.out.println("线程 2 生产数据结束 transfer");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }, "线程 2").start();
    
    Thread.sleep(1000);
    
    System.out.println("main 线程准备生产数据 transfer");
    linkedTransferQueue.transfer("hello!");
    System.out.println("main 线程生产数据成功 transfer");
    
    /*
      线程 2 准备生产数据 transfer
      main 线程准备生产数据 transfer
      线程 2 生产数据结束 transfer
      线程 1 消费队列数据 take = hello!
    
      此时主线程会一直阻塞，直到 linkedTransferQueue 被 take 消费数据
    
     */
  }
  
}
