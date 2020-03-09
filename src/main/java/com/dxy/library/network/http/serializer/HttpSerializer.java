package com.dxy.library.network.http.serializer;

import java.lang.reflect.Type;

/**
 * @author duanxinyuan
 * 2019/10/30 11:54
 */
public interface HttpSerializer {

    /**
     * JSON反序列化
     */
    <V> V from(String json, Type type);

    /**
     * 序列化为JSON
     */
    <V> String to(V v);


}
