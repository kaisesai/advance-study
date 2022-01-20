package com.kaige.distribution.transaction.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kaige.distribution.transaction.pay.entity.PayInfo;

/**
 * 支付信息表(PayInfo)表服务接口
 *
 * @author kaige
 * @since 2021-12-12 16:53:18
 */
public interface PayInfoService extends IService<PayInfo> {

  // boolean savePayInfo(PayInfo payInfo);

  PayInfo createPayInfo(Long orderId, PayInfo PayInfo);
}
