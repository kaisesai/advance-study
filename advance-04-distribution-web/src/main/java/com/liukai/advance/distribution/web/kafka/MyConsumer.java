package com.liukai.advance.distribution.web.kafka;

import com.liukai.advance.distribution.web.controller.KafkaController;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyConsumer {
  
  /**
   * @KafkaListener(groupId = "testGroup", topicPartitions = {
   * @TopicPartition(topic = "topic1", partitions = {"0", "1"}),
   * @TopicPartition(topic = "topic2", partitions = "0",
   * partitionOffsets = @PartitionOffset(partition = "1", initialOffset = "100"))
   * },concurrency = "6")
   * // concurrency就是同组下的消费者个数，就是并发消费数，必须小于等于分区总数
   */
  @KafkaListener(topics = KafkaController.TOPIC_NAME, groupId = "mygroup1", id = "mygroup-listen",
                 clientIdPrefix = "mygroup-client")
  public void listenMyGroup(ConsumerRecord<String, String> record, Acknowledgment ack) {
    log.info(record.toString());
    // log.info(record.value());
    //手动提交offset
    ack.acknowledge();
  }
  
}