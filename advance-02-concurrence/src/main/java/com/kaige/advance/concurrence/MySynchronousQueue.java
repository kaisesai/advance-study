package com.kaige.advance.concurrence;

import java.util.concurrent.SynchronousQueue;

public class MySynchronousQueue {
  
  public static void main(String[] args) throws InterruptedException {
  
    SynchronousQueue<Object> synchronousQueue = new SynchronousQueue<>();
    
    // new Thread(()->{
      // try {
        // Thread.sleep(5000);
        // Object take = synchronousQueue.take();
        // System.out.println("take = " + take);
      // } catch (InterruptedException e) {
      //   e.printStackTrace();
      // }
    // },"线程 1").start();
    
    // Thread.sleep(1000);
  
    synchronousQueue.put("hello");
    System.out.println("put success");
    
  }
}
