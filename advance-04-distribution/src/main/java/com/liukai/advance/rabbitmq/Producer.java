package com.liukai.advance.rabbitmq;

import com.google.common.collect.Maps;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 生产者
 */
@Slf4j
public class Producer {
  
  // 声明一个队列
  public static final String MY_QUEUE = "myqueue";
  
  public static final String MY_DIRECT_EXCHANGE = "my-direct-exchange";
  
  public static final String MY_DEADLINE_EXCHANGE = "my-deadline-exchange";
  
  public static final String MY_TOPIC_EXCHANGE = "my-topic-exchange";
  
  public static final String MY_FANOUT_EXCHANGE = "my-fanout-exchange";
  
  public static final String MY_DIRECT_QUEUE = "my-direct-queue";
  
  public static final String MY_TOPIC_QUEUE = "my-topic-queue";
  
  public static final String MY_TOPIC_QUEUE_1 = "my-topic-queue1";
  
  public static final String MY_FANOUT_QUEUE_1 = "my-fanout-queue1";
  
  public static final String MY_FANOUT_QUEUE_2 = "my-fanout-queue2";
  
  public static final String MY_DEADLINE_QUEUE = "my-deadline-queue";
  
  public static void main(String[] args) {
    
    ConnectionFactory connectionFactory = getConnectionFactory();
    
    // 3. 创建连接
    try (Connection connection = connectionFactory.newConnection()) {
      // 4. 通过连接创建 channel
      try (Channel channel = connection.createChannel()) {
        // 1. 交换机
        // 直接交换机
        // directExchangeProducer(channel);
        // topic 交换机
        // topicExchangeProducer(channel);
        // 扇形交换机
        // fanoutExchangeProducer(channel);
        // 直接交换机，自定义属性消息
        // exchangePropertiesProducer(channel);
        
        // 消息确认
        // messageConfirmProducer(channel);
        
        // 消息 return listener
        // returnListenerProducer(channel);
        
        // 发送消息，创建死信队列
        deadlineExchangeProducer(channel);
        
        Thread.sleep(1000);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    // 7. 关闭连接
  }
  
  /**
   * 死信队列
   *
   * @param channel
   * @throws IOException
   */
  private static void deadlineExchangeProducer(Channel channel) throws IOException {
    
    String deadlineExchange = MY_DEADLINE_EXCHANGE;
    
    // 创建正常交换机和队列
    String normalExchange = "my-normal-exchange";
    channel.exchangeDeclare(normalExchange, BuiltinExchangeType.TOPIC, true, false, false, null);
    
    // 创建正常交换机和队列
    String normalQueue = "my-normal-queue";
    channel.queueDeclare(normalQueue, true, false, false, null);
    
    // 绑定队列——设置正常队列中的死信发往哪个交换机（即死信交换机）
    String routingKey = "my-normal-routing-key";
    Map<String, Object> arguments = Maps.newHashMap();
    arguments.put("x-dead-letter-exchange", deadlineExchange);
    channel.queueBind(normalQueue, normalExchange, routingKey, arguments);
    
    // 创建死信队列交换机，死信队列有点问题，消息不能进入死信队列中
    AMQP.Exchange.DeclareOk declareOk = channel
      .exchangeDeclare(deadlineExchange, BuiltinExchangeType.TOPIC, true, false, false, null);
    log.info("declareOk = " + declareOk);
    // 创建死信队列
    AMQP.Queue.DeclareOk queueDeclare = channel
      .queueDeclare(MY_DEADLINE_QUEUE, true, false, false, null);
    
    log.info("queueDeclare = " + queueDeclare);
    // 绑定死信队列
    channel.queueBind(queueDeclare.getQueue(), deadlineExchange, "#");
    
    // 交换机绑定，这个是相当于从一个交换机上复制数据到目标交换机
    channel.exchangeBind(deadlineExchange, normalExchange, "#");
    
    // 5. 通过 channel 来发送消息
    int messageNum = 10;
    for (int i = 0; i < messageNum; i++) {
      //设置消息超时
      AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder()
        .expiration("10000").build();
      
      String message = "这是一个死信消息" + i;
      // 发布消息
      log
        .info("开始生产消息，exchange:{}, routingKey:{}, message:{}", normalExchange, routingKey, message);
      channel.basicPublish(normalExchange, routingKey, basicProperties,
                           message.getBytes(StandardCharsets.UTF_8));
    }
    // 6. 关闭通道
  }
  
  /**
   * return listener消息处理机制：用来处理一些不可路由的消息
   * <p>
   * 以下情况会出现不可被路由的情况：
   * 1. broker 中没有对应的 exchange 交换机
   * 2. 交换机根据路由 key 不能路由到某一个队列上
   * <p>
   * 解决：
   * 1. 在消息生产端设置 mandatory 为 true，那么就会调用生产端的 ReturnListener 来处理
   * 2. 消息生产端的 mandatory 为 false（默认值为 false），那么 broker 就会自动删除消息
   *
   * @param channel
   * @throws IOException
   */
  private static void returnListenerProducer(Channel channel) throws IOException {
    // 开启确认模式
    channel.confirmSelect();
    // 添加确认回调
    channel.addConfirmListener((deliveryTag, multiple) -> {
      log.info("消息 ack 回调！deliveryTag:{}, multiple:{}", deliveryTag, multiple);
    }, (deliveryTag, multiple) -> {
      log.info("消息 nack 回调 deliveryTag:{}, multiple:{}", deliveryTag, multiple);
    });
    
    // 添加 return listener
    channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
      log.info("不可达消息...replyCode:{}, replyText:{}, exchange:{}, routingKey:{}, properties:{}, "
                 + "body:{}", replyCode, replyText, exchange, routingKey, properties,
               new String(body));
    });
    
    String exchange = MY_DIRECT_EXCHANGE;
    // 可达路由 key
    String routingKey = "kaige-direct-queue";
    // 不可达路由 key
    String notDeliveryRoutingKey = "kaige-direct-queue1";
    // 创建一个带有额外信息的消息体
    String message = "hello-direct-properties";
    byte[] body = message.getBytes(StandardCharsets.UTF_8);
    // 可达消息
    channel.basicPublish(exchange, routingKey, false, null, body);
    // 不可达消息，调用 return listener，设置 mandatory 属性 为 true
    channel.basicPublish(exchange, notDeliveryRoutingKey, true, null, body);
    // 不可达消息，被 broker 自动删除
    channel.basicPublish(exchange, notDeliveryRoutingKey, false, null, body);
  }
  
