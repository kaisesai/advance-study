package com.kaige.advance.springcloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** 网关 */
@SpringBootApplication
public class GatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
  }

  // @Bean
  // public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
  //   RouteLocatorBuilder.Builder routes = builder.routes();
  //   // routes.route("path_route", r -> r.path("/get").uri("http://httpbin.org"));
  //   routes.route("path_route", r -> r.path("/get").uri("http://baidu.com"));
  //   routes.route("host_route", r -> r.host("*.myhost.org").uri("http://httpbin.org"));
  //   routes.route("rewrite_route", r -> r.host("*.rewrite.org")
  //     .filters(f -> f.rewritePath("/foo/(?<segment>.*)",
  // "/${segment}")).uri("http://httpbin.org"));
  //   // routes.route("hystrix_route",
  //   //              r -> r.host("*.hystrix.org").filters(f -> f.hystrix(c ->
  // c.setName("slowcmd")))
  //   //                .uri("http://httpbin.org"));
  //   // routes.route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org")
  //   //   .filters(f -> f.hystrix(c ->
  // c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback")))
  //   //   .uri("http://httpbin.org")).route("limit_route",
  //   //                                     r ->
  // r.host("*.limited.org").and().path("/anything/**")
  //   //                                       .filters(f -> f.requestRateLimiter(
  //   //                                         c -> c.setRateLimiter(redisRateLimiter())))
  //   //                                       .uri("http://httpbin.org"));
  //
  //   return routes.build();
  // }

}
