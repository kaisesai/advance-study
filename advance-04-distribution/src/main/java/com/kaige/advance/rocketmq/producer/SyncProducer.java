package com.kaige.advance.rocketmq.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/** 同步发射器 */
@Slf4j
public class SyncProducer {

  public static void main(String[] args) throws Exception {
    // Instantiate with a producer group name.
    DefaultMQProducer producer = new DefaultMQProducer("producer_group_2");
    // Specify name server addresses.
    producer.setNamesrvAddr("localhost:9876");
    // Launch the instance.
    producer.start();
    for (int i = 0; i < 100; i++) {
      // Create a message instance, specifying topic, tag and message body.
      Message msg =
          new Message(
              "TopicTest" /* Topic */,
              "TagA" /* Tag */,
              ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */);
      // Call send message to deliver message to one of brokers.
      SendResult sendResult = producer.send(msg);
      log.info("sendResult:{}", sendResult);
      // TimeUnit.SECONDS.sleep(5);
    }
    // Shut down once the producer instance is not longer in use.
    producer.shutdown();
  }
}
