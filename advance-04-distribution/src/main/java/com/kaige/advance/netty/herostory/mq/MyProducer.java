package com.kaige.advance.netty.herostory.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.Objects;

/**
 * 生产者
 */
@Slf4j
public class MyProducer {
  
  /**
   * 消息生产者
   */
  private static DefaultMQProducer producer;
  
  public static void main(String[] args) throws Exception {
    //Instantiate with a producer group name.
    DefaultMQProducer producer = new DefaultMQProducer("herostory-producer-group");
    // Specify name server addresses.
    producer.setNamesrvAddr("www.kaige.com:9876");
    producer.setSendMsgTimeout(600000);
    //Launch the instance.
    producer.start();
    for (int i = 0; i < 10; i++) {
      //Create a message instance, specifying topic, tag and message body.
      Message msg = new Message("topic-1" /* Topic */, "tag-a" /* Tag */, ("Hello RocketMQ " + i)
        .getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */);
      //Call send message to deliver message to one of brokers.
      SendResult sendResult = producer.send(msg);
      System.out.printf("%s%n", sendResult);
    }
    //Shut down once the producer instance is not longer in use.
    producer.shutdown();
  }
  
  /**
   * 发送消息
   *
   * @param topic   主题
   * @param msgBody 消息
   * @return 发送结果
   */
  public static SendResult sendMessage(String topic, byte[] msgBody) {
    if (Objects.isNull(producer)) {
      return null;
    }
    try {
      // Message msg = new Message("topic-herostory", "tagA", msgBody);
      Message msg = new Message(topic, "tagA", msgBody);
      return producer.send(msg);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }
  
  /**
   * 初始化消息生产者
   */
  public static void init() {
    if (Objects.nonNull(producer)) {
      return;
    }
    try {
      producer = new DefaultMQProducer("herostory-producer-group");
      producer.setNamesrvAddr("www.kaige.com:9876");
      producer.setSendMsgTimeout(6000);
      producer.start();
      producer.setRetryTimesWhenSendAsyncFailed(3);
      log.error("rocketmq producer started...");
    } catch (MQClientException e) {
      throw new IllegalStateException("rocketmq producer start fail", e);
    }
  }
  
}
