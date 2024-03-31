
package com.net.common.util;


import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.internal.StringUtil;
import lombok.SneakyThrows;

import java.io.*;
import java.util.Map;
import java.util.function.Supplier;

public final class HttpMessageUtil {

    /**
     * 发送消息
     */
    @SneakyThrows
    public static <T> void sendMessage(OutputStream outputStream, Supplier<T> supplier) {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(supplier.get());
        objectOutputStream.flush();
    }

    /**
     * 读取消息
     */
    public static <T> T readMessage(InputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        return (T) objectInputStream.readObject();
    }

    private static void appendInitialLine(StringBuilder buf, HttpRequest req) {
        buf.append(req.method());
        buf.append(' ');
        buf.append(req.uri());
        buf.append(' ');
        buf.append(req.protocolVersion());
        buf.append(StringUtil.NEWLINE);
    }

    private static void appendHeaders(StringBuilder buf, HttpHeaders headers) {
        for (Map.Entry<String, String> e : headers) {
            buf.append(e.getKey());
            buf.append(": ");
            buf.append(e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
    }
}
