package com.kaige.distribution.transaction.pay.service;

import com.kaige.distribution.transaction.pay.entity.PayInfo;

public interface RedisService {

  boolean savePay(PayInfo payInfo);
}
