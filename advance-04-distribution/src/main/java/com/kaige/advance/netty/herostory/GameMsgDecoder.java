package com.kaige.advance.netty.herostory;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 自定义消息解码器
 *
 * <p>消息的格式为：2 byte 的消息长度 --> 2 byte 消息编码 --> 消息 body
 *
 * <p>按照这样的消息格式进行解析
 */
@Slf4j
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
  
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    
    if (Objects.isNull(ctx) || Objects.isNull(msg)) {
      return;
    }
    
    if (!(msg instanceof BinaryWebSocketFrame)) {
      return;
    }
    
    try {
      BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msg;
      ByteBuf content = inputFrame.content();
      
      // 获取消息长度
      content.readShort();
      // 获取消息编号
      short msgCode = content.readShort();
      
      // 获取消息体
      byte[] msgBody = new byte[content.readableBytes()];
      content.readBytes(msgBody);
      
      // 解析命令对象
      Message.Builder msgBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
      if (Objects.isNull(msgBuilder)) {
        log.warn("msgBuilder is null, msgCode:{}", msgCode);
        return;
      }
      
      msgBuilder.clear();
      Message cmd = msgBuilder.mergeFrom(msgBody).build();
      
      if (Objects.nonNull(cmd)) {
        ctx.fireChannelRead(cmd);
      }
      
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
  
}
