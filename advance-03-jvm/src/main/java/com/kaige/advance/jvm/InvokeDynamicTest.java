package com.kaige.advance.jvm;

/**
 * 测试动态指令执行
 *
 * @author liukai 2021年07月18日
 */
public class InvokeDynamicTest {

  public static void main(String[] args) {
    I i0 = C::n;
    System.out.println("i = " + i0);
    I i1 = C::n;
    System.out.println("i1 = " + i1);
    I i2 = C::n;
    System.out.println("i2 = " + i2);
    I i3 = () -> {};
    System.out.println("i3 = " + i3);

    I i4 =
        () -> {
          C.n();
        };
    System.out.println("i4 = " + i4);

    // 实例类的内部类
    C1 c1 = new InvokeDynamicTest().new C1();
    System.out.println("c1 = " + c1);

    /*
    i = com.kaige.advance.jvm.InvokeDynamicTest$$Lambda$14/0x0000000800066c40@26f67b76
    i1 = com.kaige.advance.jvm.InvokeDynamicTest$$Lambda$17/0x000000080009e840@7f560810
    i2 = com.kaige.advance.jvm.InvokeDynamicTest$$Lambda$18/0x000000080009ec40@13a57a3b
    i3 = com.kaige.advance.jvm.InvokeDynamicTest$1@337d0578
    i4 = com.kaige.advance.jvm.InvokeDynamicTest$$Lambda$19/0x000000080009dc40@6276ae34
    i5 = com.kaige.advance.jvm.InvokeDynamicTest$$Lambda$20/0x000000080009d040@3c09711b
    */
  }

  public interface I {

    void m();
  }

  public static class C {

    static void n() {
      System.out.println("hello");
    }
  }

  public class C1 {
    void n() {
      System.out.println("hello");
    }
  }
}
