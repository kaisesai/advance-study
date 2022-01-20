package com.kaige.distribution.transaction.pay.aop;

import com.kaige.distribution.transaction.pay.annotation.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;

@Aspect
@Component
@Slf4j
public class LockAop {

  @Autowired private RedisLockRegistry redisLockRegistry;

  @Pointcut("@annotation(com.kaige.distribution.transaction.pay.annotation.DistributedLock)")
  private void apiAop() {}

  @Around("apiAop()")
  public Object aroundApi(ProceedingJoinPoint point) throws Throwable {
    MethodSignature signature = (MethodSignature) point.getSignature();
    Method method = signature.getMethod();
    Object[] args = point.getArgs();
    DistributedLock lockDistributed = method.getAnnotation(DistributedLock.class);

    String lockKey = signature.getName() + "_" + lockDistributed.value() + "_" + args[0];
    Lock lock = redisLockRegistry.obtain(lockKey);
    boolean b = false;
    for (int i = 0; i < 3; i++) {
      // b = lock.tryLock(lockDistributed.waitTime(), TimeUnit.SECONDS);
      b = lock.tryLock();
      if (b) {
        break;
      }
    }

    if (!b) {
      log.info("获取分布式锁失败，key:{}", lockKey);
      throw new RuntimeException("获取分布式锁失败");
    }

    try {

      log.info("获取分布式锁成功，key:{}, 结果:{}", lockKey, b);
      return point.proceed();
    } finally {
      try {
        lock.unlock();
      } catch (Exception e) {
        log.error("释放分布式锁异常，key:{},", lockKey, e);
      }
    }
  }
}
