package com.net.http.server.strategy.impl;


import com.net.common.entity.Message;
import com.net.http.server.strategy.MessageStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息上下文管理器
 * @author wxy
 */
@Component
public class MessageContext {
    private final static Map<String, MessageStrategy<Message>> SERVICES = new ConcurrentHashMap<>();

    /**
     * 获取消息实现
     */
    public static MessageStrategy<Message> getMessageStrategy(String messageType) throws IllegalAccessException {
        if (ObjectUtils.isEmpty(messageType)) {
            throw new IllegalAccessException("messageType不能为空");
        }
        return SERVICES.get(messageType);
    }

    /**
     * 注册消息
     */
    public static void register(String messageType, MessageStrategy<Message> iMessageStrategy) throws IllegalAccessException  {
        if (ObjectUtils.isEmpty(messageType)) {
            throw new IllegalAccessException("messageType不能为空");
        }
        SERVICES.put(messageType, iMessageStrategy);
    }
}
