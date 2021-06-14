package com.kaige.advance.concurrence;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 阶段类
 *
 * <p>该类类似于循环栅栏，可以执行多次
 *
 * @author liukai 2021年06月07日22:03:10
 */
public class MyPhaser extends Phaser {
  
  private static final ThreadLocalRandom THREAD_LOCAL_RANDOM = ThreadLocalRandom.current();
  
  public static void main(String[] args) {
    
    MyPhaser phaser = new MyPhaser();
    // 注册 5 个参数者
    int parties = 7;
    phaser.bulkRegister(parties);
    
    for (int i = 0; i < parties - 2; i++) {
      new Thread(new Person(phaser, "客人" + i)).start();
    }
    new Thread(new Person(phaser, "新娘")).start();
    new Thread(new Person(phaser, "新郎")).start();
  }
  
  private static void randomMilliSleep() {
    try {
      TimeUnit.MILLISECONDS.sleep(THREAD_LOCAL_RANDOM.nextInt(1000));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  /**
   * @param phase             阶段
   * @param registeredParties 注册的参与者
   * @return 是否要继续
   */
  @Override
  protected boolean onAdvance(int phase, int registeredParties) {
    switch (phase) {
      case 0:
        System.out.println("所有人到齐了！");
        return false;
      case 1:
        System.out.println("所有人吃完了！");
        return false;
      case 2:
        System.out.println("所有人离开了！");
        return false;
      case 3:
        System.out.println("新郎新娘入洞房！");
        return true;
      default:
        return true;
    }
  }
  
  @Data
  @AllArgsConstructor
  static class Person implements Runnable {
    
    private final Phaser phaser;
    
    private String name;
    
    public void arrive() {
      randomMilliSleep();
      System.out.println(name + " 到达现场！");
      phaser.arriveAndAwaitAdvance();
    }
    
    public void eat() {
      
      randomMilliSleep();
      System.out.println(name + " 吃完了！");
      phaser.arriveAndAwaitAdvance();
    }
    
    public void leave() {
      randomMilliSleep();
      System.out.println(name + " 离开现场！");
      phaser.arriveAndAwaitAdvance();
    }
    
    public void hug() {
      if ("新郎".equals(this.name) || "新娘".equals(this.name)) {
        randomMilliSleep();
        System.out.println(name + " 洞房！");
        phaser.arriveAndAwaitAdvance();
      } else {
        // 取消注册
        phaser.arriveAndDeregister();
      }
    }
    
    @Override
    public void run() {
      // 到达现场
      arrive();
      // 开吃
      eat();
      // 散场
      leave();
      // 入洞房
      hug();
    }
    
  }
  
}
