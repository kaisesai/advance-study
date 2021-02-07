package com.kaige.advance.netty.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.kaige.advance.netty.herostory.cmdhandler.CmdHandlerFactory;
import com.kaige.advance.netty.herostory.cmdhandler.ICmdHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消息处理器
 *
 * @author liukai 2021年02月06日
 */
@Slf4j
public class GameMsgProcessor {
  
  /**
   * 单一线程的线程池，利用一个线程 + 一个队列的方式，顺序处理任务
   */
  private static final ExecutorService SINGLE_THREAD_EXECUTOR = Executors
    .newSingleThreadExecutor(r -> new Thread(r, "GameMsgProcessorThread"));
  
  private static volatile GameMsgProcessor gameMsgProcessorInstance;
  
  private GameMsgProcessor() {
  }
  
  /**
   * 获取实例
   *
   * @return
   */
  public static GameMsgProcessor getInstance() {
    if (gameMsgProcessorInstance == null) {
      synchronized (GameMsgProcessor.class) {
        if (gameMsgProcessorInstance == null) {
          gameMsgProcessorInstance = new GameMsgProcessor();
        }
      }
    }
    return gameMsgProcessorInstance;
  }
  
  /**
   * 处理消息
   *
   * @param ctx
   * @param msg
   */
  public void process(ChannelHandlerContext ctx, Object msg) {
    SINGLE_THREAD_EXECUTOR.execute(() -> {
      log.info("process msg:{}", msg);
      try {
        ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory
          .getCmdHandler(msg.getClass());
        if (Objects.isNull(cmdHandler)) {
          return;
        }
        cmdHandler.handle(ctx, convertMsg(msg));
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    });
    
  }
  
  /**
   * 转换命令对象
   *
   * @param msg
   * @param <T>
   * @return
   */
  @SuppressWarnings(value = "unchecked")
  private <T extends GeneratedMessageV3> T convertMsg(Object msg) {
    return (T) msg;
  }
  
}
