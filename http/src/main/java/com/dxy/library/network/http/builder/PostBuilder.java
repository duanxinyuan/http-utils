package com.dxy.library.network.http.builder;


import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.Params;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.List;

/**
 * Post请求构建者
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class PostBuilder extends OkBuilder {

    public static PostBuilder getBuilder() {
        return new PostBuilder();
    }

    public PostBuilder buildPost(String url, RequestBody body) {
        url(url).post(body);
        return this;
    }

    public PostBuilder buildPost(String url, Params params) {
        url(url).post(getRequestBody(params));
        return this;
    }

    public PostBuilder buildPost(String url, Headers headers) {
        url(url).post(getRequestBody(headers, null));
        return this;
    }

    public PostBuilder buildPost(String url, Headers headers, Params params) {
        url(url).post(getRequestBody(headers, params));
        return this;
    }

    public <T> PostBuilder buildPost(String url, T t, MediaType type) {
        return buildPost(url, null, t, type);
    }

    public <T> PostBuilder buildPost(String url, Headers headers, T t, MediaType type) {
        url(url).post(getRequestBody(headers, t, type));
        return this;
    }

    public PostBuilder buildPost(String url, Headers headers, Params params, List<FileParam> fileParams) {
        url(url).post(getRequestBody(headers, params, fileParams));
        return this;
    }

    public PostBuilder buildPost(String url, Headers headers, FileParam fileParam, Params params) {
        url(url).post(getRequestBody(headers, params, fileParam));
        return this;
    }
}
