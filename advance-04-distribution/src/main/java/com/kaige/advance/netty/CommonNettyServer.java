package com.kaige.advance.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class CommonNettyServer {
  
  /**
   * 启动 netty 服务端
   *
   * @param channelInitializer
   * @throws InterruptedException
   */
  public static void startNettyServer(ChannelInitializer<NioSocketChannel> channelInitializer)
    throws InterruptedException {
    // 创建两个事件循环组
    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    
    try {
      // 创建服务端启动类
      ServerBootstrap bootstrap = new ServerBootstrap();
      // 配置属性
      // 绑定循环组
      bootstrap.group(bossGroup, workerGroup)
        // 配置 channel 实现类
        .channel(NioServerSocketChannel.class)
        // 配置属性
        .option(ChannelOption.SO_BACKLOG, 1024)
        // 初始化 channel
        .childHandler(channelInitializer);
      
      System.out.println("netty server start");
      // 绑定端口
      ChannelFuture channelFuture = bootstrap.bind(9000).sync();
      // 监听关闭事件
      channelFuture.channel().closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
  
}
