package com.mob.network.okhttp.builder;


import com.mob.network.okhttp.header.Headers;
import com.mob.network.okhttp.param.Params;
import okhttp3.HttpUrl;

/**
 * Get请求构建者
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class GetBuilder extends OkBuilder {

    public static GetBuilder getBuilder() {
        return new GetBuilder();
    }

    /**
     * 构建get请求的Builder
     */
    public GetBuilder buildGet(String url) {
        url(url).get();
        return this;
    }

    /**
     * 构建get请求的Builder
     */
    public GetBuilder buildGet(String url, Headers headers) {
        if (null != headers) {
            addHeader(headers);
        }
        url(url).get();
        return this;
    }

    /**
     * 构建get请求的Builder
     */
    public GetBuilder buildGet(String url, Params params) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (null != httpUrl) {
            HttpUrl.Builder builder = httpUrl.newBuilder();
            addQueryParameter(builder, params);
            httpUrl = builder.build();
            url(httpUrl).get();
        }
        return this;
    }

    /**
     * 构建get请求的Builder
     */
    public GetBuilder buildGet(String url, Headers headers, Params params) {
        addHeader(headers);
        return buildGet(url, params);
    }

}
