package com.kaige.distribution.transaction.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaige.distribution.transaction.pay.annotation.DistributedLock;
import com.kaige.distribution.transaction.pay.dao.PayInfoDao;
import com.kaige.distribution.transaction.pay.entity.PayInfo;
import com.kaige.distribution.transaction.pay.service.PayInfoService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 支付信息表(PayInfo)表服务实现类
 *
 * @author kaige
 * @since 2021-12-12 16:53:20
 */
@Slf4j
@Service("payInfoService")
public class PayInfoServiceImpl extends ServiceImpl<PayInfoDao, PayInfo> implements PayInfoService {

  @SneakyThrows
  @DistributedLock(value = "payinfo")
  @Override
  public PayInfo createPayInfo(Long orderId, PayInfo payInfo) {
    log.info("开始执行 createPayInfo 方法，orderId:{}, PayInfo:{}", orderId, payInfo);
    TimeUnit.SECONDS.sleep(10);
    super.save(payInfo);
    log.info("结束执行 createPayInfo 方法，orderId:{}, PayInfo:{}", orderId, payInfo);
    return payInfo;
  }
}
