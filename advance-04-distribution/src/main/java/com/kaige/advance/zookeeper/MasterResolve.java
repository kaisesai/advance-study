package com.kaige.advance.zookeeper;

import com.github.zkclient.ZkClient;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * master 选择器
 */
public class MasterResolve {
  
  public static final String ZK_CONNECT = ZookeeperHolder.CONNECT_STR;
  
  public static final String ROOT_PATH = "/kaisai/master";
  
  public static final String SERVER_PATH = ROOT_PATH + "/service";
  
  public static final byte[] SLAVE_BYTES = "slave".getBytes(StandardCharsets.UTF_8);
  
  public static final byte[] MASTER_BYTES = "master".getBytes(StandardCharsets.UTF_8);
  
  private static final MasterResolve resolve = new MasterResolve();
  
  private ZkClient zkClient;
  
  private volatile boolean isMaster = false;
  
  private String nodePath;
  
  private MasterResolve() {
    init();
  }
  
  public static MasterResolve getInstance() {
    return resolve;
  }
  
  public void init() {
    // 初始化 zk
    zkClient = new ZkClient(ZK_CONNECT, 5000, 10000);
    // 构建根节点
    buildRoot();
    // 构建服务节点
    buildServiceNode();
    // 初始化选举 master
    initMaster();
    // 监听变更事件
    subscribeServiceNode();
  }
  
  /**
   * 监听所有的服务节点事件
   */
  private void subscribeServiceNode() {
    // 监听所有服务节点变更事件
    zkClient.subscribeChildChanges(ROOT_PATH, (parentPath, currentChildren) -> {
      System.out.println("监听到服务节点变化：currentChildren = " + currentChildren);
      boolean presentMaster = isPresentMaster(currentChildren);
      if (!presentMaster) {
        doResolve();
      }
    });
  }
  
  private void initMaster() {
    // 获取所有的服务节点，并且获取它们的数据，判断有没有 master 数据
    List<String> childrenList = zkClient.getChildren(ROOT_PATH);
    boolean presentMaster = isPresentMaster(childrenList);
    // 不存在则进行选举
    if (!presentMaster) {
      // 获取自己的节点，写入数据到节点
      // 为了确保足够安全，需要再查一次
      doResolve();
    }
  }
  
  private boolean isPresentMaster(List<String> childrenList) {
    // 查询所有的服务节点判断是否有无 master 数据
    return childrenList.stream().map(p -> ROOT_PATH + "/" + p).map(p -> zkClient.readData(p))
      .anyMatch(bytes -> Arrays.equals(bytes, MASTER_BYTES));
  }
  
  private void doResolve() {
    // 查找根节点下的第一个序号节点
    Optional<String> firstPath = zkClient.getChildren(ROOT_PATH).stream()
      // 按照节点顺序排序，必须排序，因为查询出来的结果每次都是无序的，不排序可能会出现第一个结果永远不是自己的去情况
      .sorted()
      // 查找子节点
      .map(p -> ROOT_PATH + "/" + p)
      // 查找第一个
      .findFirst()
      // 过滤不是自己的节点
      .filter(nodePath::equals);
    
    // 自己只选举自己，不管其他人
    firstPath.ifPresent(p -> {
      // first 节点数据不是 master
      if (!Arrays.equals(MASTER_BYTES, zkClient.readData(p))) {
        // 设置 master 到该节点上
        zkClient.writeData(p, MASTER_BYTES);
        // 设置标识
        isMaster = true;
        System.out.println("选举master节点成功, nodePath: " + nodePath);
      }
    });
  }
  
  public boolean isMaster() {
    return isMaster;
  }
  
  private void buildServiceNode() {
    // 创建临时序号节点，节点值为 slave
    nodePath = zkClient.createEphemeralSequential(SERVER_PATH, SLAVE_BYTES);
    System.out.println("创建服务节点 nodePath = " + nodePath);
  }
  
  private void buildRoot() {
    if (!zkClient.exists(ROOT_PATH)) {
      zkClient.createPersistent(ROOT_PATH, true);
    }
    System.out.println("创建根节点：" + ROOT_PATH);
  }
  
}
