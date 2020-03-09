package com.dxy.library.network.http.builder;


import com.dxy.library.network.http.serializer.HttpSerializer;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.Params;
import okhttp3.MediaType;

/**
 * Patch请求构建者
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class PatchBuilder extends OkBuilder {

    public PatchBuilder(HttpSerializer httpSerializer) {
        super(httpSerializer);
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
