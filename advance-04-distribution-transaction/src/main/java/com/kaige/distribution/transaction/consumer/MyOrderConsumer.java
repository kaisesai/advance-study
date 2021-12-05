package com.kaige.distribution.transaction.consumer;

import com.alibaba.fastjson.JSON;
import com.kaige.distribution.transaction.constant.MyOrderStateEnum;
import com.kaige.distribution.transaction.entity.MyOrder;
import com.kaige.distribution.transaction.service.MyOrderService;
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

  @Resource private MyOrderService myOrderService;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void onMessage(MessageExt message) {
    String body = new String(message.getBody());
    log.info("收到订单消息:{}, body: {}", message, body);
    // 解析为订单数据
    MyOrder myOrder = null;
    try {
      myOrder = JSON.parseObject(body, MyOrder.class);
    } catch (Exception e) {
      log.error("解析订单消息失败，body: {}", body, e);
    }

    if (Objects.isNull(myOrder)) {
      log.error("订单消息 body 解析结果为空，body: {}", body);
      return;
    }

    // 更新订单状态为已完成
    boolean result =
        myOrderService
            .lambdaUpdate()
            .eq(MyOrder::getId, myOrder.getId())
            .eq(MyOrder::getState, MyOrderStateEnum.CREATE.getCode())
            .set(MyOrder::getState, MyOrderStateEnum.PAID.getCode())
            .update();
    log.info("更新订单:{} 的状态为已完成，结果为：{}", myOrder.getId(), result);
  }
}
