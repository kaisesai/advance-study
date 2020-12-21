package com.liukai.advance.distribution.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping(value = "/kafka")
@RestController
public class KafkaController {
  
  public static final String TOPIC_NAME = "mytopic";
  
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  
  @RequestMapping(value = "/send")
  public String send(@RequestParam(value = "key") String key,
                     @RequestParam(value = "value") String value) {
    log.info("发送 kafka 消息：key=" + key + ", value=" + value);
    kafkaTemplate.send(TOPIC_NAME, key, value);
    return "ok";
  }
  
}
