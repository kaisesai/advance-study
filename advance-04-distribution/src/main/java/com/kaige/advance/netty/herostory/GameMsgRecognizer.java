package com.kaige.advance.netty.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.kaige.advance.netty.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 消息识别器
 *
 * @author liukai 2021年02月03日
 */
public class GameMsgRecognizer {
  
  /**
   * 消息编号 --> 消息对象字典
   */
  private static final Map<Integer, GeneratedMessageV3> MSGCODE_AND_MSGOBJ_MAP = new HashMap<>();
  
  /**
   * 消息对象 --> 消息编号字典
   */
  private static final Map<Class<?>, Integer> MSGOBJ_AND_MSGCODE_MAP = new HashMap<>();
  
  static {
    init();
  }
  
  private GameMsgRecognizer() {
  }
  
  /**
   * 初始化消息识别器
   */
  public static void init() {
    MSGCODE_AND_MSGOBJ_MAP.put(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE,
                               GameMsgProtocol.UserEntryCmd.getDefaultInstance());
    MSGCODE_AND_MSGOBJ_MAP.put(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE,
                               GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
    MSGCODE_AND_MSGOBJ_MAP.put(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE,
                               GameMsgProtocol.UserMoveToCmd.getDefaultInstance());
    
    MSGOBJ_AND_MSGCODE_MAP
      .put(GameMsgProtocol.UserEntryResult.class, GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE);
    MSGOBJ_AND_MSGCODE_MAP.put(GameMsgProtocol.WhoElseIsHereResult.class,
                               GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE);
    MSGOBJ_AND_MSGCODE_MAP.put(GameMsgProtocol.UserMoveToResult.class,
                               GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE);
    MSGOBJ_AND_MSGCODE_MAP
      .put(GameMsgProtocol.UserQuitResult.class, GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE);
  }
  
  /**
   * 根据消息类型获取消息编号
   *
   * @param clazz
   * @return
   */
  public static int getMsgCodeByMsgClass(Class<?> clazz) {
    if (Objects.isNull(clazz)) {
      return -1;
    }
    return MSGOBJ_AND_MSGCODE_MAP.getOrDefault(clazz, -1);
  }
  
  /**
   * 根据消息编号获取消息构建器
   *
   * @param msgCode
   * @return
   */
  public static Message.Builder getBuilderByMsgCode(int msgCode) {
    if (msgCode < 0) {
      return null;
    }
    GeneratedMessageV3 message = MSGCODE_AND_MSGOBJ_MAP.get(msgCode);
    if (Objects.isNull(message)) {
      return null;
    }
    return message.newBuilderForType();
  }
  
}
