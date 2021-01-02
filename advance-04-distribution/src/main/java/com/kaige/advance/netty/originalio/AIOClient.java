package com.kaige.advance.netty.originalio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * AIO 客户端
 */
@Slf4j
public class AIOClient {
  
  public static void main(String[] args)
    throws IOException, ExecutionException, InterruptedException {
    AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
    // 建立连接
    socketChannel.connect(new InetSocketAddress("127.0.0.1", 9000)).get();
    // 向服务器端写入数据
    socketChannel.write(ByteBuffer.wrap("helloServer".getBytes(StandardCharsets.UTF_8)));
    // 读取服务器端的数据
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    Integer len = socketChannel.read(buffer).get();
    if (!Objects.equals(len, -1)) {
      log.info("收到服务器端的数据：{}", new String(buffer.array(), 0, len));
    }
  }
  
}