  /**
   * 消息确认模式：生产端投递消息之成功之后，消息服务就会给生产者一个应答。
   * <p>
   * 该模式保障了消息的可靠性投递
   *
   * @param channel
   * @throws IOException
   */
  private static void messageConfirmProducer(Channel channel)
    throws IOException, InterruptedException {
    // 创建交换机
    String exchange = MY_DIRECT_EXCHANGE;
    // 路由 key
    String routingKey = "kaige-direct-queue";
    
    // 开启确认模式
    channel.confirmSelect();
    // 添加确认回调
    channel.addConfirmListener((deliveryTag, multiple) -> {
      log.info("消息 ack 回调！deliveryTag:{}, multiple:{}", deliveryTag, multiple);
    }, (deliveryTag, multiple) -> {
      log.info("消息 nack 回调 deliveryTag:{}, multiple:{}", deliveryTag, multiple);
    });
    
    // 创建一个带有额外信息的消息体
    String message = "hello-direct-properties";
    // 发布消息
    log.info("开始生产消息，exchange:{}, routingKey:{}, message:{}", exchange, routingKey, message);
    channel.basicPublish(exchange, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
  }
  
  /**
   * 创建自定义属性的消息
   *
   * @param channel
   * @throws IOException
   */
  private static void exchangePropertiesProducer(Channel channel) throws IOException {
    // 创建交换机
    String exchange = MY_DIRECT_EXCHANGE;
    AMQP.Exchange.DeclareOk declareOk = channel
      .exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, true, false, null);
    log.info("declareOk = " + declareOk);
    
    // 创建队列
    AMQP.Queue.DeclareOk queueDeclare = channel
      .queueDeclare(MY_DIRECT_QUEUE, true, false, false, null);
    
    // 路由 key
    String routingKey = "kaige-direct-queue";
    // 绑定队列
    channel.queueBind(queueDeclare.getQueue(), exchange, routingKey);
    
    // 5. 通过 channel 来发送消息
    HashMap<String, Object> map = Maps.newHashMap();
    map.put("prop1", "value1");
    map.put("prop2", "value2");
    
    // 创建一个带有额外信息的消息体
    AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder()
      .deliveryMode(2)// 发送类型：1 是非持久化，2 是持久化
      .appId("测试 appid").clusterId("测试集群 id").contentType("application/json")
      .contentEncoding("UTF-8").headers(map).build();
    
    String message = "hello-direct-properties";
    // 发布消息
    channel.basicPublish(exchange, routingKey, basicProperties,
                         message.getBytes(StandardCharsets.UTF_8));
    log.info("开始生产消息，exchange:{}, routingKey:{}, message:{}", exchange, routingKey, message);
  }
  
