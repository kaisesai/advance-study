package com.kaige.advance.netty.herostory;

import com.kaige.advance.netty.herostory.config.SqlSessionFactoryConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 英雄传说服务端启动程序
 *
 * @author liukai 2021年02月03日
 */
@Slf4j
public class ServerMain {
  
  public static void main(String[] args) {
  
    // 初始化命令处理工厂
    // CmdHandlerFactory.init();
    // GameMsgRecognizer.init();
    // 初始化 SqlSessionFactory
    SqlSessionFactoryConfig.init();
  
    NioEventLoopGroup boss = new NioEventLoopGroup(1);
    NioEventLoopGroup worker = new NioEventLoopGroup();
  
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
      // 初始化服务器连接队列的大小，服务器处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接。
      // 多个客户端同时来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理
      .option(ChannelOption.SO_BACKLOG, 1024)
      // 创建通道初始化对象，设置初始化参数
      .childHandler(new ChannelInitializer<SocketChannel>() {
        
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
          // http 服务器编解码
          ch.pipeline().addLast(new HttpServerCodec());
          // http 对象聚合处理器
          ch.pipeline().addLast(new HttpObjectAggregator(65535));
          // websocket 协议处理器，这里会处理握手、ping、pong 等消息
          ch.pipeline().addLast(new WebSocketServerProtocolHandler("/websocket"));
          // 自定义消息解码器
          ch.pipeline().addLast(new GameMsgDecoder());
          ch.pipeline().addLast(new GameMsgEncoder());
          // 自定义的消息处理器
          ch.pipeline().addLast(new GameMsgHandler());
        }
      });
    
    try {
      // 绑定端口，同步等待
      ChannelFuture future = bootstrap.bind(12345).sync();
      
      if (future.isSuccess()) {
        log.info("服务器启动成功！");
      }
      
      // 监听关闭事件
      future.channel().closeFuture().sync();
      
      log.info("服务器已经关闭！");
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
    } finally {
      // 关闭服务器事件循环组
      boss.shutdownGracefully();
      worker.shutdownGracefully();
    }
    
  }
  
}
