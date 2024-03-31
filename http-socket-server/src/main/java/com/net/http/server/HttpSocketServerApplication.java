package com.net.http.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 启动类
 * @author wxy
 */
@SpringBootApplication
public class HttpSocketServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(HttpSocketServerApplication.class, args);
    }
}
