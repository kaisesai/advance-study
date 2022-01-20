package com.kaige.distribution.transaction.pay;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableDistributedTransaction
@EnableEurekaClient
@EnableScheduling
@SpringBootApplication
@MapperScan("com.kaige.distribution.transaction.pay.dao")
public class PayApplication {

  public static void main(String[] args) {
    SpringApplication.run(PayApplication.class, args);
  }
}
