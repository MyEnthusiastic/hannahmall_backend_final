package com.hannah.hannahmall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRedisHttpSession//整合redis作为session存储
@EnableFeignClients(basePackages = {"com.hannah.hannahmall.common.feign"})
public class HannahmallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(HannahmallSearchApplication.class, args);
    }

}
