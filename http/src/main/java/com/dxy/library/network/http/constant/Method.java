package com.dxy.library.network.http.constant;

/**
 * Http请求方式
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public enum Method {
    //请求方式
    GET("GET", 1),
    POST("POST", 2),
    PUT("PUT", 3),
    PATCH("PATCH", 4),
    DELETE("DELETE", 5);

    private String method;

    private int type;

    Method(String method, int type) {
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
