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

    public PatchBuilder buildPatch(String url, RequestBody body) {
        url(url).patch(body);
        return this;
    }

    public PatchBuilder buildPatch(String url, Headers headers) {
        url(url).patch(getRequestBody(headers));
        return this;
    }

    public PatchBuilder buildPatch(String url, Params params) {
        url(url).patch(getRequestBody(params));
        return this;
    }

    public PatchBuilder buildPatch(String url, Headers headers, Params params) {
        url(url).patch(getRequestBody(headers, params));
        return this;
    }

    public <T> PatchBuilder buildPatch(String url, T t, MediaType type) {
        return buildPatch(url, null, t, type);
    }

    public <T> PatchBuilder buildPatch(String url, Headers headers, T t, MediaType type) {
        url(url).patch(getRequestBody(headers, t, type));
        return this;
    }

}
