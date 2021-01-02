package com.kaige.advance.jvm;

/**
 * JVM 设置：-Xss128（默认 1k）
 */
public class StackOverflowError {
  
  static int count;
  
  static void redo() {
    count++;
    redo();
  }
  
  public static void main(String[] args) {
    try {
      redo();
    } catch (Throwable e) {
      e.printStackTrace();
      System.out.println(count);
    }
  }
  
}
