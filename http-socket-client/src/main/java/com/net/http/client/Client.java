package com.net.http.client;

import com.alibaba.fastjson.JSON;
import com.net.common.constant.Constant;
import com.net.common.constant.MessageType;
import com.net.common.entity.Message;
import com.net.common.exception.NetRuntimeException;
import com.net.common.util.CloseableUtils;
import com.net.common.util.HttpMessageUtil;
import com.net.common.util.HttpUtils;
import com.net.common.util.ThreadPoolFactory;
import com.net.http.client.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 客户端
 * @author wxy
 */
public class Client extends Socket{
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private static final ServerConfig serverConfig = ServerConfig.getInstance();
    public static void main(String[] args) {
        try (Socket clientSocket = new Socket(serverConfig.getServerIp(), serverConfig.getServerPort())){
            // 与服务的进行握手
            HttpMessageUtil.sendMessage(clientSocket.getOutputStream(), () -> new Message(MessageType.NEW_SOCKET, Constant.CONNECT.getBytes(StandardCharsets.UTF_8), new HashMap<>()));
            // 读取服务端返回消息
            Message message = HttpMessageUtil.readMessage(clientSocket.getInputStream());
            log.info("服务端返回握手消息[{}]", JSON.toJSONString(message));
            if (MessageType.ERROR.equals(message.getMessageType())) {
                throw new NetRuntimeException(new String(message.getBody(), StandardCharsets.UTF_8));
            }
            // 超时时间(如果服务器与客户端长时间未通信这客户端自动关闭连接)
            clientSocket.setSoTimeout(Integer.parseInt(message.getHeaderMap().get(Constant.TIME_OUT)));
            log.info("代理地址为-> http://{}:{}:超时时间为-> {}毫秒",serverConfig.getServerIp(),message.getHeaderMap().get(Constant.PORT),Integer.parseInt(message.getHeaderMap().get(Constant.TIME_OUT)));
            // 定时检测内网服务是否可用,主要防止客户端一直占用服务器资源
            scheduleConnectionListener(clientSocket);
            // 监听服务端的消息
            messageListener(clientSocket);
        } catch (Exception e) {
            log.error("客户端异常推出:{}", Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse(e.getMessage()));
        } finally {
            // 监听结束要终止所有线程
            ThreadPoolFactory.shutdown();
        }
    }

    /**
     * 监听服务端消息
     */
    public static void messageListener(Socket clientSocket) {
        while (!clientSocket.isClosed() && clientSocket.isConnected()) {
            try {
                // 读取服务端消息
                Message message = HttpMessageUtil.readMessage(clientSocket.getInputStream());
                // 监听到消息就进行转发
                ThreadPoolFactory.execute(new ForwardMessageRunnable(serverConfig.getLocalIp(), serverConfig.getLocalPort(), message, clientSocket));
            } catch (SocketTimeoutException timeoutException) {
                log.error("与服务端[{}]长时间未通信连接自动断开", serverConfig.getServerIp() + ":" + serverConfig.getServerPort());
                break;
            } catch (Exception e) {
                log.error("与服务端[{}:{}]断开连接,原因:{}", serverConfig.getServerIp(), serverConfig.getServerPort(), e.getMessage());
                break;
            }
        }
        // 关闭资源
        CloseableUtils.close(clientSocket);
    }

    /**
     * 定时检查内网服务
     */
    public static void scheduleConnectionListener(Socket clientSocket) {
        ThreadPoolFactory.scheduleAtFixedRate(() -> {
            boolean validate = HttpUtils.validate(serverConfig.getLocalIp(), serverConfig.getLocalPort());
            if (validate) {
                return;
            }
            log.info("内网服务{}:{}不可用断开连接！", serverConfig.getLocalIp(), serverConfig.getLocalPort());
            CloseableUtils.close(clientSocket);
        }, 3, 60, TimeUnit.SECONDS);
    }


}
