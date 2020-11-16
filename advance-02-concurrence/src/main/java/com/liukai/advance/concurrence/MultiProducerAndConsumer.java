package com.liukai.advance.concurrence;

import java.util.List;
import java.util.concurrent.*;

/**
 * 多生产者和多消费者模式
 */
public class MultiProducerAndConsumer {
  
  public static void main(String[] args) throws InterruptedException {
    MsgQueueManger messagesQueue = MsgQueueFactory.getMessagesQueue();
    
    // 生产者创建消息
    for (int i = 0; i < 1; i++) {
      int finalI = i;
      new Thread(() -> {
        for (int j = 0; j < 10; j++) {
          try {
            messagesQueue.put(new Message("消息" + finalI + "_M_" + j));
            Thread.sleep(1);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }, "生产者" + i).start();
    }
    
    // 消费者分发线程，分发消息到其他消费者队列
    new Thread(new DispatchMessageTask(), "分发者").start();
    
    // 睡眠一段时间之后，再开始创建消费者
    Thread.sleep(3000);
    
    Session session = Session.getInstance();
    // 创建多个消费者队列，并且创建三个消费者来进行消费
    for (int i = 0; i < 2; i++) {
      session.addSubMsgQueue(new LinkedBlockingQueue<>());
      
      new Thread(() -> {
        while (true) {
          try {
            // 这里使用 poll 超时获取消息，而不是使用 take() 是为了防止消费者线程每次都可能随机获取到空的队列，而导致一直被阻塞，从而无法获取到有数据的队列
            Message message = session.getSubQueue().poll(3, TimeUnit.SECONDS);
            if (message == null) {
              System.out.println(Thread.currentThread().getName() + " 消费消息为空");
              continue;
            }
            System.out.println(Thread.currentThread().getName() + " 消费消息，消息：" + message.data);
            
            // 模拟错误的数据
            if (message.data.endsWith("_M_1")) {
              System.out.println(Thread.currentThread().getName() + " 消费失败，回溯消息：" + message.data);
              // 修复数据
              Message newMessage = new Message(message.data + "FIX");
              messagesQueue.put(newMessage);
            }
            
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }, "消费者" + i).start();
    }
    
    Thread.sleep(5000);
    new Thread(() -> {
      for (; ; ) {
        int i = 0;
        for (BlockingQueue<Message> subMsgQueue : session.subMsgQueues) {
          
          System.out.println("获取到子队列，index：" + i++ + "队列内容：" + subMsgQueue);
        }
        System.out.println("===============华丽丽的分割线=================");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }, "检查消费队列").start();
  }
  
  interface IMsgQueue {
    
    void put(Message message);
    
    Message take();
    
  }
  
  /**
   * 消息总队列
   */
  static class MsgQueueManger implements IMsgQueue {
    
    public final BlockingQueue<Message> messageQueue;
    
    public MsgQueueManger() {
      // 创建一个转化队列
      this.messageQueue = new LinkedTransferQueue<>();
    }
    
    public void put(Message message) {
      try {
        System.out.println(Thread.currentThread().getName() + " 生产消息：" + message.data);
        messageQueue.put(message);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    
    public Message take() {
      
      try {
        Message message = messageQueue.take();
        System.out.println(Thread.currentThread().getName() + " 消费消息：" + message.data);
        return message;
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      return null;
    }
    
  }
  
  /**
   * 分发消息，负责把消息从大队列赛到小队列里
   */
  static class DispatchMessageTask implements Runnable {
    
    @Override
    public void run() {
      BlockingQueue<Message> subQueue;
      for (; ; ) {
        // 如果没有数据，则阻塞到这里
        Message msg = MsgQueueFactory.getMessagesQueue().take();
        // 如果为空，则表示没有 Session 机器连接上来
        // 需要等待，直到有 Session 机器连接
        while ((subQueue = Session.getInstance().getSubQueue()) == null) {
          try {
            System.out.println(Thread.currentThread().getName() + " 分发消息，没有获取到消费者队列");
            Thread.sleep(1000L);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
        
        // 把消息放入小队列中
        try {
          // Thread.sleep(100L);
          System.out.println(Thread.currentThread().getName() + " 分发消息，消息：" + msg.data);
          subQueue.put(msg);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
    
  }
  
  static class Session {
    
    private static final Session INSTANCE = new Session();
    
    private final List<BlockingQueue<Message>> subMsgQueues = new CopyOnWriteArrayList<>();
    
    // private AtomicInteger counter = new AtomicInteger();
    
    public static Session getInstance() {
      return INSTANCE;
    }
    
    /**
     * 添加消费者子队列
     *
     * @param subMsgQueue
     */
    public void addSubMsgQueue(BlockingQueue<Message> subMsgQueue) {
      subMsgQueues.add(subMsgQueue);
    }
    
    /**
     * 负载均衡算法获取一个消费者子队列
     *
     * @return
     */
    public BlockingQueue<Message> getSubQueue() {
      
      int errorCount = 0;
      for (; ; ) {
        if (subMsgQueues.isEmpty()) {
          return null;
        }
        int index = (int) (System.nanoTime() % subMsgQueues.size());
        // int index = counter.getAndIncrement() % subMsgQueues.size();
        try {
          System.out.println(Thread.currentThread().getName() + "获取到子队列，index：" + index);
          return subMsgQueues.get(index);
        } catch (Exception e) {
          System.out.println(Thread.currentThread().getName() + " 获取子队列出现错误" + e.getMessage());
          if ((++errorCount) > 3) {
            break;
          }
        }
      }
      return null;
    }
    
  }
  
  static class MsgQueueFactory {
    
    public static MsgQueueManger getMessagesQueue() {
      return InstanceHolder.MSG_QUEUE_MANGER;
    }
    
    /**
     * 静态内部类初始化类
     */
    private static class InstanceHolder {
      
      private static final MsgQueueManger MSG_QUEUE_MANGER = new MsgQueueManger();
      
    }
    
  }
  
  public static class Message {
    
    String data;
    
    public Message(String data) {
      this.data = data;
    }
    
    @Override
    public String toString() {
      return "Message{" + "data=" + data + '}';
    }
    
  }
  
}
