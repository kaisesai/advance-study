package com.kaige.advance.netty.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import com.kaige.advance.netty.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 命令处理器工厂
 */
public class CmdHandlerFactory {
  
  private static final Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> CMD_HANDLER_MAP
    = new HashMap<>();
  
  static {
    init();
  }
  
  private CmdHandlerFactory() {
  }
  
  /**
   * 初始化命令处理器
   */
  public static void init() {
    CMD_HANDLER_MAP.put(GameMsgProtocol.UserEntryCmd.class, new UserEntryCmdHandler());
    CMD_HANDLER_MAP.put(GameMsgProtocol.WhoElseIsHereCmd.class, new WhoElseIsHereCmdHandler());
    CMD_HANDLER_MAP.put(GameMsgProtocol.UserMoveToCmd.class, new UserMoveToCmdHandler());
    
  }
  
  /**
   * 获取命令处理器
   *
   * @param clazz
   * @return
   */
  public static ICmdHandler<? extends GeneratedMessageV3> getCmdHandler(Class<?> clazz) {
    if (Objects.isNull(clazz)) {
      return null;
    }
    return CMD_HANDLER_MAP.get(clazz);
  }
  
}
