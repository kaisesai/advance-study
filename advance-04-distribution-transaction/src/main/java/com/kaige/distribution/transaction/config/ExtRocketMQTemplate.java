package com.kaige.distribution.transaction.config;

import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

@ExtRocketMQTemplateConfiguration(nameServer = "${my.rocketmq.extNameServer}")
public class ExtRocketMQTemplate extends RocketMQTemplate {}
