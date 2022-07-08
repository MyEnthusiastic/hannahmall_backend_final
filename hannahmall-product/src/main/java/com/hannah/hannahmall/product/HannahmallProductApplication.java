package com.hannah.hannahmall.product;

import com.hannah.hannahmall.product.config.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.net.InetAddress;
import java.net.UnknownHostException;


@MapperScan("com.hannah.hannahmall.product.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.hannah.hannahmall.common.feign"})
@EnableRedisHttpSession
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},
        scanBasePackages = {"com.hannah.hannahmall.common", "com.hannah.hannahmall.product"})
@Slf4j
public class HannahmallProductApplication {
    public static void main(String[] args) throws UnknownHostException {
        ApplicationContext applicationContext = SpringApplication.run(com.hannah.hannahmall.product.HannahmallProductApplication.class, args);
        SpringContextHolder springContextHolder = new SpringContextHolder();
        springContextHolder.setApplicationContext(applicationContext);
        Environment env = applicationContext.getEnvironment();
        log.info("\n-----------------------------------\n\t" +
                        "应用 '{}'运行成功,\n\t" +
                        "Swagger文档: \t\thttp://{}:{}/doc.html\n\t" +
                        "-----------------------------------------------",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port")
        );
    }


}
