package com.liukai.advance.zookeeper;

import lombok.Data;

@Data
public class OsBean {
  
  private long lastUpdateTime;
  
  private String ip;
  
  private int cpu;
  
  private long usableMemorySize;
  
  private long maxMemorySize;
  
}
