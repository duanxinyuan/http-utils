package com.dxy.network.okhttp.builder;


import com.dxy.network.okhttp.header.Headers;
import com.dxy.network.okhttp.param.Params;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.File;

/**
 * Post请求构建者
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class PostBuilder extends OkBuilder {

    public static PostBuilder getBuilder() {
        return new PostBuilder();
    }

    /**
     * 构建post请求的Builder
     */
    public PostBuilder buildPost(String url, RequestBody body) {
        url(url).post(body);
        return this;
    }

    /**
     * 构建post请求的Builder
     */
    public PostBuilder buildPost(String url, Params params) {
        url(url).post(getRequestBody(params));
        return this;
    }

    /**
     * 构建post请求的Builder
     */
    public PostBuilder buildPost(String url, Headers headers) {
        url(url).post(getRequestBody(headers, null));
        return this;
    }

    /**
     * 构建post请求的Builder
     */
    public PostBuilder buildPost(String url, Headers headers, Params params) {
        url(url).post(getRequestBody(headers, params));
        return this;
    }

    /**
     * 构建post请求的Builder
     */
    public <T> PostBuilder buildPost(String url, T t, MediaType type) {
        return buildPost(url, null, t, type);
    }

    /**
     * 构建post请求的Builder
     */
    public <T> PostBuilder buildPost(String url, Headers headers, T t, MediaType type) {
        url(url).post(getRequestBody(headers, t, type));
        return this;
    }

    /**
     * 构建post请求的Builder
     */
    public PostBuilder buildPost(String url, String[] fileKeys, File[] files, Params params) {
        url(url).post(getRequestBody(files, fileKeys, params));
        return this;
    }

    /**
     * 构建post请求的Builder
     */
    public PostBuilder buildPost(String url, String fileKey, File file, Params params) {
        url(url).post(getRequestBody(file, fileKey, params));
        return this;
    }
}
