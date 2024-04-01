package com.net.http.server.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http 协议解析
 */
@Data
@Slf4j
public class HttpRequestUtil {

    /**
     * 请求方法类型
     */
    private String method;
    /**
     * http 请求方法 url
     */
    private String uri;
    /**
     * http版本
     */
    private String version;

    /**
     * 一次完整的http请求流
     */
    private byte[] data;

    /**
     * 请求头
     */
    private Map<String, String> headerMap = new HashMap<>();

    /**
     * httpSessionId
     */
    private String httpSessionId;

    public HttpRequestUtil(InputStream input) throws IOException {
        // 解析浏览器发送的HTTP请求
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
        String requestLine = bufferedReader.readLine();
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 3 || !"HTTP/1.1".equals(requestParts[2])) {
            throw new IllegalArgumentException("Invalid HTTP request line: " + requestLine);
        }
        method = requestParts[0];
        uri = requestParts[1];
        version = requestParts[2];
        // 创建一个ByteArrayOutputStream用于存储请求数据
        ByteArrayOutputStream requestBytes = new ByteArrayOutputStream();
        // 读取并解析请求头，写入ByteArrayOutputStream
        String line;
        while ((line = bufferedReader.readLine()).trim().length() > 0) { // 继续读取请求头，直到空行（表示头信息结束）
            requestBytes.write(line.getBytes(StandardCharsets.UTF_8)); // 将请求头行写入ByteArrayOutputStream
            requestBytes.write("\r\n".getBytes(StandardCharsets.UTF_8)); // 添加回车换行符
        }
        requestBytes.write("\r\n".getBytes(StandardCharsets.UTF_8)); // 添加额外的回车换行符，表示头信息结束

        // 读取请求体（如果有），写入ByteArrayOutputStream
        if ("POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method)
                || "GET".equalsIgnoreCase(method)
        ) {
            // 缓冲区大小，可根据实际情况调整
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                // 将请求体数据写入ByteArrayOutputStream
                requestBytes.write(buffer, 0, bytesRead);
            }
        }
        // 获取ByteArrayOutputStream中的所有字节，即整个HTTP请求数据
        data = requestBytes.toByteArray();
        // 输出请求数据的字符串表示
        log.info("获取浏览器数据:"+new String(data, StandardCharsets.UTF_8));

        // 读取并解析请求头，查找Cookie
        Map<String, List<String>> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = bufferedReader.readLine()).trim().length() > 0) { // 继续读取请求头，直到空行（表示头信息结束）
            int colonIndex = headerLine.indexOf(':');
            if (colonIndex == -1) {
                throw new IllegalArgumentException("Invalid HTTP header line: " + line);
            }
            String headerName = headerLine.substring(0, colonIndex).trim();
            String headerValue = headerLine.substring(colonIndex + 1).trim();
            headers.computeIfAbsent(headerName, k -> new ArrayList<>()).add(headerValue);
        }

        // 提取Cookie信息
        List<String> cookieHeaders = headers.get("Cookie");
        if (cookieHeaders != null && !cookieHeaders.isEmpty()) {
            StringBuilder cookiesBuilder = new StringBuilder();
            for (String cookieHeader : cookieHeaders) {
                cookiesBuilder.append(cookieHeader).append("; ");
            }
            String cookies = cookiesBuilder.toString().trim();
            log.info("Received Cookies: {}" , cookies);
        }
    }
}