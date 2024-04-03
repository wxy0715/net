package com.net.http.server.client;

import com.alibaba.fastjson.JSON;
import com.net.common.constant.MessageType;
import com.net.common.entity.Message;
import com.net.common.exception.NetRuntimeException;
import com.net.common.util.CloseableUtils;
import com.net.common.util.HttpMessageUtil;
import com.net.http.server.strategy.MessageStrategy;
import com.net.http.server.strategy.impl.MessageContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 监听客户端消息
 * @author wxy
 */
@Slf4j
public class ServerSocketRunnable implements Runnable{
    private final Socket clientSocket;
    private final InputStream clientIn;
    private final OutputStream clientOut;
    private final String sessionId;

    @SneakyThrows
    public ServerSocketRunnable(Socket socket, String sessionId) {
        this.clientSocket = socket;
        this.clientIn = socket.getInputStream();
        this.clientOut = socket.getOutputStream();
        this.sessionId = sessionId;
    }

    @Override
    public void run() {
        try {
            while (!clientSocket.isClosed() && clientSocket.isConnected()) {
                try {
                    Message message = HttpMessageUtil.readMessage(clientIn);
                    if (MessageType.CLOSE.equals(message.getMessageType())) {
                        break;
                    }
                    if (!MessageType.HTTP_FORWARD.equals(message.getMessageType())) {
                        log.info("服务端收到握手消息[{}]", JSON.toJSONString(message));
                    }
                    // 根据不同消息类型发送消息
                    MessageStrategy<Message> iMessageStrategy = MessageContext.getMessageStrategy(message.getMessageType());
                    iMessageStrategy.execute(message, clientSocket, sessionId);
                } catch (Exception e) {
                    if (e instanceof NetRuntimeException) {
                        HttpMessageUtil.sendMessage(clientOut, () -> new Message(MessageType.ERROR, e.getMessage().getBytes(StandardCharsets.UTF_8)));
                    } else {
                        log.error("关闭客户端{}, 原因：{}", clientSocket.getPort(), e.getMessage(), e);
                        HttpMessageUtil.sendMessage(clientOut, () -> new Message(MessageType.ERROR, "服务器错误".getBytes(StandardCharsets.UTF_8)));
                    }
                    break;
                }
            }
        } finally {
            // 关闭缓存并释放
            CloseableUtils.close(clientOut, clientIn, clientSocket);
        }
    }
}
