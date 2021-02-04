package com.kaige.advance.netty.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.kaige.advance.netty.herostory.msg.GameMsgProtocol;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 消息识别器
 *
 * @author liukai 2021年02月03日
 */
@Slf4j
public class GameMsgRecognizer {
  
  /**
   * 消息编号 --> 消息对象字典
   */
  private static final Map<Integer, GeneratedMessageV3> MSGCODE_AND_MSGOBJ_MAP = new HashMap<>();
  
  /**
   * 消息对象 --> 消息编号字典
   */
  private static final Map<Class<?>, Integer> MSGCLASS_AND_MSGCODE_MAP = new HashMap<>();
  
  static {
    init();
  }
  
  private GameMsgRecognizer() {
  }
  
  /**
   * 初始化消息识别器
   */
  public static void init() {
    log.info("初始化消息识别器");
    // 通过反射来获取类型信息
    // 获取类中声明的类，这些类包括静态与非静态类
    Class<?>[] innerClassArr = GameMsgProtocol.class.getDeclaredClasses();
    
    // 遍历声明的类型
    for (Class<?> innerClass : innerClassArr) {
      // 过滤无效的类型
      if (Objects.isNull(innerClass) || !GeneratedMessageV3.class.isAssignableFrom(innerClass)) {
        continue;
      }
      
      // 获取类名称并且转化为小写
      String lowerCaseClazzName = StringUtils.lowerCase(innerClass.getSimpleName());
      
      // 遍历消息类型枚举，获取每个枚举的名称，去除下划线且转化为小写
      for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
        String msgCodeName = msgCode.name();
        // 转化小写
        msgCodeName = StringUtils.lowerCase(msgCodeName);
        // 去除下划线
        msgCodeName = StringUtils.replace(msgCodeName, "_", StringUtils.EMPTY);
        
        // 比较类名与枚举名是否一致
        if (StringUtils.equals(lowerCaseClazzName, msgCodeName)) {
          log.info("{} <==> {}", msgCode.name(), innerClass.getSimpleName());
          
          // 调用类的 getDefaultInstance() 静态方法获取默认实例
          try {
            Method method = innerClass.getDeclaredMethod("getDefaultInstance");
            // 执行方法
            Object defaultInstance = method.invoke(null);
            // 添加消息编号和消息实例添加到 map
            MSGCODE_AND_MSGOBJ_MAP.put(msgCode.getNumber(), (GeneratedMessageV3) defaultInstance);
            
            // 添加到消息类型与消息编号的 map 中
            MSGCLASS_AND_MSGCODE_MAP.put(innerClass, msgCode.getNumber());
          } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
          }
          break;
        }
      }
      
    }
    
    log.info("消息编号与实例 map ：{}", MSGCODE_AND_MSGOBJ_MAP);
    log.info("消息类型与消息编号 map ：{}", MSGCLASS_AND_MSGCODE_MAP);
    
  }
  
  public static void main(String[] args) {
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
    return MSGCLASS_AND_MSGCODE_MAP.getOrDefault(clazz, -1);
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
