package com.kaige.distribution.transaction.pay.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kaige.distribution.transaction.pay.entity.PayInfo;
import com.kaige.distribution.transaction.pay.service.PayInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 订单消息消费者
 *
 * <p>Push模式
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "${my.rocketmq.orderTopic}",
    selectorExpression = "ORDER_CREATE",
    // selectorExpression = "*",
    consumerGroup = "my-consumer-group1")
public class MyOrderConsumer implements RocketMQListener<MessageExt> {

  @Resource private PayInfoService payInfoService;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void onMessage(MessageExt message) {
    String body = new String(message.getBody());
    log.info("收到订单消息:{}, body: {}", message, body);

    // 解析为订单数据
    JSONObject orderInfo = null;
    try {
      orderInfo = JSON.parseObject(body);
    } catch (Exception e) {
      log.error("解析订单消息失败，body: {}", body, e);
    }

    if (Objects.isNull(orderInfo)) {
      log.error("订单消息 body 解析结果为空，body: {}", body);
      return;
    }

    log.info("解析结果为：{}", orderInfo);

    // 创建支付信息
    PayInfo payInfo = new PayInfo();
    payInfo.setOrderId(orderInfo.getLong("id"));
    payInfo.setAmount(orderInfo.getLong("amount"));
    payInfo.setUserId(orderInfo.getLong("userId"));
    payInfo.setState(1);
    boolean save = payInfoService.save(payInfo);
    log.info("创建支付信息，payInfo:{}", payInfo);

    // 更新订单状态为已完成
    // boolean result =
    //     orderInfoService
    //         .lambdaUpdate()
    //         .eq(OrderInfo::getId, orderInfo.getId())
    //         .eq(OrderInfo::getState, MyOrderStateEnum.CREATE.getCode())
    //         .set(OrderInfo::getState, MyOrderStateEnum.PAID.getCode())
    //         .update();
    // log.info("更新订单:{} 的状态为已完成，结果为：{}", orderInfo.getId(), result);

  }
}
