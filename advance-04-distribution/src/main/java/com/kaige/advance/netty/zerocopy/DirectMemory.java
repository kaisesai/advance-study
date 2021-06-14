package com.kaige.advance.netty.zerocopy;

import java.nio.ByteBuffer;

/** 直接内存之零拷贝 */
public class DirectMemory {
  
  public static void main(String[] args) {
    for (int i = 0; i < 10; i++) {
      heapAccess();
      directAccess();
    }
    
    System.out.println();
    
    for (int i = 0; i < 10; i++) {
      heapAllocate();
      directAllocate();
    }
  }
  
  /** 堆内存访问 */
  public static void heapAccess() {
    long startTime = System.currentTimeMillis();
    // 分配堆内存
    ByteBuffer buffer = ByteBuffer.allocate(1000);
    // 访问堆内存
    long endTime = accessTimeTotal(buffer);
    System.out.println("堆内存访问耗时：" + (endTime - startTime) + "ms");
  }
  
  /** 直接内存访问 */
  public static void directAccess() {
    long startTime = System.currentTimeMillis();
    // 分配直接内存
    ByteBuffer buffer = ByteBuffer.allocateDirect(1000);
    // 访问直接内存
    long endTime = accessTimeTotal(buffer);
    System.out.println("直接内存访问耗时:" + (endTime - startTime) + "ms");
  }
  
  private static long accessTimeTotal(ByteBuffer buffer) {
    for (int i = 0; i < 100000; i++) {
      for (int i1 = 0; i1 < 200; i1++) {
        buffer.putInt(i1);
      }
      buffer.flip();
      for (int i1 = 0; i1 < 200; i1++) {
        buffer.getInt();
      }
      buffer.clear();
    }
    return System.currentTimeMillis();
  }
  
  /** 堆内存申请 */
  public static void heapAllocate() {
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < 100000; i++) {
      ByteBuffer.allocate(100);
    }
    long endTime = System.currentTimeMillis();
    System.out.println("堆内存申请耗时:" + (endTime - startTime) + "ms");
  }
  
  /** 直接内存申请 */
  public static void directAllocate() {
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < 100000; i++) {
      ByteBuffer.allocateDirect(100);
    }
    long endTime = System.currentTimeMillis();
    System.out.println("直接内存申请耗时:" + (endTime - startTime) + "ms");
  }
  
}
