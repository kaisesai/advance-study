package com.kaige.distribution.transaction.task;

import com.kaige.distribution.transaction.constant.EventStateEnum;
import com.kaige.distribution.transaction.constant.EventTypeEnum;
import com.kaige.distribution.transaction.entity.EventData;
import com.kaige.distribution.transaction.service.EventDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MyTask {

  @Value("${my.rocketmq.orderTopic}")
  private String myOrderTopic;

  @Resource private RocketMQTemplate rocketMQTemplate;

  @Resource private EventDataService eventDataService;

  /** 发送订单消息 */
  @Transactional(rollbackFor = Exception.class)
  @Scheduled(fixedDelay = 5000)
  public void doSendOrderMsg() {

    // 查询状态为 1、业务类型为 100 的事件表数据
    List<EventData> eventDataList =
        eventDataService
            .lambdaQuery()
            .eq(EventData::getState, EventStateEnum.CREATE.getCode())
            .eq(EventData::getType, EventTypeEnum.ORDER_CREATE.getCode())
            .list();
    if (CollectionUtils.isEmpty(eventDataList)) {
      log.info("目前没有新建状态的事件数据");
      return;
    }

    List<String> eventDataIds =
        eventDataList.stream().map(EventData::getId).collect(Collectors.toList());

    // 更新事件表状态为 2 处理成功
    boolean result =
        eventDataService
            .lambdaUpdate()
            .set(EventData::getState, EventStateEnum.SUCCESS.getCode())
            .in(EventData::getId, eventDataIds)
            .update();
    log.info("更新事件表数据，eventDataIds: {}, 结果：{}", eventDataIds, result);

    // 发送mq消息
    List<Message<String>> messages = buildBatchMessageOrderly(eventDataList);

    // 构建目的地：myOrderTopic:ORDER_CREATE
    String destination = myOrderTopic + ":" + EventTypeEnum.ORDER_CREATE;

    SendResult sendResult = rocketMQTemplate.syncSend(destination, messages);
    log.info("发送事件消息成功！结果:{}", sendResult);
  }

  private List<Message<String>> buildBatchMessageOrderly(List<EventData> eventDataList) {

    // 根据事件表的业务类型作为 tag 来分组
    // Map<Integer, List<EventData>> eventTypeToDataMap =
    // eventDataList.stream().collect(Collectors.groupingBy(EventData::getType));
    // 构建有序的消息，前提条件是有相同的业务 key，不适合此场景

    List<Message<String>> msgs =
        eventDataList.stream()
            .map(
                eventData ->
                    MessageBuilder.withPayload(eventData.getContent())
                        .setHeader(RocketMQHeaders.KEYS, "KEY_" + eventData.getId())
                        // 这里设置 tag 属性不起作用，需要在 destination 上添加，格式为: `topicName:tags`
                        // .setHeader(RocketMQHeaders.TAGS, eventData.getType())
                        .build())
            .collect(Collectors.toList());
    log.info("构建的消息为: {}", msgs);
    return msgs;
  }
}
