package com.liukai.advance.netty.originalio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 客户端 socket
 */
@Slf4j
public class BIOClient {
  
  public static void main(String[] args) throws IOException {
    Socket socket = new Socket("127.0.0.1", 9000);
    // 向服务器端发送数据
    socket.getOutputStream().write("helloServer".getBytes(StandardCharsets.UTF_8));
    socket.getOutputStream().flush();
    log.info("向服务器端写入数据");
    // 接收服务器端回传的数据
    byte[] bytes = new byte[1024];
    int read = socket.getInputStream().read(bytes);
    log.info("接收到服务器端的数据：{}", new String(bytes, 0, read));
    socket.close();
  }
  
}
