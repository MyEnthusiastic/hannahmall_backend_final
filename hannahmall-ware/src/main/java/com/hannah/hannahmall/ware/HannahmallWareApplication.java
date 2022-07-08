package com.hannah.hannahmall.ware;

import com.hannah.hannahmall.ware.config.DataSourceProxyAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;


@MapperScan("com.hannah.hannahmall.ware.dao")
@EnableDiscoveryClient
@Import({DataSourceProxyAutoConfiguration.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},
        scanBasePackages = {"com.hannah.hannahmall.ware","com.hannah.hannahmall.common.feign"})
@EnableRabbit
@EnableFeignClients(basePackages = {"com.hannah.hannahmall.common.feign"})
@EnableAsync
public class HannahmallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(HannahmallWareApplication.class, args);
    }
    @Bean
    public MessageConverter createMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
