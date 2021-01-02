package com.liukai.advance.distribution.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liukai.advance.distribution.web.entity.ReadBookPd;

/**
 * (ReadBookPd)表服务接口
 *
 * @author liukai
 * @since 2021-01-02 21:44:38
 */
public interface ReadBookPdService extends IService<ReadBookPd> {
  
  void putAllDataToElasticsearch();
  
}
