package com.net.http.client;

import com.net.common.constant.MessageType;
import com.net.common.entity.Message;
import com.net.common.util.CloseableUtils;
import com.net.common.util.HttpMessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Objects;

/**
 * 负责转发http 消息
 */
public class ForwardMessageRunnable implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ForwardMessageRunnable.class);
    /**
     * 内网ip
     */
    private final String localIp;
    /**
     * 内网端口
     */
    private final int localPort;
    /**
     * 消息实体
     */
    private final Message message;
    /**
     * 客户端socket
     */
    private final Socket clientSocket;

    public ForwardMessageRunnable(String localIp, int localPort, Message message, Socket clientSocket) {
        this.localIp = localIp;
        this.localPort = localPort;
        this.message = message;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();
            byte[] data = forwardMessage(localIp, localPort, message.getBody());
            if (Objects.nonNull(message.getCarrier()) && isPrint(message.getCarrier().getUri())) {
                log.info("转发内网服务->{} 耗时->{}毫秒", message.getCarrier().getUri(), (System.currentTimeMillis() - start));
            }
            // 同步发送数据
            synchronized (clientSocket) {
                HttpMessageUtil.sendMessage(clientSocket.getOutputStream(), () -> new Message(MessageType.HTTP_FORWARD, data, message.getCarrier(), message.getSessionId(), message.getSocketWanId()));
            }
        } catch (Exception e) {
            log.error("转发内网web 应用失败", e);
        }
    }


    /**
     * 将服务端的消息转发给web 应用
     * @param localPort 应用端口
     * @param data      转发数据
     * @return 返回的数据
     */
    private byte[] forwardMessage(String localIp, int localPort, byte[] data) {
        Socket socket = null;
        OutputStream out = null;
        InputStream in = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            socket = new Socket(localIp, localPort);
            out = socket.getOutputStream();
            out.write(data);
            // 告知服务器发送完毕
            socket.shutdownOutput();

            in = socket.getInputStream();
            byte[] bytes = new byte[1024 * 10];
            int len;
            while ((len = in.read(bytes)) != -1) {
                byteArrayOutputStream.write(bytes, 0, len);
            }

        } catch (ConnectException connectException) {
            log.error("内网服务不可用,客户端退出！");
            CloseableUtils.close(clientSocket);
        } catch (Exception e) {
            log.error("消息转发失败,请重新启动客户端", e);
        } finally {
            CloseableUtils.close(out, in, socket);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private boolean isPrint(String uri) {
        if (StringUtils.isBlank(uri)) {
            return false;
        }
        return !uri.contains(".js") && !uri.contains(".html") && !uri.contains(".png") && !uri.contains(".css");
    }
}
