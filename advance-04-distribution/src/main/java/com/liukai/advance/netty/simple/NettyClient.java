package com.liukai.advance.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty 客户端
 */
@Slf4j
public class NettyClient {
  
  public static void main(String[] args) throws InterruptedException {
    // 客户端需要一个事件循环组
    NioEventLoopGroup group = new NioEventLoopGroup();
    
    try {
      // 创建客户端启动对象
      // 客户端使用的 Bootstrap，而不是 ServerBootstrap
      Bootstrap bootstrap = new Bootstrap();
      // 设置相关参数
      // 设置线程组
      bootstrap.group(group)
        // 使用 NioSocketChannel 作为客户端的通道实现
        .channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>() {
        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
          // 加入处理器
          ch.pipeline().addLast(new NettyClientHandler());
        }
      });
      
      log.info("netty client start...");
      // 启动客户端去连接服务端
      ChannelFuture cf = bootstrap.connect("127.0.0.1", 9000).sync();
      // 对关闭通道进行监听
      cf.channel().closeFuture().sync();
      
    } finally {
      group.shutdownGracefully();
    }
    
  }
  
}
