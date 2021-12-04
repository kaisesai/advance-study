package com.kaige.distribution.transaction.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaige.distribution.transaction.constant.EventStateEnum;
import com.kaige.distribution.transaction.constant.EventTypeEnum;
import com.kaige.distribution.transaction.dao.OrderDao;
import com.kaige.distribution.transaction.entity.EventData;
import com.kaige.distribution.transaction.entity.MyOrder;
import com.kaige.distribution.transaction.service.EventDataService;
import com.kaige.distribution.transaction.service.MyOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * (Order)表服务实现类
 *
 * @author kaige
 * @since 2021-12-04 22:28:31
 */
@Slf4j
@Service("orderService")
public class MyOrderServiceImpl extends ServiceImpl<OrderDao, MyOrder> implements MyOrderService {

  @Resource private EventDataService eventDataService;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean createOrder(MyOrder myOrder) {
    // 保存订单
    boolean result = super.save(myOrder);
    if (!result) {
      throw new IllegalStateException("创建订单异常");
    }
    String eventContent = JSON.toJSONString(myOrder);

    // 保存事件表
    EventData eventData = new EventData();
    eventData.setId(EventTypeEnum.ORDER_CREATE + "_" + myOrder.getId());
    eventData.setState(EventStateEnum.CREATE.getCode());
    eventData.setType(EventTypeEnum.ORDER_CREATE.getCode());
    eventData.setContent(eventContent);
    log.info("创建订单成功，保存事件：{}", JSON.toJSONString(eventData));
    return eventDataService.save(eventData);
  }
}
