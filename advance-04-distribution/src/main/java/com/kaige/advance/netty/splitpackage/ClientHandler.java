package com.kaige.advance.netty.splitpackage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang.RandomStringUtils;

import java.nio.charset.StandardCharsets;

public class ClientHandler extends SimpleChannelInboundHandler<MyMessageProtocol> {
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    // 发送 10 次消息，模拟消息粘包与拆包
    for (int i = 0; i < 100; i++) {
      String msg = "你好，我是凯哥" + i + " random-" + RandomStringUtils.randomAlphabetic(100);
      // 获取消息的内容以及长度
      byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
      // 封装到
      MyMessageProtocol mp = new MyMessageProtocol();
      mp.setLen(bytes.length);
      mp.setContent(bytes);
      ctx.writeAndFlush(mp);
    }
  }
  
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, MyMessageProtocol msg) throws Exception {
  }
  
}
