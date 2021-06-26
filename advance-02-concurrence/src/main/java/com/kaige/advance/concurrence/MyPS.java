package com.kaige.advance.concurrence;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 并发
 */
public class MyPS {
  
  static List<Integer> nums = new ArrayList<>();
  
  static {
    Random r = new Random();
    for (int i = 0; i < 10000; i++) {
      nums.add(1000000 + r.nextInt(1000000));
    }
  }
  
  public static void foreach() {
    nums.forEach(v -> isPrime(v));
  }
  
  public static void parallel() {
    nums.parallelStream().forEach(MyPS::isPrime);
  }
  
  static boolean isPrime(int num) {
    for (int i = 2; i <= num / 2; i++) {
      if (num % i == 0) {
        return false;
      }
    }
    return true;
  }
  
}
