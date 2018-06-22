package com.dxy.network.okhttp;


import com.dxy.network.okhttp.header.Headers;
import com.dxy.network.okhttp.param.Params;
import com.google.gson.reflect.TypeToken;
import com.dxy.network.okhttp.callback.RequestCallback;
import com.dxy.network.okhttp.constant.Method;

import java.io.File;

/**
 * OkHttp工具类
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public interface OkHttpUtil {

    /**
     * 是否记录日志
     */
    static void isLog(boolean isLog) {
        OkHttpExecutor.isLog(isLog);
    }

    /******** get *********/

    static String get(String url) {
        return get(url, null, null, String.class);
    }

    static <V> V get(String url, Class<V> c) {
        return get(url, null, null, c);
    }

    static <V> V get(String url, TypeToken<V> typeToken) {
        return get(url, null, null, typeToken);
    }

    static String get(String url, Headers headers) {
        return get(url, headers, null, String.class);
    }

    static <V> V get(String url, Headers headers, Class<V> c) {
        return get(url, headers, null, c);
    }

    static <V> V get(String url, Headers headers, TypeToken<V> typeToken) {
        return get(url, headers, null, typeToken);
    }

    static String get(String url, Params params) {
        return get(url, null, params, String.class);
    }

    static <V> V get(String url, Params params, Class<V> c) {
        return get(url, null, params, c);
    }

    static <V> V get(String url, Params params, TypeToken<V> typeToken) {
        return get(url, null, params, typeToken);
    }

    static String get(String url, Headers headers, Params params) {
        return OkHttpExecutor.getInstance().excute(Method.GET, url, headers, params, String.class);
    }

    static <V> V get(String url, Headers headers, Params params, Class<V> c) {
        return OkHttpExecutor.getInstance().excute(Method.GET, url, headers, params, c);
    }

    static <V> V get(String url, Headers headers, Params params, TypeToken<V> typeToken) {
        return OkHttpExecutor.getInstance().excute(Method.GET, url, headers, params, typeToken);
    }

    static void getAsync(String url) {
        getAsync(url, null, null, null);
    }

    static void getAsync(String url, RequestCallback callback) {
        getAsync(url, null, null, callback);
    }

    static void getAsync(String url, Params params, RequestCallback callback) {
        getAsync(url, null, params, callback);
    }

    static void getAsync(String url, Headers headers, RequestCallback callback) {
        getAsync(url, headers, null, callback);
    }

    static void getAsync(String url, Headers headers, Params params, RequestCallback callback) {
        OkHttpExecutor.getInstance().enqueue(Method.GET, url, headers, params, callback);
    }


    /******** post *********/

    static String post(String url, Headers headers) {
        return post(url, headers, null, String.class);
    }

    static <V> V post(String url, Headers headers, Class<V> c) {
        return post(url, headers, null, c);
    }

    static <V> V post(String url, Headers headers, TypeToken<V> typeToken) {
        return post(url, headers, null, typeToken);
    }

    static String post(String url, Params params) {
        return post(url, null, params, String.class);
    }

    static <V> V post(String url, Params params, Class<V> c) {
        return post(url, null, params, c);
    }

    static <V> V post(String url, Params params, TypeToken<V> typeToken) {
        return post(url, null, params, typeToken);
    }

    static String post(String url, Headers headers, Params params) {
        return OkHttpExecutor.getInstance().excute(Method.POST, url, headers, params, String.class);
    }

    static <V> V post(String url, Headers headers, Params params, Class<V> c) {
        return OkHttpExecutor.getInstance().excute(Method.POST, url, headers, params, c);
    }

    static <V> V post(String url, Headers headers, Params params, TypeToken<V> typeToken) {
        return OkHttpExecutor.getInstance().excute(Method.POST, url, headers, params, typeToken);
    }

    static <T> String postJson(String url, T t) {
        return postJson(url, null, null, t, String.class);
    }

    static <V, T> V postJson(String url, T t, Class<V> c) {
        return postJson(url, null, null, t, c);
    }

    static <V, T> V postJson(String url, T t, TypeToken<V> typeToken) {
        return postJson(url, null, null, t, typeToken);
    }

    static <T> String postJson(String url, Headers headers, T t) {
        return postJson(url, headers, null, t, String.class);
    }

    static <V, T> V postJson(String url, Headers headers, T t, Class<V> c) {
        return postJson(url, headers, null, t, c);
    }

    static <V, T> V postJson(String url, Headers headers, T t, TypeToken<V> typeToken) {
        return postJson(url, headers, null, t, typeToken);
    }

    static <V, T> V postJson(String url, Params params, T t, Class<V> c) {
        return postJson(url, null, params, t, c);
    }

    static <V, T> V postJson(String url, Params params, T t, TypeToken<V> typeToken) {
        return postJson(url, null, params, t, typeToken);
    }

    static <V, T> V postJson(String url, Headers headers, Params params, T t, Class<V> c) {
        return OkHttpExecutor.getInstance().excute(Method.POST, url, headers, params, t, c);
    }

    static <V, T> V postJson(String url, Headers headers, Params params, T t, TypeToken<V> typeToken) {
        return OkHttpExecutor.getInstance().excute(Method.POST, url, headers, params, t, typeToken);
    }

    static void postAsync(String url, RequestCallback callback) {
        postAsync(url, null, null, callback);
    }

    static void postAsync(String url, Headers headers) {
        postAsync(url, headers, null, null);
    }

    static void postAsync(String url, Headers headers, RequestCallback callback) {
        postAsync(url, headers, null, callback);
    }

    static void postAsync(String url, Params params) {
        postAsync(url, null, params, null);
    }

    static void postAsync(String url, Params params, RequestCallback callback) {
        postAsync(url, null, params, callback);
    }

    static void postAsync(String url, Headers headers, Params params, RequestCallback callback) {
        OkHttpExecutor.getInstance().enqueue(Method.POST, url, headers, params, callback);
    }

    static void postFileAsync(String url, String fileKey, File file, RequestCallback callback) {
        postFileAsync(url, null, null, fileKey, file, callback);
    }

    static void postFileAsync(String url, Params params, String fileKey, File file, RequestCallback callback) {
        postFileAsync(url, null, params, fileKey, file, callback);
    }

    static void postFileAsync(String url, Params params, String[] fileKeys, File[] files, RequestCallback callback) {
        postFileAsync(url, null, params, fileKeys, files, callback);
    }

    static void postFileAsync(String url, Headers headers, Params params, String fileKey, File file, RequestCallback callback) {
        OkHttpExecutor.getInstance().enqueue(url, headers, params, fileKey, file, callback);
    }

    static void postFileAsync(String url, Headers headers, Params params, String[] fileKeys, File[] files, RequestCallback callback) {
        OkHttpExecutor.getInstance().enqueue(url, headers, params, fileKeys, files, callback);
    }

    static <T> void postJsonAsync(String url, T t) {
        postJsonAsync(url, null, null, t, null);
    }

    static <T> void postJsonAsync(String url, T t, RequestCallback callback) {
        postJsonAsync(url, null, null, t, callback);
    }

    static <T> void postJsonAsync(String url, Headers headers, T t) {
        postJsonAsync(url, headers, null, t, null);
    }

    static <T> void postJsonAsync(String url, Headers headers, T t, RequestCallback callback) {
        postJsonAsync(url, headers, null, t, callback);
    }

    static <T> void postJsonAsync(String url, Params params, T t) {
        postJsonAsync(url, null, params, t, null);
    }

    static <T> void postJsonAsync(String url, Params params, T t, RequestCallback callback) {
        postJsonAsync(url, null, params, t, callback);
    }

    static <T> void postJsonAsync(String url, Headers headers, Params params, T t) {
        postJsonAsync(url, headers, params, t, null);
    }

    static <T> void postJsonAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        OkHttpExecutor.getInstance().enqueue(Method.POST, url, headers, params, t, callback);
    }


    /******** put *********/

    static String put(String url, Headers headers) {
        return put(url, headers, null, String.class);
    }

    static <V> V put(String url, Headers headers, Class<V> c) {
        return put(url, headers, null, c);
    }

    static <V> V put(String url, Headers headers, TypeToken<V> typeToken) {
        return put(url, headers, null, typeToken);
    }

    static String put(String url, Params params) {
        return put(url, null, params, String.class);
    }

    static <V> V put(String url, Params params, Class<V> c) {
        return put(url, null, params, c);
    }

    static <V> V put(String url, Params params, TypeToken<V> typeToken) {
        return put(url, null, params, typeToken);
    }

    static String put(String url, Headers headers, Params params) {
        return put(url, headers, params, String.class);
    }

    static <V> V put(String url, Headers headers, Params params, Class<V> c) {
        return OkHttpExecutor.getInstance().excute(Method.PUT, url, headers, params, c);
    }

    static <V> V put(String url, Headers headers, Params params, TypeToken<V> typeToken) {
        return OkHttpExecutor.getInstance().excute(Method.PUT, url, headers, params, typeToken);
    }

    static <T> String putJson(String url, T t) {
        return putJson(url, null, null, t, String.class);
    }

    static <V, T> V putJson(String url, T t, Class<V> c) {
        return putJson(url, null, null, t, c);
    }

    static <V, T> V putJson(String url, T t, TypeToken<V> typeToken) {
        return putJson(url, null, null, t, typeToken);
    }

    static <T> String putJson(String url, Headers headers, T t) {
        return putJson(url, headers, null, t, String.class);
    }

    static <V, T> V putJson(String url, Headers headers, T t, Class<V> c) {
        return putJson(url, headers, null, t, c);
    }

    static <V, T> V putJson(String url, Headers headers, T t, TypeToken<V> typeToken) {
        return putJson(url, headers, null, t, typeToken);
    }

    static <V, T> V putJson(String url, Params params, T t, Class<V> c) {
        return putJson(url, null, params, t, c);
    }

    static <V, T> V putJson(String url, Params params, T t, TypeToken<V> typeToken) {
        return putJson(url, null, params, t, typeToken);
    }

    static <V, T> V putJson(String url, Headers headers, Params params, T t, Class<V> c) {
        return OkHttpExecutor.getInstance().excute(Method.PUT, url, headers, params, t, c);
    }

    static <V, T> V putJson(String url, Headers headers, Params params, T t, TypeToken<V> typeToken) {
        return OkHttpExecutor.getInstance().excute(Method.PUT, url, headers, params, t, typeToken);
    }

    static <T> void putJsonAsync(String url, T t) {
        putJsonAsync(url, null, t, null);
    }

    static <T> void putJsonAsync(String url, T t, RequestCallback callback) {
        putJsonAsync(url, null, t, callback);
    }

    static <T> void putJsonAsync(String url, Headers headers, T t) {
        putJsonAsync(url, headers, t, null);
    }

    static <T> void putJsonAsync(String url, Headers headers, T t, RequestCallback callback) {
        putJsonAsync(url, headers, null, t, callback);
    }

    static <T> void putJsonAsync(String url, Headers headers, Params params, T t) {
        putJsonAsync(url, headers, params, t, null);
    }

    static <T> void putJsonAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        OkHttpExecutor.getInstance().enqueue(Method.PUT, url, headers, params, t, callback);
    }

    static void putAsync(String url, Params params) {
        putAsync(url, null, params, null);
    }

    static void putAsync(String url, Headers headers) {
        putAsync(url, headers, null, null);
    }

    static void putAsync(String url, Headers headers, Params params, RequestCallback callback) {
        OkHttpExecutor.getInstance().enqueue(Method.PUT, url, headers, params, callback);
    }


    /******** patch *********/

    static String patch(String url) {
        return patch(url, null, null, String.class);
    }

    static <V> V patch(String url, Class<V> c) {
        return patch(url, null, null, c);
    }

    static <V> V patch(String url, TypeToken<V> typeToken) {
        return patch(url, null, null, typeToken);
    }

    static String patch(String url, Headers headers) {
        return patch(url, headers, null, String.class);
    }

    static <V> V patch(String url, Headers headers, Class<V> c) {
        return patch(url, headers, null, c);
    }

    static <V> V patch(String url, Headers headers, TypeToken<V> typeToken) {
        return patch(url, headers, null, typeToken);
    }

    static String patch(String url, Params params) {
        return patch(url, null, params, String.class);
    }

    static <V> V patch(String url, Params params, Class<V> c) {
        return patch(url, null, params, c);
    }

    static <V> V patch(String url, Params params, TypeToken<V> typeToken) {
        return patch(url, null, params, typeToken);
    }

    static <V> V patch(String url, Headers headers, Params params, Class<V> c) {
        return OkHttpExecutor.getInstance().excute(Method.PATCH, url, headers, params, c);
    }

    static <V> V patch(String url, Headers headers, Params params, TypeToken<V> typeToken) {
        return OkHttpExecutor.getInstance().excute(Method.PATCH, url, headers, params, typeToken);
    }

    static void patchAsync(String url, RequestCallback callback) {
        patchAsync(url, null, null, callback);
    }

    static void patchAsync(String url, Headers headers, RequestCallback callback) {
        patchAsync(url, headers, null, callback);
    }

    static void patchAsync(String url, Params params) {
        patchAsync(url, null, params, null);
    }

    static void patchAsync(String url, Params params, RequestCallback callback) {
        patchAsync(url, null, params, callback);
    }

    static void patchAsync(String url, Headers headers, Params params, RequestCallback callback) {
        OkHttpExecutor.getInstance().enqueue(Method.PATCH, url, headers, params, callback);
    }


    /******** delete *********/

    static String delete(String url) {
        return delete(url, null, null, String.class);
    }

    static <V> V delete(String url, Class<V> c) {
        return delete(url, null, null, c);
    }

    static <V> V delete(String url, TypeToken<V> typeToken) {
        return delete(url, null, null, typeToken);
    }

    static String delete(String url, Headers headers) {
        return delete(url, headers, null, String.class);
    }

    static <V> V delete(String url, Headers headers, Class<V> c) {
        return delete(url, headers, null, c);
    }

    static <V> V delete(String url, Headers headers, TypeToken<V> typeToken) {
        return delete(url, headers, null, typeToken);
    }

    static String delete(String url, Params params) {
        return delete(url, null, params, String.class);
    }

    static <V> V delete(String url, Params params, Class<V> c) {
        return delete(url, null, params, c);
    }

    static <V> V delete(String url, Params params, TypeToken<V> typeToken) {
        return delete(url, null, params, typeToken);
    }

    static String delete(String url, Headers headers, Params params) {
        return OkHttpExecutor.getInstance().excute(Method.DELETE, url, headers, params, String.class);
    }

    static <V> V delete(String url, Headers headers, Params params, Class<V> c) {
        return OkHttpExecutor.getInstance().excute(Method.DELETE, url, headers, params, c);
    }

    static <V> V delete(String url, Headers headers, Params params, TypeToken<V> typeToken) {
        return OkHttpExecutor.getInstance().excute(Method.DELETE, url, headers, params, typeToken);
    }

    static void deleteAsync(String url, RequestCallback callback) {
        deleteAsync(url, null, null, callback);
    }

    static void deleteAsync(String url, Headers headers) {
        deleteAsync(url, headers, null);
    }

    static void deleteAsync(String url, Headers headers, RequestCallback callback) {
        deleteAsync(url, headers, null, callback);
    }

    static void deleteAsync(String url, Params params) {
        deleteAsync(url, params, null);
    }

    static void deleteAsync(String url, Params params, RequestCallback callback) {
        deleteAsync(url, null, params, callback);
    }

    static void deleteAsync(String url, Headers headers, Params params, RequestCallback callback) {
        OkHttpExecutor.getInstance().enqueue(Method.DELETE, url, headers, params, callback);
    }

    /******** download *********/

    static void download(String url, String targetDir) {
        OkHttpExecutor.getInstance().download(url, targetDir);
    }

    static void downloadAsync(String url, String targetDir) {
        OkHttpExecutor.getInstance().downloadAsync(url, targetDir);
    }

}
