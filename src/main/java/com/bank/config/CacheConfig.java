package com.bank.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 生产环境使用Redis做分布式缓存，
 * 这里模拟单节点服务，简化缓存一致性的处理逻辑，
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置交易信息缓存
     * 最大容量500条，过期时间5分钟
     * key是交易编号, value是交易信息obj
     */
    @Bean(name = "transactionByReferenceCache")
    public Cache<String, Object> transactionByReferenceCache() {
        return CacheBuilder.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats()
            .build();
    }
}
