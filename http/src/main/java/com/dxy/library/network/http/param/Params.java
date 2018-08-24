package com.dxy.library.network.http.param;


import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

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

    public Params(String key, Integer value) {
        add(key, value);
    }

    public Params(String key, Double value) {
        add(key, value);
    }

    public Params(String key, Long value) {
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

    public Params add(String key, Integer value) {
        put(key, String.valueOf(value));
        return this;
    }

    public Params add(String key, Double value) {
        put(key, String.valueOf(value));
        return this;
    }

    public Params add(String key, Long value) {
        put(key, String.valueOf(value));
        return this;
    }

    public Params add(String key, Boolean value) {
        put(key, String.valueOf(value));
        return this;
    }

    @Override
    public String put(String key, String value) {
        //键值不能为空
        if (null != key && null != value) {
            return super.put(key, value);
        }
        return null;
    }

    private void addKeyAndValues(String... keyAndValues) {
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

    /**
     * Url编码参数
     */
    public String encodeUrl() {
        StringBuilder query = new StringBuilder();
        boolean hasParam = false;
        Set<Map.Entry<String, String>> entries = entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            // 忽略参数名或参数值为空的参数
            if (StringUtils.isNoneEmpty(key, value)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }
                try {
                    value = URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                query.append(key).append("=").append(value);
            }
        }
        return query.toString();
    }

    /**
     * 按参数名排序编码参数
     */
    public String encodeSortByKey() {
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(keySet());
        Collections.sort(keys);
        int index = 0;
        for (String key : keys) {
            String value = get(key);
            if (StringUtils.isNoneEmpty(key, value)) {
                content.append(index == 0 ? "" : "&").append(key).append("=").append(value);
                index++;
            }
        }
        return content.toString();
    }

    /**
     * 编码参数
     */
    public String encode() {
        StringBuilder query = new StringBuilder();
        boolean hasParam = false;
        Set<Map.Entry<String, String>> entries = entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            // 忽略参数名或参数值为空的参数
            if (StringUtils.isNoneEmpty(key, value)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }
                query.append(key).append("=").append(value);
            }
        }
        return query.toString();
    }
}
