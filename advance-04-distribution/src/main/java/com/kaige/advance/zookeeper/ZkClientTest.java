package com.kaige.advance.zookeeper;

import com.github.zkclient.IZkDataListener;
import com.github.zkclient.ZkClient;

import java.nio.charset.StandardCharsets;

/**
 * 第三方客户端 ZkClient
 */
public class ZkClientTest {
  
  public static void main(String[] args) throws InterruptedException {
    ZkClient zkClient = new ZkClient(ZookeeperHolder.CONNECT_STR, 5000);
    byte[] bytes = zkClient.readData("/kaisai");
    System.out.println("bytes = " + new String(bytes, StandardCharsets.UTF_8));
    
    zkClient.subscribeDataChanges("/kaisai", new IZkDataListener() {
      @Override
      public void handleDataChange(String dataPath, byte[] data) throws Exception {
        System.out.println(
          "handleDataChange dataPath = " + dataPath + ", data = " + new String(data,
                                                                               StandardCharsets.UTF_8));
      }
      
      @Override
      public void handleDataDeleted(String dataPath) throws Exception {
        System.out.println("handleDataDeleted dataPath = " + dataPath);
      }
    });
    Thread.sleep(100 * 1000);
  }
  
}
