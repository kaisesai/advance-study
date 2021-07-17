package com.kaige.advance.agent;

import java.lang.instrument.Instrumentation;

/**
 * 自定义 agent，用来获取底层工具类，在程序中通过工具类来获取类的占用大小
 */
public class MyAgent {
  
  private static Instrumentation inst;
  
  public static void premain(String agentArgs, Instrumentation _inst) {
    inst = _inst;
  }
  
  public static long sizeOf(Object o) {
    return inst.getObjectSize(o);
  }
  
}
