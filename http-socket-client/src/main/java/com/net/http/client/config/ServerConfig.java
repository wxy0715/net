package com.net.http.client.config;

import lombok.Data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * 服务端配置
 * @author wxy
 */
@Data
public class ServerConfig {

    /**
     * 服务端ip
     */
    private String serverIp;
    /**
     * 服务端端口
     */
    private int serverPort;
    /**
     * 本地ip
     */
    private String localIp;
    /**
     * 本地端口
     */
    private int localPort;

    public static ServerConfig getInstance() {
        Properties properties = new Properties();
        ServerConfig serverConfig = new ServerConfig();
        try {
            //加载配置文件
            properties.load(new InputStreamReader(Objects.requireNonNull(ServerConfig.class.getClassLoader().getResourceAsStream("application.properties")), StandardCharsets.UTF_8));
            serverConfig.setServerIp(properties.getProperty("serverIp"));
            serverConfig.setServerPort(Integer.parseInt(properties.getProperty("serverPort")));
            serverConfig.setLocalIp(properties.getProperty("localIp"));
            serverConfig.setLocalPort(Integer.parseInt(properties.getProperty("localPort")));
        } catch (IOException e) {
            throw new RuntimeException("加载配置文件失败"+e);
        }
        return serverConfig;
    }
}
