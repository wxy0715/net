package com.net.http.client.config;

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

    /**
     * 服务端ip
     */
    @Value("${serverIp}")
    private String serverIp;
    /**
     * 服务端端口
     */
    @Value("${serverPort}")
    private int serverPort;
    /**
     * 本地ip
     */
    @Value("${localIp}")
    private String localIp;
    /**
     * 本地端口
     */
    @Value("${localPort}")
    private int localPort;

}
