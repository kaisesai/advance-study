package com.liukai.advance.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * Netty 的 ByteBuf 类测试
 */
@Slf4j
public class NettyByteBuf {
  
  public static void main(String[] args) {
  
    /*
      创建 byteBuf 对象，该对象内部包含一个字节数组 byte[10]
      通过 readerIndex 和 writerIndex 和 capacity，将 buffer 分成三个区域
      已经读取的区域：[0, readerIndex)
      可读取的区域：[readerIndex, writerIndex)
      可写入的区域：[writerIndex, capacity)
     */
    ByteBuf byteBuf = Unpooled.buffer(10);
    log.info("初始化 byteBuf = " + byteBuf);
    
    for (int i = 0; i < 8; i++) {
      byteBuf.writeByte(i);
    }
    log.info("执行 writeByte 方法之后 byteBuf = " + byteBuf);
    
    for (int i = 0; i < 5; i++) {
      log.info("byteBuf.getByte " + i + " = " + byteBuf.getByte(i));
    }
    
    log.info("执行 getByte 方法之后的 byteBuf = " + byteBuf);
    
    for (int i = 0; i < 5; i++) {
      log.info("byteBuf.readByte() = " + byteBuf.readByte());
    }
    log.info("执行 readByte 方法之后的 byteBuf = " + byteBuf);
    
    // 使用 Unpooled 工具类创建 ByteBuf
    ByteBuf buf = Unpooled.copiedBuffer("hello, man", StandardCharsets.UTF_8);
    // 相关方法
    if (buf.hasArray()) {
      byte[] array = buf.array();
      // 将 array 转成字符串
      log.info(new String(array, StandardCharsets.UTF_8));
      log.info("buf = " + buf);
      
      // 获取当前读指针
      log.info("buf.readerIndex() = " + buf.readerIndex());
      // 获取当前写指针
      log.info("buf.writerIndex() = " + buf.writerIndex());
      // 获取缓存区容量
      log.info("buf.capacity() = " + buf.capacity());
      
      // 获取数组 0 这个位置的字符 h 的 ASCII 码，h = 104
      log.info("buf.getByte(0) = " + buf.getByte(0));
      
      // 可读取的字节数
      log.info("buf.readableBytes() = " + buf.readableBytes());
      
      // 使用 for 取出各个字节
      for (int i = 0; i < buf.readableBytes(); i++) {
        log.info("buf.getByte " + i + " = " + buf.getByte(i));
      }
      
      // 范围读取
      log.info("buf.getCharSequence(0,6,StandardCharsets.UTF_8) = " + buf
        .getCharSequence(0, 6, StandardCharsets.UTF_8));
      log.info("buf.getCharSequence(0,6,StandardCharsets.UTF_8) = " + buf
        .getCharSequence(6, 6, StandardCharsets.UTF_8));
      
    }
    
  }
  
}
