package com.kaige.advance.netty.originalio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Slf4j
public class NIOServer {
  
  public static void main(String[] args) throws IOException {
    // 创建一个本地端口进行监听的服务 socket 通道，并设置为非阻塞方式
    ServerSocketChannel ssc = ServerSocketChannel.open();
    // 必须配置为非阻塞才能在 selector 上阻塞，否则会报错，selector 模式本身就是非阻塞模式
    ssc.configureBlocking(false);
    ssc.socket().bind(new InetSocketAddress(9000));
    
    // 创建一个选择器 selector
    Selector selector = Selector.open();
    // 把 ServerSocketChannel 注册到 selector 上，并且 selector 对客户端 accept 连接操作感兴趣
    ssc.register(selector, SelectionKey.OP_ACCEPT);
    
    while (true) {
      log.info("等待事件发生...");
      // 轮询监听 channel 里的 key，select() 是阻塞的，accept() 也是阻塞的
      int select = selector.select();
      log.info("有事情发生了...");
      Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
      if (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        // 删除本次已处理的 key，防止下次 select 重复处理
        keyIterator.remove();
        handle(key);
      }
    }
    
  }
  
  private static void handle(SelectionKey key) throws IOException {
    if (key.isAcceptable()) {
      log.info("有客户端连接进来了...");
      ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
      // NIO 非阻塞的体现：此处 accept 方法是阻塞的，但是这里因为是发生了连接事件，所以这个方法会马上执行完，不会阻塞
      // 处理完连接请求不会继续等待客户端的数据发送
      SocketChannel sc = ssc.accept();
      sc.configureBlocking(false);
      // 通过 selector 监听 channel 时对读事件感兴趣
      sc.register(key.selector(), SelectionKey.OP_READ);
    } else if (key.isReadable()) {
      log.info("有客户端数据可读事件发生了...");
      SocketChannel sc = (SocketChannel) key.channel();
      ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
      // NIO 非阻塞的体现：首先 read 方法不会阻塞，其次这种事件响应模型，当调用到 read 方法时肯定是发生了客户端发送数据的事件
      int read = sc.read(byteBuffer);
      if (read != -1) {
        log.info("读取到客户端发送的数据：{}", new String(byteBuffer.array(), 0, read));
      }
      
      ByteBuffer buffer = ByteBuffer.wrap("helloClient".getBytes(StandardCharsets.UTF_8));
      sc.write(buffer);
      // 设置 socketChannel key 的读、写兴趣
      key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
      // key.interestOps(SelectionKey.OP_READ);
    } else if (key.isWritable()) {
      log.info("write 事件");
      SocketChannel sc = (SocketChannel) key.channel();
      // NIO 事件触发是水平触发
      // 使用 Java 的 NIO 编程时，在没有数据可以往外写的时候取消写事件
      // 在有数据外写的时候再注册写事件
      key.interestOps(SelectionKey.OP_READ);
      // key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }
    
  }
  
}
