package com.liukai.advance.zkweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zkclient.IZkDataListener;
import com.github.zkclient.ZkClient;
import com.liukai.advance.zookeeper.Agent;
import com.liukai.advance.zookeeper.OsBean;
import com.liukai.advance.zookeeper.ZookeeperTest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainControl implements InitializingBean {
  
  private static final String rootPath = Agent.ROOT_PATH;
  
  private final Map<String, OsBean> map = new HashMap<>();
  
  private final String server = ZookeeperTest.ZookeeperHolder.CONNECT_STR;
  
  private ZkClient zkClient;
  
  @RequestMapping("/list")
  public String list(Model model) {
    model.addAttribute("items", getCurrentOsBeans());
    return "list";
  }
  
  private List<OsBean> getCurrentOsBeans() {
    return zkClient.getChildren(rootPath).stream().map(p -> rootPath + "/" + p)
      .map(p -> convert(new String(zkClient.readData(p), StandardCharsets.UTF_8)))
      .collect(Collectors.toList());
    // return Collections.emptyList();
  }
  
  private OsBean convert(String json) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(json, OsBean.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public void afterPropertiesSet() throws Exception {
    zkClient = new ZkClient(server, 5000, 10000);
    // 初始化订阅事件
    initSubscribeListener();
    
  }
  
  // 初始化订阅事件
  private void initSubscribeListener() {
    // 先取消所有的订阅者
    zkClient.unsubscribeAll();
    // 获取所有子节点
    zkClient.getChildren(rootPath).stream().map(p -> rootPath + "/" + p)// 得出子节点完整路径
      .forEach(p -> {
        // 订阅子节点数据变化事件
        zkClient.subscribeDataChanges(p, new DataChanges());// 数据变更的监听
      });
    //  监听子节点的变更事件，包括：增加，删除
    zkClient.subscribeChildChanges(rootPath,
                                   (parentPath, currentChildrenList) -> initSubscribeListener());
  }
  
  // 警告过滤
  private void doFilter(OsBean bean) {
    // cpu 超过10% 报警
    if (bean.getCpu() > 10) {
      System.err.println("CPU 报警..." + bean.getCpu());
    }
  }
  
  /**
   * 子节点数据变化监听器
   */
  private class DataChanges implements IZkDataListener {
    
    @Override
    public void handleDataChange(String dataPath, byte[] data) throws Exception {
      OsBean bean = convert(new String(data, StandardCharsets.UTF_8));
      map.put(dataPath, bean);
      doFilter(bean);
    }
    
    @Override
    public void handleDataDeleted(String dataPath) throws Exception {
      if (map.containsKey(dataPath)) {
        OsBean bean = map.get(dataPath);
        System.err.println("服务已下线:" + bean);
        map.remove(dataPath);
      }
    }
    
  }
  
}
