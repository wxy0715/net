package com.net.http.server.server;

import com.net.common.constant.MessageType;
import com.net.common.entity.HttpCarrier;
import com.net.common.entity.Message;
import com.net.common.util.CloseableUtils;
import com.net.common.util.HttpMessageUtil;
import com.net.http.server.util.AnalysisHttpRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

/**
 * 接收浏览器消息
 * @author wxy
 */
@Slf4j
public class SocketWanRunnable implements Runnable {

    /**
     * 浏览器socket
     */
    private final Socket socketWan;
    /**
     * 客户端输出流
     */
    private final OutputStream clientOut;
    /**
     * 浏览器输入流
     */
    private final InputStream inWan;

    /**
     * 浏览器输入流
     */
    private final OutputStream outWan;
    /**
     * 客户端socket标识
     */
    private final String sessionId;
    /**
     * 浏览器socket标识
     */
    private final String socketWanId;


    @SneakyThrows
    public SocketWanRunnable(Socket socketWan, Socket clientSocket, String sessionId, String socketWanId) {
        this.socketWan = socketWan;
        this.clientOut = clientSocket.getOutputStream();
        this.inWan = socketWan.getInputStream();
        this.outWan = socketWan.getOutputStream();
        this.sessionId = sessionId;
        this.socketWanId = socketWanId;
    }

    @Override
    public void run() {
        while (!socketWan.isClosed() && socketWan.isConnected()) {
            try {
                // 解析http请求
                AnalysisHttpRequest request = new AnalysisHttpRequest(inWan);
                if (request.getData() == null || request.getData().length == 0) {
                    break;
                }
                HttpCarrier carrier = new HttpCarrier(request.getMethod(), request.getUri(), request.getHttpSessionId(), request.getUri() + request.getHttpSessionId(), System.currentTimeMillis());
                // 发送消息给客户端
                synchronized (clientOut) {
                    HttpMessageUtil.sendMessage(clientOut, () -> new Message(MessageType.HTTP_FORWARD, request.getData(), carrier, sessionId, socketWanId));
                }
            } catch (Exception e) {
                break;
            }
        }
        // 释放并删除连接
        CloseableUtils.close(inWan, outWan, socketWan);
        if (Objects.nonNull(SocketMapContext.SOCKET_MAP.get(sessionId))) {
            SocketMapContext.SOCKET_MAP.get(sessionId).remove(socketWanId);
        }
    }
}
