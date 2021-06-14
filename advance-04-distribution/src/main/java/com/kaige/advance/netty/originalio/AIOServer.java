package com.kaige.advance.netty.originalio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/** AIO 服务端 */
@Slf4j
public class AIOServer {
  
  public static void main(String[] args) throws IOException, InterruptedException {
    AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()
      .bind(new InetSocketAddress(9000));
    
    serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
      
      @Override
      public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
        try {
          // 在此接收客户端连接，需要这行代码，确保后面的客户端能连接到服务器
          serverSocketChannel.accept(attachment, this);
          
          log.info("来自远方的客人：{}", socketChannel.getRemoteAddress().toString());
          // 读取数据事件
          ByteBuffer buffer = ByteBuffer.allocate(1024);
          socketChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
              buffer.flip();
              log.info("读取到客户端的数据：{}", new String(buffer.array(), 0, result));
              
              // 写数据
              socketChannel.write(ByteBuffer.wrap("helloClient".getBytes(StandardCharsets.UTF_8)));
            }
            
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
              exc.printStackTrace();
            }
          });
          
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      
      @Override
      public void failed(Throwable exc, Object attachment) {
        exc.printStackTrace();
      }
    });
    
    Thread.sleep(Integer.MAX_VALUE);
  }
  
}
