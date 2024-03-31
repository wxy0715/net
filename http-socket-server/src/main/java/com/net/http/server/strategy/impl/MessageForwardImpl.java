package com.net.http.server.strategy.impl;

import com.net.common.constant.MessageType;
import com.net.common.entity.Message;
import com.net.common.util.ThreadPoolFactory;
import com.net.http.server.server.SocketMapContext;
import com.net.http.server.strategy.MessageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

/**
 * 处理转发消息
 * @author wxy
 */
@Service(MessageType.HTTP_FORWARD)
@Slf4j
public class MessageForwardImpl implements MessageStrategy<Message> {
    @Override
    public void execute(Message message, Socket clientSocket, String sessionId) {
        // 获取浏览器请求的Socket
        Socket socketWan = SocketMapContext.SOCKET_MAP.get(message.getSessionId()).get(message.getSocketWanId());
        if (Objects.isNull(socketWan)) {
            log.info("socketWan 已经被关闭 sessionId:{},socketWanId:{}", message.getSessionId(), message.getSocketWanId());
            return;
        }
        // 返回数据给浏览器
        ThreadPoolFactory.execute(() -> {
            try (OutputStream outputStream = socketWan.getOutputStream()){
                outputStream.write(message.getBody());
            } catch (IOException e) {
                log.error("转发内网消息失败!",e);
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws IllegalAccessException {
        MessageContext.register(MessageType.HTTP_FORWARD, this);
    }

}
