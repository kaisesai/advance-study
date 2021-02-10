package com.kaige.advance.netty.herostory.async;

/**
 * 异步操作接口
 */
public interface IAsyncOperation {
  
  /**
   * 获取绑定 id
   * <p>
   * 用于选择处理器
   *
   * @return
   */
  default int getBindId() {
    return 0;
  }
  
  /**
   * 处理业务
   */
  void doAsync();
  
  /**
   * 完成操作
   */
  default void doFinish() {
  }
  
}
