package com.dxy.library.network.http.param;


import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 请求的Param
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class Params extends LinkedHashMap<String, String> {
    public Params() {
    }

    public Params(String key, String value) {
        add(key, value);
    }

    public Params(String key, String value, String... keyAndValues) {
        add(key, value);
        addKeyAndValues(keyAndValues);
    }

    public Params(String key, Short value) {
        add(key, value);
    }

    public Params(String key, Integer value) {
        add(key, value);
    }

    public Params(String key, Long value) {
        add(key, value);
    }

    public Params(String key, Float value) {
        add(key, value);
    }

    public Params(String key, Double value) {
        add(key, value);
    }

    public Params(String key, Boolean value) {
        add(key, value);
    }

    public Params add(String key, String value) {
        put(key, value);
        return this;
    }

    public Params add(String key, String value, String... keyAndValues) {
        put(key, value);
        addKeyAndValues(keyAndValues);
        return this;
    }

    public Params add(String key, Short value) {
        put(key, String.valueOf(value));
        return this;
    }

    public Params add(String key, Integer value) {
        put(key, String.valueOf(value));
        return this;
    }

    public Params add(String key, Long value) {
        put(key, String.valueOf(value));
        return this;
    }

    public Params add(String key, Float value) {
        put(key, String.valueOf(value));
        return this;
    }

    public Params add(String key, Double value) {
        put(key, String.valueOf(value));
        return this;
    }

    public Params add(String key, Boolean value) {
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

}
