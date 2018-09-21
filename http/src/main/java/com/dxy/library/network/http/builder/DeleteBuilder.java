package com.dxy.library.network.http.builder;

import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.Params;

/**
 * Delete请求构建者
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class DeleteBuilder extends OkBuilder {

    public static DeleteBuilder getBuilder() {
        return new DeleteBuilder();
    }

    public DeleteBuilder buildDelete(String url) {
        url(url).delete();
        return this;
    }

    public DeleteBuilder buildDelete(String url, Params params) {
        url(url).delete(getRequestBody(params));
        return this;
    }

    public DeleteBuilder buildDelete(String url, Headers headers, Params params) {
        url(url).delete(getRequestBody(headers, params));
        return this;
    }
}
