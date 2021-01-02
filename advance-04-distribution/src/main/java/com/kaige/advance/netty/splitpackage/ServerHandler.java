package com.kaige.advance.netty.splitpackage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

public class ServerHandler extends SimpleChannelInboundHandler<MyMessageProtocol> {
  
  private int count;
  
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, MyMessageProtocol msg) throws Exception {
    // 将消息发送给其他的客户端
    System.out.println("服务端收到消息，内容如下=======");
    System.out.println("消息长度：" + msg.getLen());
    System.out.println("消息内容：" + new String(msg.getContent(), StandardCharsets.UTF_8));
    System.out.println("服务器端接收的消息数量：" + ++count);
    
  }
  
}
