package com.liukai.advance.netty.splitpackage;

import com.liukai.advance.netty.CommonNettyClient;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
  
  public static void main(String[] args) throws InterruptedException {
    CommonNettyClient.startNettyClient(new ChannelInitializer<NioSocketChannel>() {
      @Override
      protected void initChannel(NioSocketChannel ch) throws Exception {
        // ch.pipeline().addLast(new StringDecoder());
        // ch.pipeline().addLast(new StringEncoder());
        ch.pipeline().addLast(new MyMessageEncoder());
        ch.pipeline().addLast(new ClientHandler());
        
      }
    });
  }
  
}
