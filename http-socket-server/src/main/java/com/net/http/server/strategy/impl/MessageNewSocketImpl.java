package com.net.http.server.strategy.impl;

import com.net.common.constant.Constant;
import com.net.common.constant.MessageType;
import com.net.common.entity.Message;
import com.net.common.util.HttpMessageUtil;
import com.net.common.util.HttpUtils;
import com.net.common.util.MapUtils;
import com.net.common.util.ThreadPoolFactory;
import com.net.http.server.config.ServerConfig;
import com.net.http.server.server.ListenerProxySocketRunnable;
import com.net.http.server.strategy.MessageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * 客户端通知服务器开启新的socket处理浏览器请求
 * @author wxy
 */
@Service(MessageType.NEW_SOCKET)
@Slf4j
public class MessageNewSocketImpl implements MessageStrategy<Message> {
    @Resource
    private ServerConfig serverConfig;

    @Override
    public void execute(Message message, Socket clientSocket, String sessionId) throws IOException {
        // 获取可用端口
        Integer port = HttpUtils.getCanUsePort();
        // 握手成功发送可用端口和超时信息
        HttpMessageUtil.sendMessage(clientSocket.getOutputStream(), () ->
                new Message(MessageType.NEW_SOCKET,
                new byte[0],
                () -> MapUtils.createHashMap(Arrays.asList(
                        MapUtils.createHashMap(Constant.TIME_OUT, String.valueOf(serverConfig.getHttpTimeOut())),
                        MapUtils.createHashMap(Constant.PORT, String.valueOf(port)))),
                sessionId));
        // 监听代理端口
        ThreadPoolFactory.execute(new ListenerProxySocketRunnable(clientSocket, sessionId, port));
    }

    @Override
    public void afterPropertiesSet() throws IllegalAccessException {
        MessageContext.register(MessageType.NEW_SOCKET, this);
    }
}
