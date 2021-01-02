package com.kaige.advance.jvm;

public class StringTest {
  
  public static void main(String[] args) {
    
    String s1 = "Hello";
    String s2 = "Hello";
    // intern 方法会返回当前字符串对象在常量池中的字符串地址，如果存在
    String s3 = s2.intern();
    
    System.out.println("s1 == s2 = " + (s1 == s2));
    System.out.println("s1 == s3 = " + (s1 == s3));
    System.out.println("s2 == s3 = " + (s2 == s3));
    
  }
  
}
