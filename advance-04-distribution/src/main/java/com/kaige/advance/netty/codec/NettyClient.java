package com.kaige.advance.netty.codec;

import com.kaige.advance.netty.CommonNettyClient;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * netty 客户端
 */
public class NettyClient {
  
  public static void main(String[] args) throws Exception {
    
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
        ch.pipeline()
          .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
        ch.pipeline().addLast(new ObjectEncoder());
        ch.pipeline().addLast(new NettyClientHandler());
      }
    };
    
    CommonNettyClient.startNettyClient(channelInitializer);
  }
  
}
