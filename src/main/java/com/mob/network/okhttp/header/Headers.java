package com.mob.network.okhttp.header;

import com.mob.network.okhttp.param.Params;

/**
 * 请求的Header
 * @author duanxinyuan
 * 2016/9/28 11:49
 */
public class Headers extends Params {
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
}
