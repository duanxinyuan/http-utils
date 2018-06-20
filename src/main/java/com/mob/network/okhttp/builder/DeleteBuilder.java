package com.mob.network.okhttp.builder;

import com.mob.network.okhttp.header.Headers;
import com.mob.network.okhttp.param.Params;

/**
 * Delete请求构建者
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class DeleteBuilder extends OkBuilder {

    public static DeleteBuilder getBuilder() {
        return new DeleteBuilder();
    }

    /**
     * 构建delete请求的Builder
     */
    public DeleteBuilder buildDelete(String url) {
        url(url).delete();
        return this;
    }

    /**
     * 构建delete请求的Builder
     */
    public DeleteBuilder buildDelete(String url, Params params) {
        url(url).delete(getRequestBody(params));
        return this;
    }

    /**
     * 构建delete请求的Builder
     */
    public DeleteBuilder buildDelete(String url, Headers headers, Params params) {
        url(url).delete(getRequestBody(headers, params));
        return this;
    }
}
