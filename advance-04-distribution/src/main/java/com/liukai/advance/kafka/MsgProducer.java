package com.liukai.advance.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 消息生产者
 */
public class MsgProducer {
  
  // public static final String TOPIC_MYTOPIC = "my-replicated-topic";
  public static final String TOPIC_MYTOPIC = "mytopic";
  
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    
    Properties props = new Properties();
    // 配置 kafka 服务端地址
    props
      .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
    /*
      acks=0：表示 producer 不需要等待任何 broker 确认收到消息的回复，就可以继续放松下一条消息。性能最高，但是最容易丢失消息
      acks=1：表示 producer 至少等待 leader 已经成功将数据写入本地 log，但是不需要等待所有的 follower 是否成功写入。
      就可以继续发送下一条消息。这种情况下，如有 follower 没有成功备份，而此时 leader 又挂掉，则消息丢失。
      acks=-1或 all：需要等待 min.insync.replicas（默认为 1，推荐配置大于配置 2）都成功写入日志，这种策略会保证只要有一个备份或者就不会丢失数据。
      这是最强的数据保证，一般除非是金融级别，或者跟钱打交道的场精才会有用到这种配置
     */
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    // 发送失败会重试，重试能保证消息发送的可靠性，但是也可能造成消息重复发送，比如网络抖动，所以需要在接收者那边做好消息接收幂等处理
    props.put(ProducerConfig.RETRIES_CONFIG, 3);
    // 重试间隔设置，默认重试间隔 100ms
    props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 300);
    // 设置发送消息的本地缓冲区，如果设置了该缓冲区，消息会发送到本地缓冲区，可以提高消息发送性能，默认是 33554432，即 32MB
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 32 * 1024 * 1024L);
    // kafka 本地线程会从缓冲区取数据，批量发送到 broker
    // 设置批量发送消息的大小，默认是 16KB，就是说一个 batch 满了 16KB 就发送出去
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
    /*
      默认为 0，表示消息必须立即发送，这样会影响性能，这样会影响性能。
      一般设置 10ms 左右，意思说这个消息发送完后会进入本地的一个 batch，如果 10ms 内，这个 batch 满了 16KB，就将消息发送出去
      如果 10ms 内，batch 没有填满，那么也必须要把消息发送出去，不能让消息的发送延迟时间边长
     */
    props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
    // 把发送的 key 从字符串序列化为字节数组
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    // props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
    // 把发送的 value 从字符串序列化为字节数组
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    
    int num = 10_000_000;
    CountDownLatch countDownLatch = new CountDownLatch(num);
    
    Producer<String, String> producer = new KafkaProducer<>(props);
    
    for (int i = 0; i < num; i++) {
      // 指定发送分区
      // ProducerRecord<Integer, Integer> record = new ProducerRecord<>(mytopic, 0, i, i);
      
      // 未指定分区，具体发送的分区计算公式：hash(key)%partitionNum
      ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_MYTOPIC, "key" + i,
                                                                   "value" + i);
      
      // 等待消息发送成功的同步阻塞方法
      // RecordMetadata metadata = producer.send(record).get();
      // System.out.println("同步发送消息结果，topic-" + metadata.topic() + "|partition-"
      //               + metadata.partition() + "|offset-" + metadata.offset());
      
      // 异步回调发送消息
      producer.send(record, (metadata, exception) -> {
        if (exception != null) {
          System.out
            .println(Thread.currentThread().getName() + " 发送消息失败：" + exception.getMessage());
        }
        
        if (metadata != null) {
          System.out.println(
            Thread.currentThread().getName() + " 异步发送消息结果，topic:" + metadata.topic() + "|partition:"
              + metadata.partition() + "|offset:" + metadata.offset());
        }
        countDownLatch.countDown();
      });
      
    }
    
    boolean await = countDownLatch.await(5, TimeUnit.MINUTES);
    System.out.println("await = " + await);
    
    producer.close();
    
  }
  
}
