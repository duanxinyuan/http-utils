package com.dxy.library.network.http.builder;


import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.Params;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Patch请求构建者
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class PatchBuilder extends OkBuilder {

    public static PatchBuilder getBuilder() {
        return new PatchBuilder();
    }

    public PatchBuilder buildPatch(String url, Headers headers, Params params) {
        url(url).patch(getRequestBody(headers, params));
        return this;
    }

    public <T> PatchBuilder buildPatch(String url, Headers headers, Params params, T t, MediaType type) {
        if (null == params || params.size() == 0) {
            url(url).patch(getRequestBody(headers, t, type));
        } else {
            url(addQueryParameter(url, params)).patch(getRequestBody(headers, t, type));
        }
        return this;
    }

}
