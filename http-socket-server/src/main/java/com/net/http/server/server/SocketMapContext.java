package com.net.http.server.server;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * socket缓存上下文
 * @author wxy
 */
public class SocketMapContext {
    /**
     * socket缓存
     * key 客户端标识
     * value -》 key 用户端标识，value用户端套接字
     */
    public static final Map<String, Map<String, Socket>> SOCKET_MAP = new ConcurrentHashMap<>();

}
