package com.net.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * http载体
 * @author wxy
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class HttpCarrier implements Serializable {

    private static final long serialVersionUID = 1L;

    private String method;

    private String uri;

    private String httpSessionId;

    private String cacheKey;

    private Long timestamp;
}
