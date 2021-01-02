package com.kaige.advance.netty.originalio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

/**
 * NIO 客户端
 */
@Slf4j
public class NIOClient {
  
  private Selector selector;
  
  public static void main(String[] args) throws IOException {
    NIOClient nioClient = new NIOClient();
    // 初始化连接
    nioClient.initClient("127.0.0.1", 9000);
    // 建立连接
    nioClient.connect();
  }
  
  public void connect() throws IOException {
    // 轮询访问 selector
    while (true) {
      // 监听 selector 中感兴趣的事件
      selector.select();
      // 获取 selector 中选中的项的迭代器
      Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
      while (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        // 删除已选择的 key，防止重复处理
        keyIterator.remove();
        // 连接事件发生
        if (key.isConnectable()) {
          SocketChannel sc = (SocketChannel) key.channel();
          // 如果正在连接，则完成连接
          if (sc.isConnectionPending()) {
            sc.finishConnect();
          }
          
          // 设置成非阻塞
          sc.configureBlocking(false);
          // 这里也可以给服务端发送信息
          ByteBuffer buffer = ByteBuffer.wrap("helloServer".getBytes(StandardCharsets.UTF_8));
          sc.write(buffer);
          // 在和服务器端连接成功之后，为了可以接收到服务器端的消息，需要给通道设置读的权限
          sc.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
          read(key);
        }
      }
      
    }
  }
  
  /**
   * 处理读取服务器端发消息的事件
   *
   * @param key
   */
  private void read(SelectionKey key) throws IOException {
    // 服务器可读的消息：得到事件发生的 socket 通道
    SocketChannel sc = (SocketChannel) key.channel();
    // 创建读取的缓冲区
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    int read = sc.read(buffer);
    
    if (read != -1) {
      log.info("获取到服务器端的消息：{}", new String(buffer.array(), 0, read));
    }
    
    // 通过键盘输入消息传给服务器
    Scanner scanner = new Scanner(System.in);
    String next = scanner.next();
    log.info("客户端输入了数据：{}", next);
    sc.write(ByteBuffer.wrap(next.getBytes(StandardCharsets.UTF_8)));
    log.info("将数据写入服务端：{}", next);
  }
  
  /**
   * 获取一个 socket 通道，并对该通道做一些初始化的工作
   *
   * @param ip   连接的服务器 ip
   * @param port 连接的服务器的端口号
   * @throws IOException
   */
  public void initClient(String ip, int port) throws IOException {
    // 获取一个 socket 通道
    SocketChannel channel = SocketChannel.open();
    // 设置通道为非阻塞
    channel.configureBlocking(false);
    // 获得一个通道管理器
    selector = Selector.open();
    
    // 客户端连接服务器，其实方法执行并没有实现连接，需要监听处理 key 连接事件的方法中调用 channel.finishConnect(); 才能完成
    channel.connect(new InetSocketAddress(ip, port));
    
    // 将通道管理器和该通道绑定，并为该通道注册 selectKey 的 OP_CONNECT 事件
    channel.register(selector, SelectionKey.OP_CONNECT);
    
  }
  
}
