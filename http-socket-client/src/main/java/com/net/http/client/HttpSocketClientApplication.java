package com.net.http.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 启动类
 * @author wxy
 */
@SpringBootApplication
@EnableScheduling
public class HttpSocketClientApplication {


    public static void main(String[] args) {
        SpringApplication.run(HttpSocketClientApplication.class, args);
    }
}
