package com.example.demo.config;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Min;

import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.core.style.ToStringCreator;
import org.springframework.validation.annotation.Validated;

import reactor.core.publisher.Mono;

public class ThurrioRateLimiter  extends AbstractRateLimiter<ThurrioRateLimiter.Config> {

  public static final String CONFIGURATION_PROPERTY_NAME = "thurrio-rate-limiter";

  private ThurrioRateLimiter.Config defaultConfig;
//  private Log log = LogFactory.getLog(getClass());

  public ThurrioRateLimiter(Class<Config> configClass, String configurationPropertyName, ConfigurationService configurationService) {
    super(configClass, configurationPropertyName, configurationService);
  }

  public ThurrioRateLimiter(Class<ThurrioRateLimiter.Config> configClass, ConfigurationService configurationService) {
    super(configClass, CONFIGURATION_PROPERTY_NAME, configurationService);
  }

  public ThurrioRateLimiter(int refresh, int maxRequests) {
    super(ThurrioRateLimiter.Config.class, CONFIGURATION_PROPERTY_NAME, (ConfigurationService) null);
    this.defaultConfig = new ThurrioRateLimiter.Config().setRefresh(refresh).setMaxRequests(maxRequests);
  }

  @Override
  public Mono<Response> isAllowed(String routeId, String id) {

    ThurrioRateLimiter.Config routeConfig = loadConfiguration(routeId);

    // How many requests per second do you want a user to be allowed to do?
    int refresh = routeConfig.getRefresh();

    // How much bursting do you want to allow?
    int getMaxRequests = routeConfig.getMaxRequests();

    return Mono.just(new Response(true, getHeaders()));
  }

  public Map<String, String> getHeaders() {
    Map<String, String> headers = new HashMap<>();

    return headers;
  }

  /* for testing */ ThurrioRateLimiter.Config loadConfiguration(String routeId) {
    ThurrioRateLimiter.Config routeConfig = getConfig().getOrDefault(routeId, defaultConfig);

    if (routeConfig == null) {
      routeConfig = getConfig().get(RouteDefinitionRouteLocator.DEFAULT_FILTERS);
    }

    if (routeConfig == null) {
      throw new IllegalArgumentException("No Configuration found for route " + routeId + " or defaultFilters");
    }
    return routeConfig;
  }

  @Validated
  public static class Config {

    @Min(0)
    private int maxRequests = 1;

    @Min(1)
    private int refresh = 1;

    public int getMaxRequests() {
      return maxRequests;
    }

    public ThurrioRateLimiter.Config setMaxRequests(int maxRequests) {
      this.maxRequests = maxRequests;
      return this;
    }

    public int getRefresh() {
      return refresh;
    }

    public ThurrioRateLimiter.Config setRefresh(int refresh) {
      this.refresh = refresh;
      return this;
    }

    @Override
    public String toString() {
      return new ToStringCreator(this)
          .append("burstCapacity", maxRequests).append("requestedTokens", refresh).toString();

    }

  }

}
