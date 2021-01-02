package com.kaige.advance.concurrence;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyExecutor {
  
  public static void main(String[] args) throws InterruptedException {
    ExecutorService executorService = Executors.newCachedThreadPool();
    executorService.execute(()-> System.out.println("你好"));
    
    Thread.sleep(60000);
  
    executorService.execute(()-> System.out.println("不好"));
    // executorService.shutdown();
    
    
  }
}
