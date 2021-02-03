package com.kaige.advance.netty.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 自定义消息编码器
 * <p>
 * 消息格式：消息的格式为：2 byte 的消息长度 --> 2 byte 消息编码 --> 消息 body
 */
@Slf4j
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
  
  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
    
    if (Objects.isNull(ctx) || Objects.isNull(msg)) {
      return;
    }
    
    try {
      if (!(msg instanceof GeneratedMessageV3)) {
        super.write(ctx, msg, promise);
        return;
      }
      
      // 消息编码
      int msgCode = GameMsgRecognizer.getMsgCodeByMsgClass(msg.getClass());
      if (msgCode < 0) {
        log.error("无法识别的消息类型， msgClass = {}", msg.getClass().getName());
        super.write(ctx, msg, promise);
        return;
      }
      
      // 消息体
      byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();
      
      // 消息长度
      ByteBuf buffer = ctx.alloc().buffer(4 + msgBody.length);
      
      // 客户端代码解析优点问题，它使用了下面的解析方式
      // 写出-只是为了占位
      buffer.writeShort(0);
      // buffer.writeShort(msgBody.length);
      buffer.writeShort(msgCode);
      buffer.writeBytes(msgBody);
      
      // 构建 BinaryWebSocketFrame 消息体，写出去给下一个出站处理器
      BinaryWebSocketFrame webSocketFrame = new BinaryWebSocketFrame(buffer);
      
      // log.info("write msg: {}", webSocketFrame);
      super.write(ctx, webSocketFrame, promise);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
  
}
