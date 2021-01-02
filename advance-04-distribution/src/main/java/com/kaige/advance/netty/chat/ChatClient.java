package com.kaige.advance.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * 聊天室客户端
 */
@Slf4j
public class ChatClient {
  
  public static void main(String[] args) throws InterruptedException {
    // 创建一个事件循环组
    NioEventLoopGroup group = new NioEventLoopGroup();
    
    try {
      // 创建客户端启动对象
      Bootstrap bootstrap = new Bootstrap();
      // 配置参数
      // 配置事件循环组
      bootstrap.group(group)
        // 配置通道实现
        .channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
          // 添加处理器到管道
          ch.pipeline().addLast(new StringDecoder());
          ch.pipeline().addLast(new StringEncoder());
          ch.pipeline().addLast(new ChatClientHandler());
        }
      });
      
      log.info("聊天室客户端创建成功...");
      
      // 连接服务器
      ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();
      
      // 通过监听输入端不断的发送消息
      Scanner scanner = new Scanner(System.in);
      while (scanner.hasNextLine()) {
        String next = scanner.nextLine();
        log.info("发送消息：{}", next);
        // channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(next, StandardCharsets.UTF_8));
        channelFuture.channel().writeAndFlush(next);
      }
      
      // 监听关闭事件
      channelFuture.channel().closeFuture().sync();
      
    } finally {
      group.shutdownGracefully();
    }
  }
  
}
