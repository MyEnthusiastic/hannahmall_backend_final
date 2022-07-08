package com.hannah.hannahmall.search.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@Slf4j
public class HannahmallsessionConfiguration {

    //采用json序列化
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
    @Bean
    public CookieSerializer cookieSerializer() {
        log.info("设置SpringSession的cookie序列化器,CookieName:{},DomainName:{}","hannahmallSESSION","hannahmall.com");
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("hannahmallSESSION");
        serializer.setDomainName("hannahmall.com");
        return serializer;
    }
}
