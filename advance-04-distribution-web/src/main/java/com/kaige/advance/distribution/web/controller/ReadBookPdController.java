package com.kaige.advance.distribution.web.controller;

import com.kaige.advance.distribution.web.entity.ReadBookPd;
import com.kaige.advance.distribution.web.service.ReadBookPdService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * (ReadBookPd)表控制层
 *
 * @author kaige
 * @since 2021-01-02 21:44:14
 */
@Slf4j
@RestController
@RequestMapping("/readBookPd")
public class ReadBookPdController {
  
  /** 服务对象 */
  @Resource
  private ReadBookPdService readBookPdService;
  
  @Autowired
  private ElasticsearchRestTemplate elasticsearchRestTemplate;
  
  /**
   * 通过主键查询单条数据
   *
   * @return 单条数据
   */
  @GetMapping(value = "/putAllDataToEs")
  public String selectOne() {
    this.readBookPdService.putAllDataToElasticsearch();
    return "ok";
  }
  
  @GetMapping(value = "/search")
  public Object search(@RequestParam(value = "key_word") String keyWord) {
    // 查询设置
    MatchQueryBuilder builder = QueryBuilders.matchQuery("name", keyWord);
    // TermQueryBuilder builder = QueryBuilders.termQuery("name", keyWord);
    StringQuery stringQuery = new StringQuery(builder.toString());
    // 高亮设置
    HighlightQuery highlightQuery = new HighlightQuery(
      new HighlightBuilder().field("name").preTags("<h1 style='red'>").postTags("</h1>"));
    stringQuery.setHighlightQuery(highlightQuery);
    return elasticsearchRestTemplate.search(stringQuery, ReadBookPd.class);
  }
  
  @GetMapping(value = "/zero_error")
  public void zeroError() {
    try {
      int i = 1 / 0;
    } catch (Exception e) {
      log.error("zero_error error", e);
    }
  }
  
}
