package com.liukai.advance.jvm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * -Xms10M -Xmx10M -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=jvm.dump -XX:+PrintCommandLineFlags
 */
public class OOMTest {
  
  public static void main(String[] args) {
    List<Object> list = new ArrayList<>();
    int i = 0;
    int j = 0;
    while (true) {
      list.add(new User(UUID.randomUUID().toString(), i++));
      new User(UUID.randomUUID().toString(), j++);
    }
  }
  
}
