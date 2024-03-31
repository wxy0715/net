package com.net.http.server.util;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * http 协议解析
 */
@Data
public class AnalysisHttpRequest {
    /**
     * \r
     */
    private static final byte crlf13 = (byte) 13;
    /**
     * \n
     */
    private static final byte crlf10 = (byte) 10;

    private final InputStream input;
    /**
     * 请求头
     */
    private String headerContext;

    /**
     * 请求头
     */
    private Map<String, String> headerMap = new HashMap<>();

    /**
     * 请求方法类型
     */
    private String method;
    /**
     * 一次完整的http请求流
     */
    private byte[] data;
    /**
     * 临时内存
     */
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    /**
     * http 请求方法 url
     */
    private String uri;
    /**
     * http版本
     */
    private String version;
    /**
     * httpSessionId
     */
    private String httpSessionId;

    public AnalysisHttpRequest(InputStream input) throws IOException {
        this.input = input;
        analysisRequestHeader();
        analysisRequestMethodAndChuckedAndRequestLine();
        analysisRequestBody();
        analysisRequestHeaderMap();
        analysisSessionId();
    }

    private void analysisRequestBody() throws IOException {
        int index = headerContext.indexOf("Content-Length: ");
        if (index == -1) {
            data = byteArrayOutputStream.toByteArray();
            return;
        }
        char[] charArray = headerContext.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = (index + 16); i < charArray.length; i++) {
            if ((byte) charArray[i] == 13 || (byte) charArray[i] == 10) {
                break;
            }
            builder.append(charArray[i]);
        }

        int length = Integer.parseInt(builder.toString());
        int len = 0;
        byte[] bytes = new byte[1024];
        while (len < length) {
            int read = input.read(bytes);
            byteArrayOutputStream.write(bytes, 0, read);
            len += read;
        }
        data = byteArrayOutputStream.toByteArray();
    }

    private void analysisRequestHeader() throws IOException {
        byte[] crlf = new byte[1];
        // 已经连接的回车换行数 crlfNum=4为头部结束
        int crlfNum = 0;
        while (input.read(crlf) != -1) {
            byteArrayOutputStream.write(crlf, 0, 1);
            if (crlf[0] == crlf13 || crlf[0] == crlf10) {
                crlfNum++;
            } else {
                crlfNum = 0;
            }
            if (crlfNum == 4) {
                break;
            }
        }
        headerContext = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    private void analysisRequestMethodAndChuckedAndRequestLine() throws UnsupportedEncodingException {
        int index = headerContext.indexOf("\r\n");
        String requestLine = "";
        if (index != -1) {
            requestLine = URLDecoder.decode(headerContext.substring(0, index), "UTF-8");
        }

        if (StringUtils.isBlank(requestLine)) {
            return;
        }

        String[] splits = requestLine.split(" ");
        method = splits[0];
        uri = splits[1];
        version = splits[2];
    }

    private void analysisRequestHeaderMap() {
        if (StringUtils.isBlank(headerContext)) {
            return;
        }
        String[] splits = headerContext.split("\r\n");
        for (int i = 0; i < splits.length; i++) {
            if (i == 0) {
                continue;
            }
            String[] split = splits[i].split(":");
            if (split.length < 2) {
                continue;
            }
            headerMap.put(split[0], split[1]);
        }
    }


    private void analysisSessionId() {
        if (!headerMap.containsKey("Cookie")) {
            return;
        }
        String cookie = headerMap.get("Cookie");
        String[] splits = cookie.split(";");
        for (String split : splits) {
            if (!split.contains("JSESSIONID")) {
                continue;
            }
            String[] JSESSIONID = split.split("=");
            if (JSESSIONID.length > 1) {
                httpSessionId = JSESSIONID[1];
            }
        }
    }
}