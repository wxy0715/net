package com.net.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Supplier;


/**
 * 消息主体
 * @author wxy
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息长度
     */
    private int length;

    /**
     * 消息实体
     */
    private byte[] body;
    /**
     * 自定义header参数
     */
    private Map<String, String> headerMap;
    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 客户端标识
     */
    private String sessionId;
    /**
     * 与浏览器建立连接的客户端标识
     */
    private String socketWanId;

    /**
     * http载体
     */
    private HttpCarrier carrier;

    public Message(String messageType, byte[] body) {
        this.length = body.length;
        this.body = body;
        this.messageType = messageType;
    }

    public Message(String messageType, byte[] body, Map<String, String> headerMap) {
        this.length = body.length;
        this.headerMap = headerMap;
        this.body = body;
        this.messageType = messageType;
    }

    public Message(String messageType, byte[] body, Supplier<Map<String, String>> supplier, String sessionId) {
        this.length = body.length;
        this.headerMap = supplier.get();
        this.body = body;
        this.messageType = messageType;
        this.sessionId = sessionId;
    }

    public Message(String messageType, byte[] body, HttpCarrier carrier, String sessionId, String socketWanId) {
        this.length = body.length;
        this.carrier = carrier;
        this.body = body;
        this.messageType = messageType;
        this.sessionId = sessionId;
        this.socketWanId = socketWanId;
    }
}
