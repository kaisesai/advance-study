package com.kaige.advance.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty 服务端
 */
@Slf4j
public class NettyServer {
  
  public static void main(String[] args) throws Exception {
    // 创建两个线程组 bossGroup 和 workerGroup，含有子线程 NioEventLoop 的个数默认为 CPU 核数的两倍
    // bossGroup 只是处理连接请求，真正的和客户端业务处理，会交给 workerGroup 完成
    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    
    try {
      // 创建服务器端的启动对象
      ServerBootstrap bootstrap = new ServerBootstrap();
      // 配置参数
      bootstrap.group(bossGroup, workerGroup) // 设置两个线程组
        // 使用 NioServerSocketChannel 作为服务器的通道实现
        .channel(NioServerSocketChannel.class)
        // 初始化服务器连接队列的大小，服务器处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接。
        // 多个客户端同时来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理
        .option(ChannelOption.SO_BACKLOG, 1024)
        // 创建通道初始化对象，设置初始化参数
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            // 对 workerGroup 的 SocketChannel 设置处理器
            ch.pipeline().addLast(new NettyServerHandler());
          }
        });
      
      log.info("netty server start...");
      // 绑定一个端口并且同步，生成一个 ChannelFuture 异步对象，通过 isDone() 等方法可以判断异步时间的执行情况
      // 启动服务器（并绑定端口），bind 是异步操作，sync 方法是等待异步操作执行完毕
      ChannelFuture cf = bootstrap.bind(9000).sync();
      // 给 cf 注册监听器，监听我们关心的时间
      cf.addListener((ChannelFutureListener) future -> {
        if (cf.isSuccess()) {
          log.info("监听端口 9000 成功");
        } else {
          log.info("监听端口 9000 失败");
        }
      });
      
      // 对通道关闭进行监听，closeFuture 是异步操作，监听通道关闭
      // 通过 sync() 方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成
      cf.channel().closeFuture().sync();
      
    } finally {
      // 优雅关闭线程组
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
    
  }
  
}