  /**
   * 创建扇形交换机
   * <p>
   * 消息通过从交换机到队列上不会通过路由 key，所以该模式的速度是最快的，只要和交换机绑定的那么消息就会被分发到与之绑定的队列上
   *
   * @param channel
   * @throws IOException
   */
  private static void fanoutExchangeProducer(Channel channel) throws IOException {
    // 创建扇形交换机
    String myFanoutExchange = MY_FANOUT_EXCHANGE;
    AMQP.Exchange.DeclareOk declareOk = channel
      .exchangeDeclare(myFanoutExchange, BuiltinExchangeType.FANOUT, true, false, null);
    log.info("createFanoutExchange {}, result:{}", myFanoutExchange, declareOk);
    
    // 创建队列
    AMQP.Queue.DeclareOk queueDeclare1 = channel
      .queueDeclare(MY_FANOUT_QUEUE_1, true, false, false, null);
    log.info("queueDeclare1 = " + queueDeclare1);
    AMQP.Queue.DeclareOk queueDeclare2 = channel
      .queueDeclare(MY_FANOUT_QUEUE_2, true, false, false, null);
    log.info("queueDeclare1 = " + queueDeclare2);
    
    // 路由 key，不用声明
    String routingKey1 = "routingKey1";
    String routingKey2 = "routingKey2";
    
    /*
     xxx.# 表示可以匹配多个单词，比如 log.# 可以匹配 log.a、log.b、log.a.b
     xxx.* 表示可以匹配一个单词，比如 log.* 可以匹配 log.a、log.b，但是不能匹配 log.a.b
     */
    // 绑定队列到扇形交换机
    channel.queueBind(queueDeclare1.getQueue(), myFanoutExchange, "");
    channel.queueBind(queueDeclare2.getQueue(), myFanoutExchange, "");
    
    // 5. 通过 channel 来发送消息
    String message = "hello-fanout";
    // 发布消息
    channel
      .basicPublish(myFanoutExchange, routingKey1, null, message.getBytes(StandardCharsets.UTF_8));
    log
      .info("开始生产消息，发送到扇形交换机，exchange:{}, routingKey:{}, message:{}", myFanoutExchange, routingKey1,
            message);
    channel
      .basicPublish(myFanoutExchange, routingKey2, null, message.getBytes(StandardCharsets.UTF_8));
    log
      .info("开始生产消息，发送到扇形交换机，exchange:{}, routingKey:{}, message:{}", myFanoutExchange, routingKey2,
            message);
  }
  // 6. 关闭通道
  
