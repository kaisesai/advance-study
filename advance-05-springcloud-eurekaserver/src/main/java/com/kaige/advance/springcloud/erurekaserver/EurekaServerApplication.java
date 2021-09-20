package com.kaige.advance.springcloud.erurekaserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.eureka.server.EurekaServerConfigBean;

@Slf4j
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication implements ApplicationRunner {
  
  @Autowired
  private EurekaServerConfigBean eurekaServerConfigBean;
  
  public static void main(String[] args) {
    new SpringApplicationBuilder(EurekaServerApplication.class).web(WebApplicationType.SERVLET)
      .run(args);
  }
  
  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("eurekaServerConfigBean 配置信息： {}", eurekaServerConfigBean);
  }
  
}
