package com.kaige.advance.rabbitmq;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 消费者
 */
@Slf4j
public class Consumer {
  
  public static void main(String[] args) throws InterruptedException {
    
    // 1. 创建连接工厂
    ConnectionFactory connectionFactory = Producer.getConnectionFactory();
    
    // 3. 创建一个连接
    try (Connection connection = connectionFactory.newConnection()) {
      // 4. 创建一个 channel
      try (Channel channel = connection.createChannel()) {
        // 5. 创建一个消费者
        DeliverCallback deliverCallback = getDeliverCallback(channel);
        CancelCallback cancelCallback = getCancelCallback();
        
        // String queue = Producer.MY_TOPIC_QUEUE;
        // String queue = Producer.MY_FANOUT_QUEUE_1;
        String queue = Producer.MY_DIRECT_QUEUE;
        
        // 消息限流
        limitMsg(channel);
        
        // 消费消息，关闭自动提交 ack，即手动提交 ack
        channel.basicConsume(queue, false, deliverCallback, cancelCallback);
        
        // 阻塞消费
        // 检查队列中是否有消息，没有消息就休眠 1秒
        while (true) {
          
          if (channel.consumerCount(queue) == 0) {
            System.out.println("队列 " + queue + " 没有消息，等待生产者生产消息...");
            log.info("队列:{} 没有消息，等待生产者生产消息...", queue);
            Thread.sleep(5 * 1000);
          }
        }
      }
    } catch (TimeoutException | IOException e) {
      e.printStackTrace();
    }
    
  }
  
  /**
   * 消费端限流量：消费端启动时，如果有大量的消息进来，此时消费端可能不能处理这么多的消息，就会导致消费单出现巨大的压力。
   * <p>
   * 解决方案：
   * rabbitmq 提供一个 qos（服务质量保证），就是在关闭了消费端的自动 ack 的前提下，通过设置阈值（出队）的消息数
   * 没有被确认（手动确认），那么就不会推送消息进来。
   * <p>
   * 限流的级别（consumer 级别或者是 channel 级别）
   *
   * @param channel
   */
  private static void limitMsg(Channel channel) throws IOException {
    /**
     * prefetchSize: 设置消息的大小（rabbitmq 没有该功能，一般填写 0）
     * prefetchCount: 设置消息的阈值，每次过来几条消息（一般填写 1，即一条一条的处理）
     * global: 表示 channel 级别还是 consumer 级别。（rabbitmq 没有 channel 级别的限制）
     */
    channel.basicQos(0, 1, false);
    
  }
  
  private static CancelCallback getCancelCallback() {
    return consumerTag -> {
      log.info("cancel msg，tag:{}", consumerTag);
    };
  }
  
  private static DeliverCallback getDeliverCallback(Channel channel) {
    return (consumerTag, message) -> {
      // 获取消息体
      String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
      // 获取消息属性
      // AMQP.BasicProperties properties = message.getProperties();
      // log.info("deliver msgBody:{}, properties:{}, header:{}, tag:{}", messageBody, properties,
      //          properties.getHeaders(), consumerTag);
      // 模拟收到消息后的业务处理情况
      if ((RandomUtils.nextInt() % 2) == 0) {
        // 业务处理成功，则发送 ack
        // 发送 ack， 表示消费成功，手动设置 ack
        log.info("开始消费消息，处理成功 msgBody:{}, tag:{}", messageBody, consumerTag);
        channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
      } else {
        // 业务处理失败，则发送 nack，requeue 参数表示是否重回队列
        log.info("开始消费消息，处理失败 msgBody:{}, tag:{}", messageBody, consumerTag);
        // channel.basicNack(message.getEnvelope().getDeliveryTag(), false, true);
        channel.basicNack(message.getEnvelope().getDeliveryTag(), false, false);
      }
      
    };
  }
  
}
