package com.kaige.advance.distribution.web.schedule;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.kaige.advance.distribution.web.entity.ReadBookPd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时查询 canal 数据
 */
// @Component
@Slf4j
public class CanalScheduling {
  
  @Autowired
  private CanalConnector canalBookConnector;
  
  @Autowired
  private ElasticsearchRestTemplate elasticsearchRestTemplate;
  
  @Scheduled(fixedDelay = 500)
  public void updateBookData() {
    long batchId = -1;
    try {
      // 一次取1000条数据
      int batchSize = 1000;
      Message message = canalBookConnector.getWithoutAck(batchSize);
      batchId = message.getId();
      List<CanalEntry.Entry> entries = message.getEntries();
      if (batchId != -1 && entries.size() > 0) {
        entries.forEach(entry -> {
          if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
            // 解析处理
            publishCanalEvent(entry);
          }
        });
      }
      // 提交确认消费完毕
      canalBookConnector.ack(batchId);
    } catch (Exception e) {
      e.printStackTrace();
      // 失败的话进行回滚
      canalBookConnector.rollback(batchId);
    }
    
  }
  
  private void publishCanalEvent(CanalEntry.Entry entry) {
    log.info("收到canal消息：{}", entry.toString());
    if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
      || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
      return;
    }
    // 拿出监听到的数据库
    String database = entry.getHeader().getSchemaName();
    // 拿出有变更的数据表
    String table = entry.getHeader().getTableName();
    CanalEntry.RowChange change = null;
    try {
      change = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
      return;
    }
    CanalEntry.EventType eventType = change.getEventType();
    change.getRowDatasList().forEach(rowData -> {
      
      List<CanalEntry.Column> columns = null;
      // 对于es来说 只要关注 delete 和 update 还有insert
      if (eventType == CanalEntry.EventType.DELETE) {
        // 为什么这里是before
        columns = rowData.getBeforeColumnsList();
      } else {
        // 其他的都是 after
        columns = rowData.getAfterColumnsList();
      }
      // 解析成map 格式
      Map<String, Object> dataMap = parseColumnsToMap(columns);
      try {
        // 真正的去改es
        indexES(dataMap, database, table, eventType);
      } catch (IOException e) {
        e.printStackTrace();
      }
      
    });
  }
  
  Map<String, Object> parseColumnsToMap(List<CanalEntry.Column> columns) {
    Map<String, Object> jsonMap = new HashMap<>();
    columns.forEach(column -> {
      if (column == null) {
        return;
      }
      jsonMap.put(column.getName(), column.getValue());
    });
    return jsonMap;
  }
  
  private void indexES(Map<String, Object> dataMap, String database, String table,
                       CanalEntry.EventType eventType) throws IOException {
    try {
      Object id = dataMap.get("id");
      if (eventType == CanalEntry.EventType.DELETE) {
        log.info("删除索引Id={},type={},value={}", id, eventType.toString(), dataMap.toString());
        // 删除 es 文档
        elasticsearchRestTemplate.delete(id.toString(), ReadBookPd.class);
      } else {
        // 这里又两种方式,一种是直接拿canal过来的数据，还有一种就是拿主键id去查询。
        // 如果是又业务关联的 这里就要写自己的业务代码
        log.info("更新索引Id={},type={},value={}", dataMap.get("id"), eventType.toString(),
                 dataMap.toString());
        
        IndexQuery query = new IndexQueryBuilder().withId(id.toString()).withObject(dataMap)
          .build();
        // 如果有个多个索引，都要修改
        elasticsearchRestTemplate.index(query, IndexCoordinates.of("book"));
        
        // 处理多业务的思路
        // 1.监听的是商品表 变更时我会拿到商品id
        // 2.根据goodsId 去营销中心 订单中心 查询数据 会调用他们的接口
        // 3.组装数据 进入es
      }
      
    } catch (Exception e) {
      log.error("定时刷新 book 数据到 es 出现异常", e);
    }
    
  }
  
}
