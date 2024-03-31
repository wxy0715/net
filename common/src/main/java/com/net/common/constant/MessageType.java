package com.net.common.constant;

/**
 * 消息类型
 * @author wxy
 */
public interface MessageType {
    /**
     * 客户端通知服务器开启新的socket处理浏览器请求
     */
    String NEW_SOCKET = "new_socket";

    /**
     * 服务的转发浏览器的消息
     */
    String HTTP_FORWARD = "http_forward";

    /**
     * 关闭连接消息
     */
    String CLOSE = "close";

    /**
     * 服务端错误消息
     */
    String ERROR = "error";
}
