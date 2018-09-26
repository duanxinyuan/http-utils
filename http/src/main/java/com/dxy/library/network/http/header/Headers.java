package com.dxy.library.network.http.header;

import com.dxy.library.json.gson.GsonUtil;
import com.dxy.library.network.http.param.Params;

/**
 * 请求的Header
 * @author duanxinyuan
 * 2016/9/28 11:49
 */
public class Headers extends Params {
    private static final String CONTENT_TYPE = "Content-Type";

    public Headers() {
    }

    public Headers(String key, String value) {
        super(key, value);
    }

    public Headers(String key, String value, String... keyAndValues) {
        super(key, value, keyAndValues);
    }

    public Headers(String key, Integer value) {
        super(key, value);
    }

    public Headers(String key, Double value) {
        super(key, value);
    }

    public Headers(String key, Long value) {
        super(key, value);
    }

    public Headers contentType(String string) {
        add(CONTENT_TYPE, string);
        return this;
    }

    public boolean containsContentType() {
        return containsKey(CONTENT_TYPE);
    }

    public String getContentType() {
        return get(CONTENT_TYPE);
    }

    @Override
    public String toString() {
        return GsonUtil.to(this);
    }
}
