package com.kaige.distribution.transaction.pay.service.impl;

import com.codingapi.txlcn.tc.annotation.DTXPropagation;
import com.codingapi.txlcn.tc.annotation.TccTransaction;
import com.kaige.distribution.transaction.pay.entity.PayInfo;
import com.kaige.distribution.transaction.pay.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

  private final RedisTemplate<String, Object> redisTemplate;

  private final BoundHashOperations<String, String, Object> hashOps;

  public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    hashOps = redisTemplate.boundHashOps("payinfo");
  }

  /**
   * TCC 的尝试方法
   *
   * @param payInfo
   */
  @TccTransaction(propagation = DTXPropagation.SUPPORTS)
  @Override
  // @Transactional
  public boolean savePay(PayInfo payInfo) {
    log.info("savePay to redis start, payInfo:{}", payInfo);
    hashOps.put(String.valueOf(payInfo.getOrderId()), payInfo);
    log.info("savePay to redis end");
    return true;
  }

  /**
   * tcc 的确认方法
   *
   * @param payInfo
   */
  public void confirmSavePay(PayInfo payInfo) {
    log.info("执行 tcc confirm savePay, payInfo:{}", payInfo);
  }

  /**
   * tcc 的取消方法
   *
   * @param payInfo
   */
  public void cancelSavePay(PayInfo payInfo) {
    log.info("执行 tcc cancel savePay, payInfo:{}", payInfo);
    Long result = hashOps.delete(String.valueOf(payInfo.getOrderId()));
    log.info("执行 tcc cancel savePay result: {}", result);
  }
}
