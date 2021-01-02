package com.kaige.advance.concurrence;

import java.util.concurrent.LinkedTransferQueue;

public class MyTransferQueue {
  
  public static void main(String[] args) throws InterruptedException {
    // 创建一个无界转换队列
    LinkedTransferQueue<Object> linkedTransferQueue = new LinkedTransferQueue<>();
    
    new Thread(()->{
      try {
        Thread.sleep(10000);
        Object take = linkedTransferQueue.take();
        System.out.println("take = " + take);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    },"线程 1").start();
  
    Thread.sleep(1000);
  
    System.out.println("准备 transfer ...");
    linkedTransferQueue.transfer("hello 啊！");
    System.out.println("transfer 成功~！@#￥%……&*");
  }
}
