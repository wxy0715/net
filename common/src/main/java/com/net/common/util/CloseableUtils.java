package com.net.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;


/**
 * 关闭连接
 * @author wxy
 */
@Slf4j
public class CloseableUtils {

    public static void close(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (Objects.isNull(closeable)) {
                continue;
            }
            try {
                closeable.close();
            } catch (IOException e) {
                log.error("Failed to close closeable resource", e);
            }
        }
    }
}
