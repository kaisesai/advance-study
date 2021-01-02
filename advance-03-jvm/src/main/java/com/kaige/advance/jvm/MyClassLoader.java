package com.kaige.advance.jvm;

// import com.sun.crypto.provider.DESKeyFactory;


import com.mysql.cj.jdbc.Driver;
import com.sun.crypto.provider.DESKeyFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
// import java.lang.reflect.Method;

@Slf4j
public class MyClassLoader extends ClassLoader {
  
  private String classPath;
  
  public MyClassLoader(String classPath) {
    this.classPath = classPath;
  }
  
  public static void main(String[] args) throws SQLException {
    // 展示系统类加载器
    // 1. 启动类加载器
    ClassLoader bootStrapClassLoader = String.class.getClassLoader();
    log.info("bootStrapClassLoader = " + bootStrapClassLoader);
    // 2. 扩展类加载器
    ClassLoader extClassLoader = DESKeyFactory.class.getClassLoader();
    log.info("extClassLoader = " + extClassLoader);
    // 3. 应用程序类加载器：sun.misc.Launcher$AppClassLoader
    ClassLoader appClassLoader = MyClassLoader.class.getClassLoader();
    log.info("appClassLoader = " + appClassLoader);
    // 4. 系统类加载器：sun.misc.Launcher$AppClassLoader
    ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    log.info("systemClassLoader = " + systemClassLoader);
    
    // Thread.currentThread().setContextClassLoader(extClassLoader);
    // mysql jdbc 驱动实现时通过 SPI，线程上的类加载器加载的
    log.info("Mysql Driver.class.getClassLoader() = " + Driver.class.getClassLoader());
    
    // java.sql.Driver 使用的是系统启动类加载器
    log.info("java.sql.Driver.class.getClassLoader() = " + java.sql.Driver.class.getClassLoader());
    // DriverManager 驱动管理器使用启动类加载器
    log.info("DriverManager.class.getClassLoader() = " + DriverManager.class.getClassLoader());
    
    // 使用自定义类加载器加载指定的类
    String classPath = "/Users/kaisai";
    MyClassLoader myClassLoader = new MyClassLoader(classPath);
    
    System.out.println("MyDriver.class.getClassLoader() = " + MyDriver.class.getClassLoader());
    
    // SPI 的思想就是，像 JDBC JNDI 这样在 rt.jar 包中的类，想要自动的加载它的实现类，而这些实现类是由第三方来实现的
    // 此时类的加载关系就变成了：启动类加载加载相关接口的实现了，那肯定是不能实现的，除非是用户端手动的把实现类注册到接口管理器中，
    // 解决的方法是通过 SPI 机制进行自动注册，它的内部是通过线程的类加载器（默认是 APP 加载器），这样的类加载就变成了：启动类加载器 -> APP 加载器加载了。
    MyDriver myDriver = new MyDriver();
    DriverManager.registerDriver(myDriver);
  
    // try {
    //   Class<?> userClass = myClassLoader.loadClass("com.kaige.advance.jvm.User");
    //   ClassLoader userClassClassLoader = userClass.getClassLoader();
    //   log.info("userClassClassLoader = " + userClassClassLoader);
    //
    //   // 创建实例
    //   Object instance = userClass.newInstance();
    //   // 调用目标 sout 方法
    //   Method method = userClass.getDeclaredMethod("sout");
    //   method.invoke(instance);
    //
    // } catch (Exception e) {
    //   e.printStackTrace();
    // }
    
    try {
      Class<?> aClass = myClassLoader.loadClass("java.lang.String");
      log.info("aClass = " + aClass);
    } catch (Exception e) {
      System.out.println("e.getMessage() = " + e.getMessage());
      // e.printStackTrace();
    }
    
  }
  
  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    synchronized (getClassLoadingLock(name)) {
      // First, check if the class has already been loaded
      // 先判断类是否已经被加载
      Class<?> c = findLoadedClass(name);
      if (c == null) {
        long t0 = System.nanoTime();
        // 这里打破双亲委派机制，不在调用父类加载器加载类
        long t1 = System.nanoTime();
        // 直接加在方法
        c = findClass(name);
        
        // this is the defining class loader; record the stats
        // sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
        // sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
        // sun.misc.PerfCounter.getFindClasses().increment();
      }
      if (resolve) {
        resolveClass(c);
      }
      return c;
    }
  }
  
  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    try {
      // 加载类文件到内存
      // 在 classPath 末尾添加一个 '/' 字符
      if (!classPath.endsWith("/")) {
        classPath += "/";
      }
      // 去除 name 开头的 '/' 字符，将 '.' 替换为 '/'
      String path = classPath + name.replace('.', '/').concat(".class");
      log.info("path = " + path);
      FileInputStream fis = new FileInputStream(path);
      byte[] bytes = new byte[fis.available()];
      fis.read(bytes);
      fis.close();
      // 解析类
      return super.defineClass(name, bytes, 0, bytes.length);
    } catch (Exception e) {
      throw new ClassNotFoundException("自定义类加载器无法加载指定的类", e);
    }
  }
  
}
