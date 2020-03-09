package com.dxy.library.network.http.header;

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

    public Headers(String key, Short value) {
        super(key, value);
    }

    public Headers(String key, Integer value) {
        super(key, value);
    }

    public Headers(String key, Long value) {
        super(key, value);
    }

    public Headers(String key, Float value) {
        super(key, value);
    }

    public Headers(String key, Double value) {
        super(key, value);
    }

    public Headers(String key, Boolean value) {
        super(key, value);
    }

    @Override
    public Headers add(String key, String value) {
        put(key, value);
        return this;
    }

    @Override
    public Headers add(String key, String value, String... keyAndValues) {
        put(key, value);
        addKeyAndValues(keyAndValues);
        return this;
    }

    @Override
    public Headers add(String key, Short value) {
        put(key, String.valueOf(value));
        return this;
    }

    @Override
    public Headers add(String key, Integer value) {
        put(key, String.valueOf(value));
        return this;
    }

    @Override
    public Headers add(String key, Float value) {
        put(key, String.valueOf(value));
        return this;
    }

    @Override
    public Headers add(String key, Double value) {
        put(key, String.valueOf(value));
        return this;
    }

    @Override
    public Headers add(String key, Long value) {
        put(key, String.valueOf(value));
        return this;
    }

    @Override
    public Headers add(String key, Boolean value) {
        put(key, String.valueOf(value));
        return this;
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

}
