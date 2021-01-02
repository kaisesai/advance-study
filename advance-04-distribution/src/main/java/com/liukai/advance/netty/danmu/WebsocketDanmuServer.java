package com.liukai.advance.netty.danmu;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 基于 websocket 协议实现的弹幕服务端系统
 */
public class WebsocketDanmuServer {
  
  public static void main(String[] args) throws InterruptedException {
    // 创建事件循环组
    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    
    try {
      // 创建服务启动类
      ServerBootstrap bootstrap = new ServerBootstrap();
      // 配置属性
      // 配置事件组
      bootstrap.group(bossGroup, workerGroup)
        // 配置 channel 实现类
        .channel(NioServerSocketChannel.class)
        // 配置客户端连接数
        .option(ChannelOption.SO_BACKLOG, 1024)
        // 配置工作组客户端连接保持存活
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        // 配置工作组消息处理器
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            // 向 channel 的 pipeline 中注册消息处理器
            ChannelPipeline pipeline = ch.pipeline();
            // http 请求解码处理器
            pipeline.addLast(new HttpRequestDecoder());
            // http 请求头和请求体合并处理器，参数为接收的内容的最大长度为 64KB
            pipeline.addLast(new HttpObjectAggregator(65536));
            // http 响应编码处理器
            pipeline.addLast(new HttpResponseEncoder());
            // http 写响应
            pipeline.addLast(new ChunkedWriteHandler());
            
            // http 请求处理器，自定义实现
            pipeline.addLast(new HttpRequestHandler("/ws"));
            // websocket 协议解码器
            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
            // websocket 消息逻辑处理
            pipeline.addLast(new TextWebSocketFrameHandler());
          }
        });
      
      // 绑定地址
      ChannelFuture channelFuture = bootstrap.bind(8080).sync();
      channelFuture.channel().closeFuture().sync();
      
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
    
  }
  
}
