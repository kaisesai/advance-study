package com.kaige.distribution.transaction.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kaige.distribution.transaction.order.entity.OrderInfo;

/**
 * (Order)表服务接口
 *
 * @author kaige
 * @since 2021-12-04 22:28:31
 */
public interface OrderInfoService extends IService<OrderInfo> {

  boolean createOrderForMQ(OrderInfo orderInfo);

  boolean createOrderForLCN(OrderInfo orderInfo);
}
