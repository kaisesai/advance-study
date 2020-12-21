package com.liukai.advance.kafka;

import com.google.common.collect.Lists;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * 消息消费者
 */
public class MsgConsumer {
  
  public static void main(String[] args) {
    Properties props = new Properties();
    // 配置 kafka 服务端地址
    props
      .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
    // 消费分组名
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "mygroup1");
    // 是否自动提交 offset，默认是 true
    // props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    // 自动提交 offset 的间隔时间
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
    /*
      当消费主题的是一个新的消费组，或者指定 offset 的消费方式，offset 不存在，那么应该如何消费？
      latest（默认）：只消费自己启动之后发送到主题的消息
      earliest：第一次从头开始消费，以后按照消费 offset 记录继续消费，这个需要区别于 consumer.seekToBeginning（每次都从头开始消费）
     */
    // props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    // consumer 给 broker 发送心跳的间隔时间，broker 接收到心跳，如果此时有 rebalance 发生，会通过心跳响应将 rebalance 方案下发给 consumer，这个时间可以稍微短一点
    props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);
    // 服务端 broker 多久感知不到一个 consumer 心跳就认为它故障了，会将其提出消费组，对应的 partition 也会被重新分配给其他 consumer。默认是 10 秒
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10 * 1000);
    // 一次 poll 最大拉取消息的条数，如果消费者处理速度很快，可以设置大点，如果处理速度一般，可以设置小点
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
    // 如果两次 poll 操作间隔超过了这个时间，broker 就会认为这个 consumer 处理能力太弱，会将其提出消费组，将分区分配给别的 consumer 消费
    props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 30 * 1000);
    // key 序列化方式
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    // value 序列化方式
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    
    // 订阅一个主题
    consumer.subscribe(Lists.newArrayList(MsgProducer.TOPIC_MYTOPIC));
    
    // 指定消息分区
    // consumer.assign(Lists.newArrayList(new TopicPartition(MsgProducer.TOPIC_MYTOPIC, 0)));
  
    /*
    // 消息回溯消费
    consumer.assign(Lists.newArrayList(new TopicPartition(MsgProducer.TOPIC_MYTOPIC, 0)));
    consumer.seekToBeginning(Lists.newArrayList(new TopicPartition(MsgProducer.TOPIC_MYTOPIC, 0)));
    */
    
    /*
    // 指定 offset 消费
    consumer.assign(Lists.newArrayList(new TopicPartition(MsgProducer.TOPIC_MYTOPIC, 0)));
    consumer.seek(new TopicPartition(MsgProducer.TOPIC_MYTOPIC, 0), 100);
    */
    
    /*
    // 从指定时间点开始消费
    // 获取 topic 的分区信息
    List<PartitionInfo> partitionInfos = consumer.partitionsFor(MsgProducer.TOPIC_MYTOPIC);
    // 从 1 小时以前开始消费
    long fetchDateTime = new Date().getTime() - 1000 * 60 * 60;
    
    Map<TopicPartition, Long> map = partitionInfos.stream().collect(Collectors.toMap(
      partitionInfo -> new TopicPartition(partitionInfo.topic(), partitionInfo.partition()),
      partitionInfo -> fetchDateTime));
    
    // 获取 1 小时以前的 topic 分区和时间信息
    Map<TopicPartition, OffsetAndTimestamp> partMap = consumer.offsetsForTimes(map);
    
    // 过滤 TopicPartition
    List<TopicPartition> availableTopicPartitions = partMap.entrySet().stream().filter(entry -> {
      if (entry.getKey() != null && entry.getValue() != null) {
        System.out
          .println("partition-" + entry.getKey().partition() + "|offset = " + entry.getValue());
      }
      return entry.getValue() != null;
    }).map(Map.Entry::getKey).collect(Collectors.toList());
    
    if (availableTopicPartitions.size() == 0) {
      System.out.println("该时间以前没有可用的 TopicPartition");
      return;
    }
    
    // 分配 TopicPartition
    consumer.assign(availableTopicPartitions);
    // 指定每个分区消费的 offset
    availableTopicPartitions.forEach(topicPartition -> {
      // 根据消费里的 timestamp 确定 offset
      consumer.seek(topicPartition, partMap.get(topicPartition).offset());
    });
    */
    while (true) {
      /*
        poll 拉取消息的长轮询
       */
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
      for (ConsumerRecord<String, String> record : records) {
        System.out
          .printf("收到消息：partition = %d, offset = %d, key = %s, value = %s%n", record.partition(),
                  record.offset(), record.key(), record.value());
      }
      
      // 手动异步提交 offset，当前线程提交 offset 不会阻塞，可以继续处理后面的程序逻辑
      consumer.commitAsync((offsets, exception) -> {
        if (exception != null) {
          System.err.println("Commit failed for " + offsets);
          System.err
            .println("Commit failed exception: " + Arrays.toString(exception.getStackTrace()));
        }
      });
    }
    
  }
  
}
