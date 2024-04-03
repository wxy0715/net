package com.net.common.util;

import com.net.common.exception.NetRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * http工具类
 * @author wxy
 */
@Slf4j
public class HttpUtils {

    /**
     * 获取服务器可用端口
     * @return port 端口
     */
    public static Integer getCanUsePort() {
        ServerSocket serverSocket = null;
        Integer localPort = null;
        try {
            serverSocket = new ServerSocket(0);
            localPort = serverSocket.getLocalPort();
            return localPort;
        } catch (IOException e) {
            log.error("获取端口失败!",e);
        } finally {
            CloseableUtils.close(serverSocket);
        }
        throw new NetRuntimeException("未获取到系统端口!");
    }

    /**
     * 验证本地连接是否可用
     * @param ip   本地ip
     * @param port 端口
     * @return boolean
     */
    public static boolean validate(String ip, int port) {
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            return false;
        } finally {
            CloseableUtils.close(socket);
        }
        return true;
    }
}
