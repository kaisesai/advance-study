package com.kaige.advance.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class CommonNettyClient {
  
  /**
   * 启动 netty 客户端
   *
   * @param channelInitializer
   * @throws InterruptedException
   */
  public static void startNettyClient(ChannelInitializer<NioSocketChannel> channelInitializer)
    throws InterruptedException {
    // 创建事件循环组
    NioEventLoopGroup group = new NioEventLoopGroup();
    try {
      // 创建客户端启动类
      Bootstrap bootstrap = new Bootstrap();
      // 配置属性
      // 设置事件循环组
      bootstrap.group(group)
        // 设置 channel 实现类
        .channel(NioSocketChannel.class)
        // 初始化 channel
        .handler(channelInitializer);
      // 启动 netty 客户端
      System.out.println("netty client start");
      ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();
      // 监听关闭事件
      channelFuture.channel().closeFuture().sync();
      
    } finally {
      group.shutdownGracefully();
    }
  }
  
}
