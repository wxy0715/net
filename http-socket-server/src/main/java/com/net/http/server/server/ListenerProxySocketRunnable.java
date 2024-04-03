package com.net.http.server.server;

import cn.hutool.core.util.IdUtil;
import com.net.common.util.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端监听浏览器请求
 * @author wxy
 */
@Slf4j
public class ListenerProxySocketRunnable implements Runnable {
    /**
     * 客户端socket
     */
    private final Socket clientSocket;
    /**
     * 客户端标识
     */
    private final String sessionId;
    /**
     * 代理端口
     */
    private final int proxyPort;

    public ListenerProxySocketRunnable(Socket clientSocket, String sessionId, int proxyPort) {
        this.clientSocket = clientSocket;
        this.sessionId = sessionId;
        this.proxyPort = proxyPort;
    }

    @Override
    public void run() {
        try (ServerSocket listenerProxySocket = new ServerSocket(proxyPort)){
            while (true) {
                Socket socketWan;
                try {
                    socketWan = listenerProxySocket.accept();
                    socketWan.setSoTimeout(1000 * 60);
                    ThreadPoolFactory.execute(new SocketWanRunnable(socketWan, clientSocket, sessionId, addSocketWanMap(socketWan)));
                } catch (Exception e) {
                    log.error("代理服务{}异常", proxyPort);
                    break;
                }
            }
        } catch (IOException e) {
            log.info("代理服务{}已关闭", proxyPort);
        }
    }

    /**
     * 浏览器socket加入缓存
     * @param socketWan 浏览器socket
     * @return socketWanId
     */
    private String addSocketWanMap(Socket socketWan) {
        String socketWanId = IdUtil.fastSimpleUUID();
        if (Objects.isNull(SocketMapContext.SOCKET_MAP.get(sessionId))) {
            Map<String, Socket> socketMap = new ConcurrentHashMap<>(8);
            socketMap.put(socketWanId, socketWan);
            SocketMapContext.SOCKET_MAP.put(sessionId, socketMap);
        } else {
            Map<String, Socket> socketMap = SocketMapContext.SOCKET_MAP.get(sessionId);
            socketMap.put(socketWanId, socketWan);
        }
        return socketWanId;
    }
}
