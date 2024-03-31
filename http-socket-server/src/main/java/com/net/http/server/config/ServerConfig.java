package com.net.http.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 服务端配置
 * @author wxy
 */
@Configuration
@Data
public class ServerConfig {

    @Value("${http.timeout}")
    private int httpTimeOut;

    @Value("${http.port}")
    private int httpPort;
}
