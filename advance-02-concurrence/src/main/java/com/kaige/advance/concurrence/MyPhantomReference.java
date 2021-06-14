package com.kaige.advance.concurrence;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 虚引用的应用，用于处理虚拟机进行堆外内配
 *
 * @author liukai 2021年06月12日
 */
public class MyPhantomReference {
  
  private static final List<Object> LIST = new ArrayList<>();
  
  private static final ReferenceQueue<Object> REFERENCE_QUEUE = new ReferenceQueue<>();
  
  public static void main(String[] args) {
    Object referent = new Object();
    
    // 虚引用
    PhantomReference<Object> phantomReference = new PhantomReference<>(referent, REFERENCE_QUEUE);
    
    // 一个线程不断在堆内存写数据
    new Thread(() -> {
      while (true) {
        LIST.add(new byte[1024 * 1024]);
        try {
          TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
          Thread.currentThread().interrupt();
        } catch (OutOfMemoryError e) {
          System.exit(0);
        }
        System.out.println("读取虚引用中的对象：" + phantomReference.get());
      }
    }, "t1").start();
    
    // 一个线程引用队列
    new Thread(() -> {
      while (true) {
        Reference<?> reference = REFERENCE_QUEUE.poll();
        if (reference != null) {
          System.out.println("虚引用对象被 jvm 回收了，reference：" + reference);
        }
      }
    }, "t2").start();
    
    // try {
    //   TimeUnit.SECONDS.sleep(1);
    // } catch (InterruptedException e) {
    //   e.printStackTrace();
    // }
  }
  
}
