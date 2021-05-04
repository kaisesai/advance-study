package com.kaige.advance.zookeeper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zkclient.ZkClient;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Agent {
  
  public static final String ROOT_PATH = "/kaisai/manager";
  
  public static final String SERVICE_PATH = ROOT_PATH + "/service";
  
  private static Agent instance;
  
  private String server = ZookeeperHolder.CONNECT_STR;
  
  private ZkClient zkClient;
  
  private String nodePath;
  
  private Thread stateThread;
  
  // private List<OsBean> list = new ArrayList<>();
  
  private Agent() {
  }
  
  public static void main(String[] args) {
    premain(null, null);
  }
  
  public static void premain(String args, Instrumentation instrumentation) {
    instance = new Agent();
    if (args != null) {
      instance.server = args;
    }
    instance.init();
  }
  
  // 初始化连接
  private void init() {
    zkClient = new ZkClient(server, 1000 * 5, 1000 * 10);
    System.out.println("zk 连接成功" + server);
    // 构建根节点
    buildRoot();
    // 创建服务节点
    createServerNode();
    
    // 开启新的线程监听服务节点
    stateThread = new Thread(() -> {
      
      while (true) {
        // 更新服务节点
        updateServerNode();
        try {
          Thread.sleep(5000);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      
    }, "zk_stat_thread");
    // 设置守护线程
    stateThread.setDaemon(true);
    // stateThread.setDaemon(true);
    // 启动线程
    stateThread.start();
  }
  
  /**
   * 更细服务节点信息
   */
  private void updateServerNode() {
    // 获取系统信息并写入节点数据
    zkClient.writeData(nodePath, getOsInfo());
  }
  
  /**
   * 创建服务节点
   */
  private void createServerNode() {
    // 创建虚拟的序列节点，同时设置操作系统属性
    nodePath = zkClient.createEphemeralSequential(SERVICE_PATH, getOsInfo());
    System.out.println("创建节点：" + nodePath);
  }
  
  private byte[] getOsInfo() {
    OsBean osBean = new OsBean();
    // 设置最后更新时间
    osBean.setLastUpdateTime(System.currentTimeMillis());
    // 设置服务 ip
    osBean.setIp(getLocalIp());
    
    OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
    System.out.println("operatingSystemMXBean = " + operatingSystemMXBean);
    
    // 设置服务 CPU 数量
    osBean.setCpu(operatingSystemMXBean.getAvailableProcessors());
    
    MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
    System.out.println("heapMemoryUsage = " + heapMemoryUsage);
    
    // 设置已使用堆内存大小，单位 MB
    osBean.setUsableMemorySize(heapMemoryUsage.getUsed() / 1024 / 1024);
    // 设置最大堆内存大小，单位 MB
    osBean.setMaxMemorySize(heapMemoryUsage.getMax() / 1024 / 1024);
    
    // 将 bean 转换为 json 字符串
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      String value = objectMapper.writeValueAsString(osBean);
      System.out.println("getOsInfo = " + value);
      return value.getBytes(StandardCharsets.UTF_8);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("json write fail", e);
    }
  }
  
  private String getLocalIp() {
    try {
      InetAddress address = InetAddress.getLocalHost();
      System.out.println("address = " + address);
      return address.getHostAddress();
    } catch (UnknownHostException e) {
      throw new RuntimeException();
    }
  }
  
  /**
   * 更新节点数据
   *
   * @param path
   * @param data
   */
  public void updateNode(String path, byte[] data) {
    if (zkClient.exists(path)) {
      // 节点存在则更新数据
      zkClient.writeData(path, data);
    } else {
      // 节点不存在，则创建
      zkClient.createEphemeral(path, data);
    }
  }
  
  private void buildRoot() {
    if (!zkClient.exists(ROOT_PATH)) {
      // 创建持久节点，同时创建父节点
      zkClient.createPersistent(ROOT_PATH, true);
    }
  }
  
}
