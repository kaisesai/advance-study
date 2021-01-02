package com.kaige.advance.netty.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 聊天室服务端
 */
@Slf4j
public class ChatServer {
  
  public static void main(String[] args) throws InterruptedException {
    // 创建事件循环组
    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    
    try {
      // 创建服务端
      ServerBootstrap bootstrap = new ServerBootstrap();
      // 配置属性
      // 绑定事件循环组
      bootstrap.group(bossGroup, workerGroup)
        // 配置通道实现类
        .channel(NioServerSocketChannel.class)
        // 配置客户端连接队列的大小
        .option(ChannelOption.SO_BACKLOG, 1024)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            // 添加处理器
            ch.pipeline().addLast(new StringDecoder());
            ch.pipeline().addLast(new StringEncoder());
            ch.pipeline().addLast(new ChatServerHandler());
          }
        });
      log.info("聊天室服务端已经启动...");
      // 绑定端口启动服务
      ChannelFuture channelFuture = bootstrap.bind(9000).sync();
      // 监听关闭事件
      channelFuture.channel().closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
    
  }
  
}