  /**
   * 创建 topic 交换机
   * <p>
   * 队列上绑定到 topic 交换机上的路由 key 可以是通过通配符来匹配的。
   * 规则为：
   * xxx.# 表示可以匹配多个单词，比如 log.# 可以匹配 log.a、log.b、log.a.b
   * xxx.* 表示可以匹配一个单词，比如 log.* 可以匹配 log.a、log.b，但是不能匹配 log.a.b
   *
   * @param channel
   * @throws IOException
   */
  private static void topicExchangeProducer(Channel channel) throws IOException {
    // 创建 topic 交换机
    // String exchange = "my-topic-exchange";
    AMQP.Exchange.DeclareOk declareOk = channel
      .exchangeDeclare(MY_TOPIC_EXCHANGE, BuiltinExchangeType.TOPIC, true, false, null);
    log.info("createTopicExchange {}, result:{}", MY_TOPIC_EXCHANGE, declareOk);
    
    // 创建一个队列
    AMQP.Queue.DeclareOk queueDeclare1 = channel
      .queueDeclare(MY_TOPIC_QUEUE, true, false, false, null);
    log.info("queueDeclare1 = " + queueDeclare1);
    AMQP.Queue.DeclareOk queueDeclare2 = channel
      .queueDeclare(MY_TOPIC_QUEUE_1, true, false, false, null);
    log.info("queueDeclare1 = " + queueDeclare2);
    
    // 路由 key
    String routingKey1 = "top.key";
    String routingKey2 = "news.key";
    
    /*
     xxx.# 表示可以匹配多个单词，比如 log.# 可以匹配 log.a、log.b、log.a.b
     xxx.* 表示可以匹配一个单词，比如 log.* 可以匹配 log.a、log.b，但是不能匹配 log.a.b
     */
    // 绑定队列
    channel.queueBind(queueDeclare1.getQueue(), MY_TOPIC_EXCHANGE, "top.key.#");
    channel.queueBind(queueDeclare2.getQueue(), MY_TOPIC_EXCHANGE, "#.key");
    
    // 5. 通过 channel 来发送消息
    int messageNum = 100;
    for (int i = 0; i < messageNum; i++) {
      String message = "hello-topic-" + i;
      // 发布消息
      channel.basicPublish(MY_TOPIC_EXCHANGE, routingKey1, null,
                           message.getBytes(StandardCharsets.UTF_8));
      log.info("开始生产消息，发送到 topic 交换机，exchange:{}, routingKey:{}, message:{}", MY_TOPIC_EXCHANGE,
               routingKey1, message);
      channel.basicPublish(MY_TOPIC_EXCHANGE, routingKey2, null,
                           message.getBytes(StandardCharsets.UTF_8));
      log.info("开始生产消息，发送到 topic 交换机，exchange:{}, routingKey:{}, message:{}", MY_TOPIC_EXCHANGE,
               routingKey2, message);
    }
    // 6. 关闭通道
  }
  
  /**
   * 直接交换机
   * 所有发送到直接交换机的消息都是会被投递到与路由 key 名称相同的队列上
   *
   * @param channel
   * @throws IOException
   */
  private static void directExchangeProducer(Channel channel) throws IOException {
    // 创建交换机
    String exchange = MY_DIRECT_EXCHANGE;
    AMQP.Exchange.DeclareOk declareOk = channel
      .exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, true, false, null);
    log.info("declareOk = " + declareOk);
  
    /*
      参数：
        queue：队列名称
        durable：是否持久化，队列的声明是存放在内存中的，如果重启 rabbitmq 对列举就会丢失
        exclusive：是否独占，当连接断开时候，该队列是否会自动删除，如果为 false，则其他消费者也可以访问同一个队列
          如果是 true，当前消费者会对当前队列加锁，其他的 channel 是不能访问的。如果为 true 的话，一个队列只能有一个消费者来消费的场景
        autoDelete：是否自动删除。当最后一个消费者断开连接之后，队列是否自动被删除。
     */
    AMQP.Queue.DeclareOk queueDeclare = channel
      .queueDeclare(MY_DIRECT_QUEUE, true, false, false, null);
    log.info("queueDeclare = " + queueDeclare);
    
    // 路由 key
    String routingKey = "kaige-queue-01";
    // 绑定队列
    channel.queueBind(queueDeclare.getQueue(), exchange, routingKey);
    
    // 5. 通过 channel 来发送消息
    int messageNum = 20;
    for (int i = 0; i < messageNum; i++) {
      String message = "hello-" + i;
      // 发布消息
      channel.basicPublish(exchange, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
      log.info("开始生产消息，exchange:{}, routingKey:{}, message:{}", exchange, routingKey, message);
    }
    // 6. 关闭通道
  }
  
  public static ConnectionFactory getConnectionFactory() {
    // 1. 创建连接工厂
    ConnectionFactory connectionFactory = new ConnectionFactory();
    
    // 2. 设置工厂属性
    connectionFactory.setHost("127.0.0.1");
    connectionFactory.setPort(5672);
    connectionFactory.setVirtualHost("kaige-virtual-host");
    connectionFactory.setUsername("kaige");
    connectionFactory.setPassword("kaige");
    return connectionFactory;
  }
  
}
