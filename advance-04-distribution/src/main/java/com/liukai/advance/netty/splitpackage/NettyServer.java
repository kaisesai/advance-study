package com.liukai.advance.netty.splitpackage;

import com.liukai.advance.netty.CommonNettyServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyServer {
  
  public static void main(String[] args) throws InterruptedException {
    CommonNettyServer.startNettyServer(new ChannelInitializer<NioSocketChannel>() {
      @Override
      protected void initChannel(NioSocketChannel ch) throws Exception {
        // 注册消息处理器
        ChannelPipeline pipeline = ch.pipeline();
        // 添加解码器
        // pipeline.addLast(new StringDecoder());
        // pipeline.addLast(new StringEncoder());
        pipeline.addLast(new MyMessageDecoder());
        // 添加自定义的解决拆包粘包解码器
        pipeline.addLast(new ServerHandler());
      }
    });
  }
  
}
