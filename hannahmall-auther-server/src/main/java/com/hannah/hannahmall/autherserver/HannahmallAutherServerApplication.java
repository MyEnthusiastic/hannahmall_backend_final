package com.hannah.hannahmall.autherserver;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication(scanBasePackages = {"com.hannah.hannahmall.autherserver","com.hannah.hannahmall.common.feign"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.hannah.hannahmall.common.feign"})
@EnableRedisHttpSession
public class HannahmallAutherServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HannahmallAutherServerApplication.class, args);
    }

}
