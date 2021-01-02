package com.liukai.advance.netty.codec;

import com.liukai.advance.netty.CommonNettyServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Netty 服务端
 */
public class NettyServer {
  
  public static void main(String[] args) throws InterruptedException {
    
    ChannelInitializer<NioSocketChannel> channelInitializer
      = new ChannelInitializer<NioSocketChannel>() {
      @Override
      protected void initChannel(NioSocketChannel ch) throws Exception {
        // 注册消息处理器到 channel 管道
        // 注册 byte-long 消息处理器
        // ch.pipeline().addLast(new ByteToLongDecoder());
        // ch.pipeline().addLast(new LongToByteEncoder());
        // 注册字符串消息处理器
        // ch.pipeline().addLast(new StringDecoder());
        // ch.pipeline().addLast(new StringEncoder());
        // 注册对象消息处理器
        ch.pipeline().addLast(new ObjectDecoder(1000, ClassResolvers.cacheDisabled(null)));
        ch.pipeline().addLast(new ObjectEncoder());
        ch.pipeline().addLast(new NettyServerHandler());
      }
    };
    
    CommonNettyServer.startNettyServer(channelInitializer);
    
  }
  
}
