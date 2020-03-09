package com.dxy.library.network.http.serializer;

import com.dxy.library.json.jackson.JacksonUtil;

import java.lang.reflect.Type;

/**
 * 序列化实现
 * @author duanxinyuan
 * 2019/10/30 11:51
 */
public final class DefaultSerializer implements HttpSerializer {

    /**
     * JSON反序列化
     */
    @Override
    public <V> V from(String json, Type type) {
        return JacksonUtil.from(json, type);
    }

    /**
     * 序列化为JSON
     */
    @Override
    public <V> String to(V v) {
        return JacksonUtil.to(v);
    }

}