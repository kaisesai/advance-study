package com.kaige.advance.netty.heartbeat;

import com.kaige.advance.netty.CommonNettyServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class HeartbeatServer {
  
  public static void main(String[] args) throws InterruptedException {
    CommonNettyServer.startNettyServer(new ChannelInitializer<NioSocketChannel>() {
      @Override
      protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new StringDecoder());
        ch.pipeline().addLast(new StringEncoder());
        // 添加心跳处理器
        // IdleStateHandler 第一个参数表示，读超时，当在指定时间间隔内没有从 Channel 读取到数据时，会触发一个 READER_IDLE 的
        // idleStateEvent 事件
        ch.pipeline().addLast(new IdleStateHandler(3, 0, 0, TimeUnit.SECONDS));
        ch.pipeline().addLast(new HeartbeatServerHandler());
      }
    });
  }
  
  /** 消息处理器，重写触发事件接口 */
  static class HeartbeatServerHandler extends SimpleChannelInboundHandler<String> {
    
    private final Map<ChannelId, Integer> readIdleTimes = new ConcurrentHashMap<>();
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
      System.out.println("===== > [server] message received: " + msg);
      System.out.println("读取到客户端消息：" + msg);
      if ("Heartbeat Packet".equals(msg)) {
        ctx.writeAndFlush("ok");
      } else {
        System.out.println("其他消息处理");
      }
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      // 注册
      readIdleTimes.putIfAbsent(ctx.channel().id(), 0);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      // 取消注册
      readIdleTimes.remove(ctx.channel().id());
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      
      IdleStateEvent event = (IdleStateEvent) evt;
      String eventType = "";
      switch (event.state()) {
        case READER_IDLE:
          eventType = "读空闲";
          readIdleTimes.computeIfPresent(ctx.channel().id(), (k, v) -> v + 1);
          break;
        case WRITER_IDLE:
          eventType = "写空闲";
          break;
        case ALL_IDLE:
          eventType = "读写空闲";
          break;
      }
      
      System.out.println(ctx.channel().remoteAddress() + " 超时事件：" + eventType);
      
      if (readIdleTimes.getOrDefault(ctx.channel().id(), 0) > 3) {
        System.out.println("[server] 读空闲超过 3 次，关闭连接，释放更多资源");
        ctx.channel().writeAndFlush("idle close");
        ctx.channel().close();
      }
      
      // 客户端已经超时，需要关闭客户端连接
      // super.userEventTriggered(ctx, evt);
    }
    
  }
  
}
