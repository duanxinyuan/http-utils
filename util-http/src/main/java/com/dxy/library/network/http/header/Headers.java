package com.dxy.library.network.http.header;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 请求的Header
 * @author duanxinyuan
 * 2016/9/28 11:49
 */
public class Headers extends LinkedHashMap<String, String> {
    private static final String CONTENT_TYPE = "Content-Type";

    public Headers() {
    }

    public Headers(String key, String value) {
        add(key, value);
    }

    public Headers(String key, String value, String... keyAndValues) {
        add(key, value);
        addKeyAndValues(keyAndValues);
    }

    public Headers(String key, Short value) {
        add(key, value);
    }

    public Headers(String key, Integer value) {
        add(key, value);
    }

    public Headers(String key, Long value) {
        add(key, value);
    }

    public Headers(String key, Float value) {
        add(key, value);
    }

    public Headers(String key, Double value) {
        add(key, value);
    }

    public Headers(String key, Boolean value) {
        add(key, value);
    }

    public Headers add(String key, String value) {
        put(key, value);
        return this;
    }

    public Headers add(String key, String value, String... keyAndValues) {
        put(key, value);
        addKeyAndValues(keyAndValues);
        return this;
    }

    public Headers add(String key, Short value) {
        put(key, String.valueOf(value));
        return this;
    }

    public Headers add(String key, Integer value) {
        put(key, String.valueOf(value));
        return this;
    }

    public Headers add(String key, Long value) {
        put(key, String.valueOf(value));
        return this;
    }

    public Headers add(String key, Float value) {
        put(key, String.valueOf(value));
        return this;
    }

    public Headers add(String key, Double value) {
        put(key, String.valueOf(value));
        return this;
    }

    public Headers add(String key, Boolean value) {
        put(key, String.valueOf(value));
        return this;
    }

    public void addKeyAndValues(String... keyAndValues) {
        if (null != keyAndValues) {
            ArrayList<String> strings = Lists.newArrayList(keyAndValues);
            if (strings.size() % 2 != 0) {
                strings.add("");
            }
            for (int i = 0; i < strings.size(); i = i + 2) {
                add(strings.get(i), strings.get(i + 1));
            }
        }
    }

    @Override
    public String put(String key, String value) {
        //键值不能为空
        if (null != key && null != value && !"null".equals(value)) {
            return super.put(key, value);
        } else {
            return null;
        }
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
