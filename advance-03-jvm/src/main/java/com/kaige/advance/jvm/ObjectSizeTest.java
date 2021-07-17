package com.kaige.advance.jvm;

import com.kaige.advance.agent.MyAgent;

/**
 * 对象大小的检测
 *
 * @author liukai 2021年07月17日
 */
public class ObjectSizeTest {
  
  public static void main(String[] args) {
    Object o = new Object();
    System.out.println("Object size = " + MyAgent.sizeOf(o));
    byte[] bytes = new byte[100];
    System.out.println("byte[] size = " + MyAgent.sizeOf(bytes));
  }
  
}
