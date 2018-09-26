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

    public PostBuilder buildPost(String url, Headers headers, Params params) {
        url(url).post(getRequestBody(headers, params));
        return this;
    }

    public <T> PostBuilder buildPost(String url, Headers headers, Params params, T t, MediaType type) {
        if (null == params || params.size() == 0) {
            url(url).post(getRequestBody(headers, t, type));
        } else {
            url(addQueryParameter(url, params)).post(getRequestBody(headers, t, type));
        }
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
