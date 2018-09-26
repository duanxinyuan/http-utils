package com.dxy.library.network.http.builder;


import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.Params;

/**
 * Get请求构建者
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class GetBuilder extends OkBuilder {

    public static GetBuilder getBuilder() {
        return new GetBuilder();
    }

    public GetBuilder buildGet(String url, Params params) {
        if (null == params || params.size() == 0) {
            url(url).get();
        } else {
            url(addQueryParameter(url, params)).get();
        }
        return this;
    }

    public GetBuilder buildGet(String url, Headers headers, Params params) {
        addHeader(headers);
        return buildGet(url, params);
    }

}
