package com.liukai.advance.netty.originalio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * BIO 服务端
 */
@Slf4j
public class BIOServer {
  
  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(9000);
    log.info("服务器端已经创建");
    
    while (true) {
      log.info("等待客户端连接...");
      // 阻塞方法
      Socket socket = serverSocket.accept();
      log.info("有客户端连接进来了...");
      new Thread(() -> {
        try {
          handler(socket);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();
    }
    
  }
  
  private static void handler(Socket socket) throws IOException {
    log.info("开始处理客户端连接");
    byte[] bytes = new byte[1024];
    
    log.info("准备read...");
    InputStream inputStream = socket.getInputStream();
    int read = inputStream.read(bytes);
    log.info("读取完毕");
    if (read != -1) {
      log.info("接收到客户端数据，msg:{}", new String(bytes, 0, read));
    }
    log.info("准备write...");
    socket.getOutputStream().write("helloClient".getBytes(StandardCharsets.UTF_8));
    socket.getOutputStream().flush();
  }
  
}
