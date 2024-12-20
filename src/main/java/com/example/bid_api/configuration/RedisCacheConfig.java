//package com.example.bid_api.configuration;
//
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//
//import java.time.Duration;
//
//@Configuration
//@EnableCaching
//public class RedisCacheConfig {
//
//    @Bean
//    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
//        // config TTL is 10 minutes for cache
//        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofDays(6))
//                .disableCachingNullValues() // dont cache null
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
//                        new org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer())); // Dùng JSON serializer
//
//        return RedisCacheManager.builder(redisConnectionFactory)
//                .cacheDefaults(cacheConfig)
//                .build();
//    }
//}
