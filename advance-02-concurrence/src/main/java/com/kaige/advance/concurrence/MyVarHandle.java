package com.kaige.advance.concurrence;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * VarHandle 类型
 *
 * @author liukai 2021年06月12日
 */
public class MyVarHandle {
  
  /** 获取变量句柄 */
  private static VarHandle varHandle;
  
  static {
    try {
      // 获取变量句柄
      varHandle = MethodHandles.lookup().findVarHandle(MyVarHandle.class, "x", int.class);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }
  
  private final int x = 8;
  
  public static void main(String[] args) {
    MyVarHandle myVarHandle = new MyVarHandle();
    
    System.out.println("handle get x = " + varHandle.get(myVarHandle));
    System.out.println("myVarHandle.x = " + myVarHandle.x);
    
    varHandle.compareAndSet(myVarHandle, 8, 9);
    System.out.println("handle get x = " + varHandle.get(myVarHandle));
    
    Object andAdd = varHandle.getAndAdd(myVarHandle, 10);
    System.out.println("andAdd = " + andAdd);
    System.out.println("myVarHandle.x = " + myVarHandle.x);
  }
  
}
