package com.dxy.library.network.http.builder;


import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.Params;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Put请求构建者
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class PutBuilder extends OkBuilder {

    public static PutBuilder getBuilder() {
        return new PutBuilder();
    }

    /**
     * 构建put请求的Builder
     */
    public PutBuilder buildPut(String url, RequestBody body) {
        url(url).put(body);
        return this;
    }

    /**
     * 构建put请求的Builder
     */
    public PutBuilder buildPut(String url, Params params) {
        url(url).put(getRequestBody(params));
        return this;
    }

    /**
     * 构建put请求的Builder
     */
    public PutBuilder buildPut(String url, Headers headers, Params params) {
        url(url).put(getRequestBody(headers, params));
        return this;
    }

    /**
     * 构建post请求的Builder
     */
    public <T> PutBuilder buildPut(String url, T t, MediaType type) {
        return buildPut(url, null, t, type);
    }

    /**
     * 构建post请求的Builder
     */
    public <T> PutBuilder buildPut(String url, Headers headers, T t, MediaType type) {
        url(url).put(getRequestBody(headers, t, type));
        return this;
    }
}
