package com.kaige.advance.jvm;

public class TestIPulsPlus {

  public static void main(String[] args) {
    int i = 8;
    i++;
    System.out.println("i = " + i);
    i = i++;
    System.out.println("i = " + i);
    i = ++i;
    System.out.println("i = " + i);
  }
}
