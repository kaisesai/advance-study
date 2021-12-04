package com.kaige.distribution.transaction.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kaige.distribution.transaction.entity.MyOrder;

/**
 * (Order)表服务接口
 *
 * @author kaige
 * @since 2021-12-04 22:28:31
 */
public interface MyOrderService extends IService<MyOrder> {

  boolean createOrder(MyOrder myOrder);
}
