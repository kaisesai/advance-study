package com.kaige.advance.concurrence;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

public class MyExchanger {
  
  public static void main(String[] args) {
    
    Exchanger<String> exchanger = new Exchanger<>();
    
    new Thread(() -> {
      String a = "银行流水A";
      try {
        exchanger.exchange(a);
        System.out.println(Thread.currentThread().getName() + "交换流水 A");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }, "线程1").start();
    
    new Thread(() -> {
      String b = "银行流水B";
      try {
        TimeUnit.SECONDS.sleep(2);
        String exchange = exchanger.exchange(b);
        System.out
          .println("A和B的数据是否一致：" + b.equals(exchange) + "，A 录入的是：" + exchange + "，B 录入的是：" + b);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }, "线程1").start();
  }
  
}
