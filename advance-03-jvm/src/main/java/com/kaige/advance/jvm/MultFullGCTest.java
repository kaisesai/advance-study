package com.kaige.advance.jvm;

import java.util.ArrayList;
import java.util.List;

public class MultFullGCTest {
  
  public static void main(String[] args) {
    
    while (true) {
      try {
        List<User> users = queryUsers();
        System.out.println(users);
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  private static List<User> queryUsers() {
    int initialCapacity = 5000;
    ArrayList<User> users = new ArrayList<>(initialCapacity);
    for (int i = 0; i < initialCapacity; i++) {
      String s = String.valueOf(new MyClassLoader("12313"));
      users.add(new User("a", i));
    }
    return users;
  }
  
}
