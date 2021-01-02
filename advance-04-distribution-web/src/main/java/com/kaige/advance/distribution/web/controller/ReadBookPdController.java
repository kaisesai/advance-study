package com.kaige.advance.distribution.web.controller;

import com.kaige.advance.distribution.web.service.ReadBookPdService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * (ReadBookPd)表控制层
 *
 * @author kaige
 * @since 2021-01-02 21:44:14
 */
@RestController
@RequestMapping("readBookPd")
public class ReadBookPdController {
  
  /**
   * 服务对象
   */
  @Resource
  private ReadBookPdService readBookPdService;
  
  /**
   * 通过主键查询单条数据
   *
   * @param id 主键
   * @return 单条数据
   */
  @GetMapping("putAllDataToEs")
  public String selectOne() {
    this.readBookPdService.putAllDataToElasticsearch();
    return "ok";
  }
  
}
