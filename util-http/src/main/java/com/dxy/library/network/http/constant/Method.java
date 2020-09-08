package com.dxy.library.network.http.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Http请求方式
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public enum Method {

    //请求方式
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    private static final Map<String, Method> METHOD_MAP = new HashMap<>(16);

    static {
        for (Method httpMethod : values()) {
            METHOD_MAP.put(httpMethod.name(), httpMethod);
        }
    }

    public static Method resolve(String method) {
        return (method != null ? METHOD_MAP.get(method) : null);
    }

}
