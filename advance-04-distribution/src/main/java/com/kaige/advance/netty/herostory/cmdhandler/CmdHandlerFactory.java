package com.kaige.advance.netty.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import com.kaige.advance.netty.herostory.util.PackageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 命令处理器工厂
 */
@Slf4j
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
    log.info("初始化命令处理器工厂");
    // 通过扫描指定路径下的类文件来通过反射生成对应的类
    Set<Class<?>> subClazz = PackageUtil
      .listSubClazz(CmdHandlerFactory.class.getPackage().getName(), true, ICmdHandler.class);
    
    // 遍历类获取类的泛型参数类型，并且实例化该类，将该类实例与它实际泛型参数一起维护在 map 中
    for (Class<?> clazz : subClazz) {
      // 过滤掉抽象类型以及接口
      if ((clazz.getModifiers() & Modifier.ABSTRACT) >= 1) {
        continue;
      }
      
      // 方法一：通过获取 class 的泛型接口，再获取它的参数化泛型，注意它可能实现了多个泛型化的接口，而且有时并没有参数化它们的泛型
      Class<?> cmdClass = getCmdClassByParameterizedType(clazz);
      // 方法二：根据指定方法的参数类型来实现
      // Class<?> cmdClass = getCmdClassByMethod(clazz);
      
      // 维护命令类型与命令处理器实例的关系
      putCmdClassAndHandlerInstance(clazz, cmdClass);
    }
    log.info("cmd 处理器 map：" + CMD_HANDLER_MAP);
  }
  
  /**
   * 方式一：通过泛型接口的参数化类型方式处理
   *
   * @param clazz
   * @return
   */
  private static Class<?> getCmdClassByParameterizedType(Class<?> clazz) {
    for (Type genericInterface : clazz.getGenericInterfaces()) {
      // 泛型接口必须是 ICmdHandler 类型
      if (!(genericInterface instanceof ParameterizedType)) {
        continue;
      }
      // 获取参数化泛型
      // 获取它的泛型实际类型
      Type[] actualTypeArguments = ((ParameterizedType) genericInterface).getActualTypeArguments();
      for (Type actualTypeArgument : actualTypeArguments) {
        if (actualTypeArgument instanceof Class) {
          return (Class<?>) actualTypeArgument;
        }
      }
    }
    return null;
  }
  
  /**
   * 维护命令类型与命令处理器实例的关系
   * <p>
   * 方法二：通过查询类的具体方法的参数类型的方式
   *
   * @param clazz
   * @return
   */
  private static Class<?> getCmdClassByMethod(Class<?> clazz) {
    for (Method method : clazz.getDeclaredMethods()) {
      // 判断方法名称
      if (!StringUtils.equals("handle", method.getName())) {
        continue;
      }
      // 参数类型
      Class<?>[] parameterTypes = method.getParameterTypes();
      // 第二个参数类型的值一定要是 GeneratedMessageV3 的子类
      if (parameterTypes.length < 1 || parameterTypes[1] == GeneratedMessageV3.class
        || !GeneratedMessageV3.class.isAssignableFrom(parameterTypes[1])) {
        continue;
      }
      return parameterTypes[1];
    }
    return null;
  }
  
  /**
   * 生成命令处理器实例并且把它与命令类型维护起来
   *
   * @param clazz         命令类型
   * @param parameterType 命令泛型参数
   */
  private static void putCmdClassAndHandlerInstance(Class<?> clazz, Class<?> parameterType) {
    if (Objects.isNull(clazz) || Objects.isNull(parameterType)) {
      return;
    }
    try {
      // 通过无参构造器类实例
      ICmdHandler<? extends GeneratedMessageV3> instance
        = (ICmdHandler<? extends GeneratedMessageV3>) clazz.getDeclaredConstructor().newInstance();
      // 保存参数类型与类实例的映射
      CMD_HANDLER_MAP.put(parameterType, instance);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      log.error(e.getMessage(), e);
    }
  }
  
  public static void main(String[] args) {
  
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
