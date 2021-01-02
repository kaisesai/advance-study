// package com.kaige.advance.concurrence;
//
// import sun.misc.Unsafe;
//
// import java.lang.reflect.Field;
//
// /**
//  * 原子的字段更新器
//  */
// public class MyAtomicObjUpdater {
//
//   private static final Unsafe UNSAFE = getUnsafe();
//
//   private static final long OFFSET;
//
//   static {
//     try {
//       // 通过 Unsafe 类的 objectFieldOffset 方法获取 age 字段的偏移量
//       OFFSET = UNSAFE.objectFieldOffset(MyAtomicObjUpdater.class.getDeclaredField("age"));
//     } catch (NoSuchFieldException e) {
//       throw new Error(e);
//     }
//   }
//
//   private volatile int age;
//
//   public MyAtomicObjUpdater(int age) {
//     this.age = age;
//   }
//
//   /**
//    * @return 通过反射获取 Unsafe 的实例
//    */
//   private static Unsafe getUnsafe() {
//     try {
//       Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
//       theUnsafe.setAccessible(true);
//       return (Unsafe) theUnsafe.get(null);
//     } catch (Exception e) {
//       throw new Error(e);
//     }
//
//   }
//
//   public int getAge() {
//     return age;
//   }
//
//   public int incrementAndGet() {
//     int oldValue;
//     do {
//       // 获取旧值
//       oldValue = UNSAFE.getIntVolatile(this, OFFSET);
//       // 设置新值
//     }
//     while (!UNSAFE.compareAndSwapInt(this, OFFSET, oldValue, oldValue + 1));
//     return oldValue + 1;
//   }
//
// }
