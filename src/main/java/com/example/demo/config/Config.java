package com.example.demo.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;

@Configuration
public class Config {

  @Bean
  public KeyResolver userKeyResolver() {
    return exchange -> Mono.just("1");
  }

  @Bean
  public RateLimiter myRateLimiter() {
    return new ThurrioRateLimiter(10,10);
  }
}
