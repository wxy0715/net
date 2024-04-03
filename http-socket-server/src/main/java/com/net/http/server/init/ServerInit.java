package com.net.http.server.init;

import cn.hutool.core.util.IdUtil;
import com.net.common.util.ThreadPoolFactory;
import com.net.http.server.client.ServerSocketRunnable;
import com.net.http.server.config.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * 服务启动初始化
 * @author wxy
 */
@Component
@Slf4j
public class ServerInit implements ApplicationRunner {
    @Resource
    private ServerConfig serverConfig;

    @Override
    public void run(ApplicationArguments args) {
        try (ServerSocket serverSocket = new ServerSocket(serverConfig.getHttpPort())){
            log.info("服务端监听端口{}", serverConfig.getHttpPort());
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // 启动新线程处理客户端连接
                    ThreadPoolFactory.execute(new ServerSocketRunnable(clientSocket, IdUtil.fastSimpleUUID()));
                } catch (IOException e) {
                    log.error("接收客户端连接时发生异常，活动线程数: {}", ThreadPoolFactory.getActiveCount(), e);
                    // 根据情况考虑是否需要关闭socket、重试等操作，这里使用简化的处理逻辑
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ie) {
                        // 保留中断状态
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("服务器异常退出", e);
        }
    }
}
