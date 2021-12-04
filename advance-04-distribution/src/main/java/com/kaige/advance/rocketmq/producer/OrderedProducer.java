package com.kaige.advance.rocketmq.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;

public class OrderedProducer {

  public static void main(String[] args) throws Exception {
    // Instantiate with a producer group name.
    DefaultMQProducer producer = new DefaultMQProducer("mygroup");
    // Launch the instance.
    producer.setNamesrvAddr("localhost:9876");
    producer.start();
    String[] tags = new String[] {"TagA", "TagB", "TagC", "TagD", "TagE"};
    for (int i = 0; i < 100; i++) {
      int orderId = i % 10;
      // Create a message instance, specifying topic, tag and message body.
      Message msg =
          new Message(
              "TopicTestjjj",
              tags[i % tags.length],
              "KEY" + i,
              ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
      SendResult sendResult =
          producer.send(
              msg,
              new MessageQueueSelector() {
                @Override
                public MessageQueue select(
                    List<MessageQueue> messageQueueList, Message message, Object arg) {
                  // 消息 id，就是上面的 orderId
                  Integer id = (Integer) arg;
                  // id 与消息队列的取模
                  int index = id % messageQueueList.size();
                  // 根据取模数来选择队列
                  return messageQueueList.get(index);
                }
              },
              orderId);

      System.out.printf("%s%n", sendResult);
    }
    // server shutdown
    producer.shutdown();
  }
}
