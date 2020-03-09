package com.dxy.library.network.http.builder;


import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.Params;
import com.dxy.library.network.http.serializer.HttpSerializer;
import okhttp3.MediaType;

/**
 * Put请求构建者
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class PutBuilder extends OkBuilder {

    public PutBuilder(HttpSerializer httpSerializer) {
        super(httpSerializer);
    }

    public PutBuilder buildPut(String url, Headers headers, Params params) {
        url(url).put(getRequestBody(headers, params));
        return this;
    }

    public <T> PutBuilder buildPut(String url, Headers headers, Params params, T t, MediaType type) {
        if (null == params || params.size() == 0) {
            url(url).put(getRequestBody(headers, t, type));
        } else {
            url(addQueryParameter(url, params)).put(getRequestBody(headers, t, type));
        }
        return this;
    }
}
