package com.kaige.distribution.transaction.lcn;

import com.codingapi.txlcn.tm.config.EnableTransactionManagerServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 启动类 @EnableTransactionManagerServer 启动LCN-TM注解服务 @EnableEurekaClient 注入到 eureka 服务
 *
 * @author liukai
 * @date 2021年12月12日
 */
@EnableTransactionManagerServer
@EnableEurekaClient
@SpringBootApplication
public class LCNApplication {

  public static void main(String[] args) {
    SpringApplication.run(LCNApplication.class, args);
  }
}
