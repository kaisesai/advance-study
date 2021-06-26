package com.concurrence.test;

import com.kaige.advance.concurrence.MyPS;
import org.openjdk.jmh.annotations.*;

/**
 * 测试 JMH
 */
@Warmup(iterations = 1)
@Measurement(iterations = 1)
@Timeout(time = 50)
@Threads(1)
@BenchmarkMode(Mode.All)
@Fork(value = 1)
public class JMHTest {
  
  @Benchmark
  public void testMyPS() {
    MyPS.foreach();
  }
  
  @Benchmark
  public void testMyPS1() {
    MyPS.parallel();
  }
  
}
