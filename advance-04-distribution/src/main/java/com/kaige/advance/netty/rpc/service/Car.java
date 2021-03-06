package com.kaige.advance.netty.rpc.service;

/** Car 接口 */
public interface Car {
  
  /**
   * 行驶
   *
   * @param name
   * @return
   */
  String drive(String name);
  
  /**
   * 获取司机信息
   *
   * @param name
   * @return
   */
  Person getDriver(String name);
  
}
