package com.kaige.advance.netty.herostory.mq;

import com.alibaba.fastjson.JSON;
import com.kaige.advance.netty.herostory.cmdhandler.UserAttkCmdHandler;
import com.kaige.advance.netty.herostory.rank.RankService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.Objects;

@Slf4j
public class MyConsumer {
  
  public static void init() {
    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("herostory-consumer-group");
    consumer.setNamesrvAddr("www.kaige.com:9876");
    
    try {
      // 订阅主题
      consumer.subscribe(UserAttkCmdHandler.HEROSTORY_VICTOR_TOPIC, "*");
      
      // 注册消息监听器
      consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
        for (MessageExt msg : msgs) {
          if (Objects.isNull(msg)) {
            continue;
          }
          // 解析消息
          VictorMsg victorMsg = JSON.parseObject(msg.getBody(), VictorMsg.class);
          if (Objects.isNull(victorMsg)) {
            continue;
          }
          
          log.info("从消息队列中收到胜利消息, winnerId = {}, loserId = {}", victorMsg.getWinnerId(),
                   victorMsg.getLoserId());
          // 更新用户排行版信息
          RankService.getInstance().refreshRank(victorMsg);
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
      });
      
      // 启动消费者
      consumer.start();
      log.info("rocketmq consumer started...");
    } catch (MQClientException e) {
      log.error(e.getErrorMessage(), e);
    }
    
  }
  
}
