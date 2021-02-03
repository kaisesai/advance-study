package com.kaige.advance.netty.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Objects;

/**
 * 广播员
 *
 * @author liukai 2021年02月03日
 */
public class Broadcaster {
  
  /**
   * 全局通信信道
   */
  private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(
    GlobalEventExecutor.INSTANCE);
  
  private Broadcaster() {
  }
  
  /**
   * 添加信道
   *
   * @param channel
   */
  public static void addChannel(Channel channel) {
    if (Objects.isNull(channel)) {
      return;
    }
    CHANNEL_GROUP.add(channel);
  }
  
  /**
   * 移除信道
   *
   * @param channel
   */
  public static void removeChannel(Channel channel) {
    if (Objects.isNull(channel)) {
      return;
    }
    CHANNEL_GROUP.remove(channel);
  }
  
  /**
   * 广播消息
   *
   * @param msg
   */
  public static void broadcast(Object msg) {
    if (Objects.isNull(msg)) {
      return;
    }
    CHANNEL_GROUP.writeAndFlush(msg);
    
  }
  
}
