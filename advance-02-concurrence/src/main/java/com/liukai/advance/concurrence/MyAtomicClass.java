package com.liukai.advance.concurrence;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;

/**
 * 原子操作类
 */
public class MyAtomicClass {
  
  volatile static boolean flag = false;
  
  public static void main(String[] args) throws InterruptedException, NoSuchFieldException {
    // 测试 AtomicReference
    // testAtomicReference();
    
    // 测试 AtomicReferenceFieldUpdater
    // testAtomicReferenceFieldUpdater();
    
    // 测试 MyAtomicFieldUpdater
    // testMyAtomicFieldUpdater();
    
    // 测试 LockSupport
    // testLockSupport();
    
    // 测试指令重排序
    for (int i = 0; i < 10000; i++) {
      testFence();
    }
    
  }
  
  private static void testFence() throws InterruptedException {
    
    MyData myData = new MyData();
    
    Thread t1 = new Thread(() -> {
      // 禁止 load 指令重排序
      // MyAtomicObjUpdater.UNSAFE.fullFence();
      // MyAtomicObjUpdater.UNSAFE.loadFence();
      myData.a = myData.j;
      // MyAtomicObjUpdater.UNSAFE.storeFence();
      myData.i = 1;
      
    }, "t1");
  
  
    
    Thread t2 = new Thread(() -> {
      // 禁止 load 指令重排序
      // MyAtomicObjUpdater.UNSAFE.fullFence();
      // MyAtomicObjUpdater.UNSAFE.loadFence();
      myData.b = myData.i;
      // MyAtomicObjUpdater.UNSAFE.storeFence();
      
      myData.j = 2;
      
    }, "t2");
    
    t1.start();
    
    Thread.sleep(1);
    
    t2.start();
    
    // t1.join();
    // t2.join();
    
    Thread.sleep(1);
    // a=0, b=1, i=1, j=2
    // a=2, b=0, i=1, j=2
    // a=0, b=0, i=1, j=2
  
    // System.out.printf("a=%s, b=%s, i=%s, j=%s%n", myData.a, myData.b, myData.i, myData.j);
    
    if(myData.b==0){
      System.out.printf("a=%s, b=%s, i=%s, j=%s%n", myData.a, myData.b, myData.i, myData.j);
    }
  }
  
  private static void testLockSupport() throws InterruptedException {
    final Object o = new Object();
    
    Thread t1 = new Thread(() -> {
      
      System.out.println(Thread.currentThread().getName() + "线程执行 park 操作");
      // 阻塞自己
      LockSupport.park(o);
      System.out.println(Thread.currentThread().getName() + "线程被唤醒了，开始做自己的事情...");
    }, "t1");
    
    t1.start();
    
    Thread.sleep(2000);
    
    System.out
      .println(Thread.currentThread().getName() + "线程执行 unpark 操作，准备唤醒" + t1.getName() + "线程的");
    LockSupport.unpark(t1);
  }
  
  private static void testMyAtomicFieldUpdater() throws InterruptedException {
    // 原子引用类
    MyAtomicObjUpdater maou = new MyAtomicObjUpdater(10);
    
    // 创建线程
    for (int i = 0; i < 20; i++) {
      new Thread(() -> {
        for (int j = 0; j < 100; j++) {
          int value = maou.incrementAndGet();
          System.out.println(Thread.currentThread().getName() + "线程，value = " + value);
        }
      }, "t" + i).start();
    }
    
    Thread.sleep(2000);
    
    System.out.println("maou.getAge() = " + maou.getAge());
  }
  
  private static void testAtomicReferenceFieldUpdater() {
    MyObj myObj = new MyObj(2, "花花");
    AtomicReferenceFieldUpdater<MyObj, Integer> arfu = AtomicReferenceFieldUpdater
      .newUpdater(MyObj.class, Integer.class, "age");
    
    Integer age = arfu.get(myObj);
    System.out.println("integer = " + age);
  }
  
  private static void testAtomicReference() throws InterruptedException {
    // 原子引用类
    MyObj myObj = new MyObj(0, "卡卡");
    AtomicReference<MyObj> atomicReference = new AtomicReference<>(myObj);
    
    // 创建两个线程修改属性
    Thread t1 = new Thread(() -> updateMyObj(atomicReference, 1, "tom"), "t1");
    Thread t2 = new Thread(() -> updateMyObj(atomicReference, 2, "Jerry"), "t2");
    
    // 启动线程
    t1.start();
    t2.start();
    
    // 让这两个线程先执行完任务
    t1.join();
    t2.join();
    
    System.out.println("atomicReference.get() = " + atomicReference.get());
  }
  
  private static void updateMyObj(AtomicReference<MyObj> atomicReference, int age, String name) {
    MyObj myObj = new MyObj(age, name);
    MyObj obj = atomicReference.getAndSet(myObj);
    System.out.println(Thread.currentThread().getName() + "线程，obj = " + obj);
  }
  
  static class MyData {
    
     int a = 0;
  
     int b = 0;
  
     int i = 0;
  
     int j = 0;
    
  }
  
  /**
   * 原子的字段更新器
   */
  static class MyAtomicObjUpdater {
    
    private static final Unsafe UNSAFE = getUnsafe();
    
    private static final long OFFSET;
    
    static {
      try {
        // 通过 Unsafe 类的 objectFieldOffset 方法获取 age 字段的偏移量
        OFFSET = UNSAFE.objectFieldOffset(MyAtomicObjUpdater.class.getDeclaredField("age"));
      } catch (NoSuchFieldException e) {
        throw new Error(e);
      }
    }
    
    private volatile int age;
    
    public MyAtomicObjUpdater(int age) {
      this.age = age;
    }
    
    /**
     * @return 通过反射获取 Unsafe 的实例
     */
    private static Unsafe getUnsafe() {
      try {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        return (Unsafe) theUnsafe.get(null);
      } catch (Exception e) {
        throw new Error(e);
      }
      
    }
    
    public int getAge() {
      return age;
    }
    
    public int incrementAndGet() {
      int oldValue;
      do {
        // 获取旧值
        oldValue = UNSAFE.getIntVolatile(this, OFFSET);
        // 设置新值
      }
      while (!UNSAFE.compareAndSwapInt(this, OFFSET, oldValue, oldValue + 1));
      return oldValue + 1;
    }
    
  }
  
  static class MyObj {
    
    volatile Integer age;
    
    private String name;
    
    public MyObj(Integer age, String name) {
      this.age = age;
      this.name = name;
    }
    
    @Override
    public String toString() {
      return "MyObj{" + "age=" + age + ", name='" + name + '\'' + '}';
    }
    
  }
  
}
