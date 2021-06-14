package com.kaige.advance.jvm;

/** 垃圾收集测试，运行时 JVM 参数：-XX:+PrintGCDetails -Xms=100M -Xmx=100M */
public class GCTest {
  
  static int unitM = 1024 * 1024;
  
  public static void main(String[] args) {
    // 分配一个大对象 50M
    byte[] bigObject1 = new byte[unitM * 20];
    byte[] bigObject2 = new byte[unitM * 10];
    // byte[] bigObject3 = new byte[ unitM * 14];
    
  }
  
}
