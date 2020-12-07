package com.liukai.advance.jvm;

public class User {
  
  private final byte[] bytes = new byte[1024];
  
  private String name;
  
  private int age;
  
  public User() {
  }
  
  public User(String name, int age) {
    this.name = name;
    this.age = age;
  }
  
  public void sout() {
    System.out.println("this is User class");
  }
  
  @Override
  public String toString() {
    return "User{" + "name='" + name + '\'' + ", age=" + age + '}';
  }
  
  @Override
  protected void finalize() throws Throwable {
    System.out.println(age + "user 对象准备销毁");
    super.finalize();
  }
  
}
