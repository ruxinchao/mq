package com.rxc.rocketmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;

/**
 * @author rxc
 * @date 2024年5月20日11:04:44
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Main {


    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);
    }
}