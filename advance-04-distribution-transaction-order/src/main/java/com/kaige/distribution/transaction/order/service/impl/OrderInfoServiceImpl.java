package com.kaige.distribution.transaction.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaige.distribution.transaction.order.constant.EventStateEnum;
import com.kaige.distribution.transaction.order.constant.EventTypeEnum;
import com.kaige.distribution.transaction.order.dao.OrderInfoDao;
import com.kaige.distribution.transaction.order.entity.EventData;
import com.kaige.distribution.transaction.order.entity.OrderInfo;
import com.kaige.distribution.transaction.order.service.EventDataService;
import com.kaige.distribution.transaction.order.service.OrderInfoService;
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
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoDao, OrderInfo>
    implements OrderInfoService {

  @Resource private EventDataService eventDataService;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean createOrder(OrderInfo orderInfo) {
    // 保存订单
    boolean result = super.save(orderInfo);
    if (!result) {
      throw new IllegalStateException("创建订单异常");
    }
    String eventContent = JSON.toJSONString(orderInfo);

    // 保存事件表
    EventData eventData = new EventData();
    eventData.setId(EventTypeEnum.ORDER_CREATE + "_" + orderInfo.getId());
    eventData.setState(EventStateEnum.CREATE.getCode());
    eventData.setType(EventTypeEnum.ORDER_CREATE.getCode());
    eventData.setContent(eventContent);
    log.info("创建订单成功，保存事件：{}", JSON.toJSONString(eventData));
    return eventDataService.save(eventData);
  }
}
