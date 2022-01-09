package com.kaige.distribution.transaction.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.kaige.distribution.transaction.order.constant.EventStateEnum;
import com.kaige.distribution.transaction.order.constant.EventTypeEnum;
import com.kaige.distribution.transaction.order.dao.OrderInfoDao;
import com.kaige.distribution.transaction.order.entity.EventData;
import com.kaige.distribution.transaction.order.entity.OrderInfo;
import com.kaige.distribution.transaction.order.service.EventDataService;
import com.kaige.distribution.transaction.order.service.OrderInfoService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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

  @Resource private RestTemplate restTemplate;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean createOrderForMQ(OrderInfo orderInfo) {
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

  @LcnTransaction
  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean createOrderForLCN(OrderInfo orderInfo) {

    log.info("订单数据：{}", orderInfo);
    // 保存订单
    boolean result = super.save(orderInfo);
    if (!result) {
      throw new IllegalStateException("创建订单异常");
    }

    JSONObject payInfo = new JSONObject();
    payInfo.put("amount", orderInfo.getAmount());
    payInfo.put("orderId", orderInfo.getId());
    payInfo.put("state", 1);

    log.info("调用支付服务，请求参数:{}", payInfo);
    // 调用支付服务
    ResponseEntity<R> rResponseEntity =
        restTemplate.postForEntity("http://MYPAY/payInfo/insert", payInfo, R.class);
    log.info("调用支付服务成功，返回值:{}", rResponseEntity.getBody());

    // 支付服务的 TCC 模式
    rResponseEntity =
        restTemplate.postForEntity("http://MYPAY/payInfo/insertToRedis", payInfo, R.class);
    log.info("调用支付服务缓存成功，返回值:{}", rResponseEntity.getBody());

    // 模拟异常
    int i = 1 / 0;
    return true;
  }

  @GlobalTransactional(rollbackFor = Exception.class)
  @Override
  public boolean createOrderForSeata(OrderInfo orderInfo) {
    log.info("订单数据：{}", orderInfo);
    // 保存订单
    boolean result = super.save(orderInfo);
    if (!result) {
      throw new IllegalStateException("创建订单异常");
    }

    JSONObject payInfo = new JSONObject();
    payInfo.put("amount", orderInfo.getAmount());
    payInfo.put("orderId", orderInfo.getId());
    payInfo.put("state", 1);

    log.info("调用支付服务，请求参数:{}", payInfo);
    // 调用支付服务
    ResponseEntity<R> rResponseEntity =
        restTemplate.postForEntity("http://MYPAY/payInfo/insert", payInfo, R.class);
    log.info("调用支付服务成功，返回值:{}", rResponseEntity.getBody());

    // 支付服务的 TCC 模式
    rResponseEntity =
        restTemplate.postForEntity("http://MYPAY/payInfo/insertToRedis", payInfo, R.class);
    log.info("调用支付服务缓存成功，返回值:{}", rResponseEntity.getBody());

    // 模拟异常
    int i = 1 / 0;
    return true;
  }
}
