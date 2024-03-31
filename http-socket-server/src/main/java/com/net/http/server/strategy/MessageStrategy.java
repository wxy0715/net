package com.net.http.server.strategy;


import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.net.Socket;


/**
 * 消息处理策略
 * @author wxy
 */
public interface MessageStrategy<T> extends InitializingBean {
    /**
     * 对接收客户端的消息进行处理
     * @param t            实体类
     * @param clientSocket 客户端socket
     * @param sessionId    客户端标识
     * @throws IOException 异常
     */
    void execute(T t, Socket clientSocket, String sessionId) throws IOException;
}
