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
    // 服务端ip
    private static String serverIp;
    // 服务端端口
    private static int serverPort;
    // 内网程序ip
    private static String localIp;
    // 内网程序端口
    private static int localPort;
    public static void main(String[] args) {
        // 读取用户输入的地址
        serverIp = Optional.of(args).filter(a -> a.length > 0).map(a -> a[0]).orElseThrow(()->new NetRuntimeException("请输入正确的服务端ip"));
        serverPort = Integer.parseInt(Optional.of(args).filter(a -> a.length > 1).map(a -> a[1]).orElseThrow(() -> new NetRuntimeException("请输入正确的服务端端口")));
        localIp = Optional.of(args).filter(a -> a.length > 2).map(a -> a[2]).orElseThrow(()->new NetRuntimeException("请输入正确的内网服务ip"));
        localPort = Integer.parseInt(Optional.of(args).filter(a -> a.length > 3).map(a -> a[3]).orElseThrow(()->new NetRuntimeException("请输入正确的内网服务端口")));
        try (Socket clientSocket = new Socket(serverIp, serverPort)){
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
            log.info("代理地址为-> http://{}:{}:超时时间为-> {}毫秒",serverIp,message.getHeaderMap().get(Constant.PORT),Integer.parseInt(message.getHeaderMap().get(Constant.TIME_OUT)));
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
                ThreadPoolFactory.execute(new ForwardMessageRunnable(localIp, localPort, message, clientSocket));
            } catch (SocketTimeoutException timeoutException) {
                log.error("与服务端[{}]长时间未通信连接自动断开", serverIp + ":" + serverIp);
                break;
            } catch (Exception e) {
                log.error("与服务端[{}:{}]断开连接,原因:{}", serverIp, serverPort, e.getMessage());
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
            boolean validate = HttpUtils.validate(localIp, localPort);
            if (validate) {
                return;
            }
            log.info("内网服务{}:{}不可用断开连接！", localIp, localPort);
            CloseableUtils.close(clientSocket);
        }, 3, 60, TimeUnit.SECONDS);
    }


}
