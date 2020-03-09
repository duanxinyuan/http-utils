package com.dxy.library.network.http;


import com.dxy.library.network.http.interceptor.RetryInterceptor;
import com.dxy.library.network.http.serializer.HttpSerializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.executor.Executor;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import com.dxy.library.network.http.serializer.DefaultSerializer;
import com.dxy.library.util.config.ConfigUtils;

import java.util.List;

/**
 * Http执行类，默认开启日志，不想开启日志可以调用log方法设置log，或者屏蔽log
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public final class Http {

    //默认序列化实现
    private static HttpSerializer defaultHttpSerializer = new DefaultSerializer();

    //默认开启请求日志
    private static final boolean LOG_ENABLE_DEFAULT = ConfigUtils.getAsBoolean("http.defaultRequestLogEnable", true);

    //默认超时时间为60秒
    private static final int TIMEOUT_DEFAULT = ConfigUtils.getAsInt("http.defaultTimout", 60);

    //默认请求失败重试次数为0次
    private static final int RETRIES_DEFAULT = ConfigUtils.getAsInt("http.defaultRetries", 0);

    //默认执行器，开启请求日志，60秒超时时间，0次重试
    private static final Executor DEFAULT_EXECUTOR = new Executor(defaultHttpSerializer, LOG_ENABLE_DEFAULT, TIMEOUT_DEFAULT, RETRIES_DEFAULT, RetryInterceptor.RETRY_INTERVAL_DEFAULT);

    /**
     * 设置请求日志开关
     */
    public static Executor requestLogEnable(boolean requestLogEnable) {
        return Executor.getExecutor(defaultHttpSerializer, requestLogEnable, TIMEOUT_DEFAULT, RETRIES_DEFAULT);
    }

    /**
     * 关闭请求日志开关
     */
    public static Executor disableRequestLog() {
        return requestLogEnable(false);
    }

    /**
     * 开启请求日志开关
     */
    public static Executor enableRequestLog() {
        return requestLogEnable(true);
    }

    /**
     * 设置请求超时时间
     */
    public static Executor timeout(int timeout) {
        return Executor.getExecutor(defaultHttpSerializer, LOG_ENABLE_DEFAULT, timeout, RETRIES_DEFAULT);
    }

    /**
     * 设置失败重试次数
     */
    public static Executor retries(int retries) {
        return Executor.getExecutor(defaultHttpSerializer, LOG_ENABLE_DEFAULT, TIMEOUT_DEFAULT, retries);
    }

    /**
     * 设置失败重试次数
     */
    public static Executor retries(int retries, long retryIntervalMillis) {
        return Executor.getExecutor(defaultHttpSerializer, LOG_ENABLE_DEFAULT, TIMEOUT_DEFAULT, retries, retryIntervalMillis);
    }

    /**
     * 替换序列化实现
     */
    public static void setHttpSerializer(HttpSerializer httpSerializer) {
        defaultHttpSerializer = httpSerializer;
        DEFAULT_EXECUTOR.httpSerializer(httpSerializer);
    }

    /**
     * 设置全局请求日志开关
     */
    public static void setDefaultRequestLogEnable(boolean requestLogEnable) {
        DEFAULT_EXECUTOR.getRequester().setRequestLogEnable(requestLogEnable);
    }

    /**
     * 设置全局超时时间，单位为秒
     */
    public static void setDefaultTimeout(int timeout) {
        DEFAULT_EXECUTOR.getRequester().setTimeout(timeout);
    }

    /**
     * 设置全局重试次数
     */
    public static void setDefaultRetries(int retries) {
        DEFAULT_EXECUTOR.getRequester().setRetries(retries);
    }

    /******** get *********/

    public static String get(String url) {
        return DEFAULT_EXECUTOR.get(url);
    }

    public static <V> V get(String url, Class<V> c) {
        return DEFAULT_EXECUTOR.get(url, c);
    }

    public static <V> V get(String url, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.get(url, typeReference);
    }

    public static String get(String url, Headers headers) {
        return DEFAULT_EXECUTOR.get(url, headers);
    }

    public static <V> V get(String url, Headers headers, Class<V> c) {
        return DEFAULT_EXECUTOR.get(url, headers, c);
    }

    public static <V> V get(String url, Headers headers, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.get(url, headers, typeReference);
    }

    public static String get(String url, Params params) {
        return DEFAULT_EXECUTOR.get(url, params, String.class);
    }

    public static <V> V get(String url, Params params, Class<V> c) {
        return DEFAULT_EXECUTOR.get(url, params, c);
    }

    public static <V> V get(String url, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.get(url, params, typeReference);
    }

    public static String get(String url, Headers headers, Params params) {
        return DEFAULT_EXECUTOR.get(url, headers, params, String.class);
    }

    public static <V> V get(String url, Headers headers, Params params, Class<V> c) {
        return DEFAULT_EXECUTOR.get(url, headers, params, c);
    }

    public static <V> V get(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.get(url, headers, params, typeReference);
    }

    public static void getAsync(String url) {
        DEFAULT_EXECUTOR.getAsync(url);
    }

    public static void getAsync(String url, RequestCallback callback) {
        DEFAULT_EXECUTOR.getAsync(url, callback);
    }

    public static void getAsync(String url, Params params) {
        DEFAULT_EXECUTOR.getAsync(url, params);
    }

    public static void getAsync(String url, Params params, RequestCallback callback) {
        DEFAULT_EXECUTOR.getAsync(url, params, callback);
    }

    public static void getAsync(String url, Headers headers) {
        DEFAULT_EXECUTOR.getAsync(url, headers);
    }

    public static void getAsync(String url, Headers headers, RequestCallback callback) {
        DEFAULT_EXECUTOR.getAsync(url, headers, callback);
    }

    public static void getAsync(String url, Headers headers, Params params) {
        DEFAULT_EXECUTOR.getAsync(url, headers, params);
    }

    public static void getAsync(String url, Headers headers, Params params, RequestCallback callback) {
        DEFAULT_EXECUTOR.getAsync(url, headers, params, callback);
    }


    /******** post *********/

    public static String post(String url) {
        return DEFAULT_EXECUTOR.post(url);
    }

    public static String post(String url, Headers headers) {
        return DEFAULT_EXECUTOR.post(url, headers);
    }

    public static <T> String post(String url, Headers headers, T t) {
        return DEFAULT_EXECUTOR.post(url, headers, t);
    }

    public static <V> V post(String url, Headers headers, Class<V> c) {
        return DEFAULT_EXECUTOR.post(url, headers, c);
    }

    public static <V> V post(String url, Headers headers, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.post(url, headers, typeReference);
    }

    public static <V, T> V post(String url, Headers headers, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.post(url, headers, t, c);
    }

    public static <V, T> V post(String url, Headers headers, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.post(url, headers, t, typeReference);
    }

    public static String post(String url, Params params) {
        return DEFAULT_EXECUTOR.post(url, params);
    }

    public static <V> V post(String url, Params params, Class<V> c) {
        return DEFAULT_EXECUTOR.post(url, params, c);
    }

    public static <V> V post(String url, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.post(url, params, typeReference);
    }

    public static String post(String url, Headers headers, Params params) {
        return DEFAULT_EXECUTOR.post(url, headers, params);
    }

    public static <V> V post(String url, Headers headers, Params params, Class<V> c) {
        return DEFAULT_EXECUTOR.post(url, headers, params, c);
    }

    public static <V> V post(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.post(url, headers, params, typeReference);
    }

    public static <T> String post(String url, Headers headers, Params params, T t) {
        return DEFAULT_EXECUTOR.post(url, headers, params, t);
    }

    public static <V, T> V post(String url, Headers headers, Params params, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.post(url, headers, params, t, c);
    }

    public static <V, T> V post(String url, Headers headers, Params params, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.post(url, headers, params, t, typeReference);
    }

    public static String postFile(String url, FileParam fileParam) {
        return DEFAULT_EXECUTOR.postFile(url, fileParam);
    }

    public static <V> V postFile(String url, FileParam fileParam, Class<V> c) {
        return DEFAULT_EXECUTOR.postFile(url, fileParam, c);
    }

    public static <V> V postFile(String url, FileParam fileParam, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postFile(url, fileParam, typeReference);
    }

    public static String postFile(String url, Params params, FileParam fileParam) {
        return DEFAULT_EXECUTOR.postFile(url, params, fileParam);
    }

    public static <V> V postFile(String url, Params params, FileParam fileParam, Class<V> c) {
        return DEFAULT_EXECUTOR.postFile(url, params, fileParam, c);
    }

    public static <V> V postFile(String url, Params params, FileParam fileParam, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postFile(url, params, fileParam, typeReference);
    }

    public static String postFile(String url, Params params, List<FileParam> fileParams) {
        return DEFAULT_EXECUTOR.postFile(url, params, fileParams);
    }

    public static <V> V postFile(String url, Params params, List<FileParam> fileParams, Class<V> c) {
        return DEFAULT_EXECUTOR.postFile(url, params, fileParams, c);
    }

    public static <V> V postFile(String url, Params params, List<FileParam> fileParams, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postFile(url, params, fileParams, typeReference);
    }

    public static String postFile(String url, Headers headers, Params params, FileParam fileParam) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParam);
    }

    public static <V> V postFile(String url, Headers headers, Params params, FileParam fileParam, Class<V> c) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParam, c);
    }

    public static <V> V postFile(String url, Headers headers, Params params, FileParam fileParam, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParam, typeReference);
    }

    public static String postFile(String url, Headers headers, Params params, List<FileParam> fileParams) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParams);
    }

    public static <V> V postFile(String url, Headers headers, Params params, List<FileParam> fileParams, Class<V> c) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParams, c);
    }

    public static <V> V postFile(String url, Headers headers, Params params, List<FileParam> fileParams, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParams, typeReference);
    }

    public static <T> String postJson(String url, T t) {
        return DEFAULT_EXECUTOR.postJson(url, t);
    }

    public static <V, T> V postJson(String url, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.postJson(url, t, c);
    }

    public static <V, T> V postJson(String url, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postJson(url, t, typeReference);
    }

    public static <T> String postJson(String url, Headers headers, T t) {
        return DEFAULT_EXECUTOR.postJson(url, headers, t);
    }

    public static <V, T> V postJson(String url, Headers headers, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.postJson(url, headers, t, c);
    }

    public static <V, T> V postJson(String url, Headers headers, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postJson(url, headers, t, typeReference);
    }

    public static <T> String postJson(String url, Params params, T t) {
        return DEFAULT_EXECUTOR.postJson(url, params, t);
    }

    public static <V, T> V postJson(String url, Params params, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.postJson(url, params, t, c);
    }

    public static <V, T> V postJson(String url, Params params, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postJson(url, params, t, typeReference);
    }

    public static <V, T> V postJson(String url, Headers headers, Params params, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.postJson(url, headers, params, t, c);
    }

    public static <T> String postJson(String url, Headers headers, Params params, T t) {
        return DEFAULT_EXECUTOR.postJson(url, headers, params, t);
    }

    public static <V, T> V postJson(String url, Headers headers, Params params, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postJson(url, headers, params, t, typeReference);
    }

    public static void postAsync(String url) {
        DEFAULT_EXECUTOR.postAsync(url);
    }

    public static void postAsync(String url, RequestCallback callback) {
        DEFAULT_EXECUTOR.postAsync(url, callback);
    }

    public static void postAsync(String url, Headers headers) {
        DEFAULT_EXECUTOR.postAsync(url, headers);
    }

    public static <T> void postAsync(String url, Headers headers, T t) {
        DEFAULT_EXECUTOR.postAsync(url, headers, t);
    }

    public static void postAsync(String url, Headers headers, RequestCallback callback) {
        DEFAULT_EXECUTOR.postAsync(url, headers, callback);
    }

    public static <T> void postAsync(String url, Headers headers, T t, RequestCallback callback) {
        DEFAULT_EXECUTOR.postAsync(url, headers, t, callback);
    }

    public static void postAsync(String url, Params params) {
        DEFAULT_EXECUTOR.postAsync(url, params);
    }

    public static void postAsync(String url, Params params, RequestCallback callback) {
        DEFAULT_EXECUTOR.postAsync(url, params, callback);
    }

    public static void postAsync(String url, Headers headers, Params params) {
        DEFAULT_EXECUTOR.postAsync(url, headers, params);
    }

    public static void postAsync(String url, Headers headers, Params params, RequestCallback callback) {
        DEFAULT_EXECUTOR.postAsync(url, headers, params, callback);
    }

    public static <T> void postAsync(String url, Headers headers, Params params, T t) {
        DEFAULT_EXECUTOR.postAsync(url, headers, params, t);
    }

    public static <T> void postAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        DEFAULT_EXECUTOR.postAsync(url, headers, params, t, callback);
    }

    public static void postFileAsync(String url, FileParam fileParam) {
        DEFAULT_EXECUTOR.postFileAsync(url, fileParam);
    }

    public static void postFileAsync(String url, FileParam fileParam, RequestCallback callback) {
        DEFAULT_EXECUTOR.postFileAsync(url, fileParam, callback);
    }

    public static void postFileAsync(String url, Params params, FileParam fileParam) {
        DEFAULT_EXECUTOR.postFileAsync(url, params, fileParam);
    }

    public static void postFileAsync(String url, Params params, FileParam fileParam, RequestCallback callback) {
        DEFAULT_EXECUTOR.postFileAsync(url, params, fileParam, callback);
    }

    public static void postFileAsync(String url, Params params, List<FileParam> fileParams) {
        DEFAULT_EXECUTOR.postFileAsync(url, params, fileParams);
    }

    public static void postFileAsync(String url, Params params, List<FileParam> fileParams, RequestCallback callback) {
        DEFAULT_EXECUTOR.postFileAsync(url, params, fileParams, callback);
    }

    public static void postFileAsync(String url, Headers headers, Params params, FileParam fileParam, RequestCallback callback) {
        DEFAULT_EXECUTOR.postFileAsync(url, headers, params, fileParam, callback);
    }

    public static void postFileAsync(String url, Headers headers, Params params, List<FileParam> fileParams) {
        DEFAULT_EXECUTOR.postFileAsync(url, headers, params, fileParams);
    }

    public static void postFileAsync(String url, Headers headers, Params params, List<FileParam> fileParams, RequestCallback callback) {
        DEFAULT_EXECUTOR.postFileAsync(url, headers, params, fileParams, callback);
    }

    public static <T> void postJsonAsync(String url, T t) {
        DEFAULT_EXECUTOR.postJsonAsync(url, t);
    }

    public static <T> void postJsonAsync(String url, T t, RequestCallback callback) {
        DEFAULT_EXECUTOR.postJsonAsync(url, t, callback);
    }

    public static <T> void postJsonAsync(String url, Headers headers, T t) {
        DEFAULT_EXECUTOR.postJsonAsync(url, headers, t);
    }

    public static <T> void postJsonAsync(String url, Headers headers, T t, RequestCallback callback) {
        DEFAULT_EXECUTOR.postJsonAsync(url, headers, t, callback);
    }

    public static <T> void postJsonAsync(String url, Params params, T t) {
        DEFAULT_EXECUTOR.postJsonAsync(url, params, t);
    }

    public static <T> void postJsonAsync(String url, Params params, T t, RequestCallback callback) {
        DEFAULT_EXECUTOR.postJsonAsync(url, params, t, callback);
    }

    public static <T> void postJsonAsync(String url, Headers headers, Params params, T t) {
        DEFAULT_EXECUTOR.postJsonAsync(url, headers, params, t);
    }

    public static <T> void postJsonAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        DEFAULT_EXECUTOR.postJsonAsync(url, headers, params, t, callback);
    }


    /******** put *********/

    public static String put(String url) {
        return DEFAULT_EXECUTOR.put(url);
    }

    public static String put(String url, Headers headers) {
        return DEFAULT_EXECUTOR.put(url, headers);
    }

    public static <T> String put(String url, Headers headers, T t) {
        return DEFAULT_EXECUTOR.put(url, headers, t);
    }

    public static <V> V put(String url, Headers headers, Class<V> c) {
        return DEFAULT_EXECUTOR.put(url, headers, c);
    }

    public static <V> V put(String url, Headers headers, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.put(url, headers, typeReference);
    }

    public static <V, T> V put(String url, Headers headers, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.put(url, headers, t, c);
    }

    public static <V, T> V put(String url, Headers headers, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.put(url, headers, t, typeReference);
    }

    public static String put(String url, Params params) {
        return DEFAULT_EXECUTOR.put(url, params);
    }

    public static <V> V put(String url, Params params, Class<V> c) {
        return DEFAULT_EXECUTOR.put(url, params, c);
    }

    public static <V> V put(String url, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.put(url, params, typeReference);
    }

    public static String put(String url, Headers headers, Params params) {
        return DEFAULT_EXECUTOR.put(url, headers, params);
    }

    public static <V> V put(String url, Headers headers, Params params, Class<V> c) {
        return DEFAULT_EXECUTOR.put(url, headers, params, c);
    }

    public static <V> V put(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.put(url, headers, params, typeReference);
    }

    public static <T> String put(String url, Headers headers, Params params, T t) {
        return DEFAULT_EXECUTOR.put(url, headers, params, t);
    }

    public static <V, T> V put(String url, Headers headers, Params params, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.put(url, headers, params, t, c);
    }

    public static <V, T> V put(String url, Headers headers, Params params, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.put(url, headers, params, t, typeReference);
    }

    public static <T> String putJson(String url, T t) {
        return DEFAULT_EXECUTOR.putJson(url, t);
    }

    public static <V, T> V putJson(String url, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.putJson(url, t, c);
    }

    public static <V, T> V putJson(String url, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.putJson(url, t, typeReference);
    }

    public static <T> String putJson(String url, Headers headers, T t) {
        return DEFAULT_EXECUTOR.putJson(url, headers, t);
    }

    public static <V, T> V putJson(String url, Headers headers, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.putJson(url, headers, t, c);
    }

    public static <V, T> V putJson(String url, Headers headers, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.putJson(url, headers, t, typeReference);
    }

    public static <T> String putJson(String url, Params params, T t) {
        return DEFAULT_EXECUTOR.putJson(url, params, t);
    }

    public static <V, T> V putJson(String url, Params params, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.putJson(url, params, t, c);
    }

    public static <V, T> V putJson(String url, Params params, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.putJson(url, params, t, typeReference);
    }

    public static <T> String putJson(String url, Headers headers, Params params, T t) {
        return DEFAULT_EXECUTOR.putJson(url, headers, params, t);
    }

    public static <V, T> V putJson(String url, Headers headers, Params params, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.putJson(url, headers, params, t, c);
    }

    public static <V, T> V putJson(String url, Headers headers, Params params, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.putJson(url, headers, params, t, typeReference);
    }

    public static void putAsync(String url) {
        DEFAULT_EXECUTOR.putAsync(url);
    }

    public static void putAsync(String url, Headers headers) {
        DEFAULT_EXECUTOR.putAsync(url, headers);
    }

    public static <T> void putAsync(String url, Headers headers, T t) {
        DEFAULT_EXECUTOR.putAsync(url, headers, t);
    }

    public static void putAsync(String url, Headers headers, RequestCallback callback) {
        DEFAULT_EXECUTOR.putAsync(url, headers, callback);
    }

    public static void putAsync(String url, Params params) {
        DEFAULT_EXECUTOR.putAsync(url, params);
    }

    public static void putAsync(String url, Params params, RequestCallback callback) {
        DEFAULT_EXECUTOR.putAsync(url, params, callback);
    }

    public static void putAsync(String url, Headers headers, Params params, RequestCallback callback) {
        DEFAULT_EXECUTOR.putAsync(url, headers, params, callback);
    }

    public static <T> void putAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        DEFAULT_EXECUTOR.putAsync(url, headers, params, t, callback);
    }

    public static <T> void putJsonAsync(String url, T t) {
        DEFAULT_EXECUTOR.putJsonAsync(url, t);
    }

    public static <T> void putJsonAsync(String url, T t, RequestCallback callback) {
        DEFAULT_EXECUTOR.putJsonAsync(url, t, callback);
    }

    public static <T> void putJsonAsync(String url, Headers headers, T t) {
        DEFAULT_EXECUTOR.putJsonAsync(url, headers, t);
    }

    public static <T> void putJsonAsync(String url, Headers headers, T t, RequestCallback callback) {
        DEFAULT_EXECUTOR.putJsonAsync(url, headers, t, callback);
    }

    public static <T> void putJsonAsync(String url, Headers headers, Params params, T t) {
        DEFAULT_EXECUTOR.putJsonAsync(url, headers, params, t);
    }

    public static <T> void putJsonAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        DEFAULT_EXECUTOR.putJsonAsync(url, headers, params, t, callback);
    }


    /******** patch *********/

    public static String patch(String url) {
        return DEFAULT_EXECUTOR.patch(url);
    }

    public static <V> V patch(String url, Class<V> c) {
        return DEFAULT_EXECUTOR.patch(url, c);
    }

    public static <V> V patch(String url, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.patch(url, typeReference);
    }

    public static String patch(String url, Headers headers) {
        return DEFAULT_EXECUTOR.patch(url, headers);
    }

    public static <V> V patch(String url, Headers headers, Class<V> c) {
        return DEFAULT_EXECUTOR.patch(url, headers, c);
    }

    public static <V> V patch(String url, Headers headers, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.patch(url, headers, typeReference);
    }

    public static <T> String patch(String url, Headers headers, T t) {
        return DEFAULT_EXECUTOR.patch(url, headers, t);
    }

    public static <V, T> V patch(String url, Headers headers, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.patch(url, headers, t, c);
    }

    public static <V, T> V patch(String url, Headers headers, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.patch(url, headers, t, typeReference);
    }

    public static String patch(String url, Params params) {
        return DEFAULT_EXECUTOR.patch(url, params);
    }

    public static <V> V patch(String url, Params params, Class<V> c) {
        return DEFAULT_EXECUTOR.patch(url, params, c);
    }

    public static <V> V patch(String url, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.patch(url, params, typeReference);
    }

    public static String patch(String url, Headers headers, Params params) {
        return DEFAULT_EXECUTOR.patch(url, headers, params);
    }

    public static <V> V patch(String url, Headers headers, Params params, Class<V> c) {
        return DEFAULT_EXECUTOR.patch(url, headers, params, c);
    }

    public static <V> V patch(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.patch(url, headers, params, typeReference);
    }

    public static <T> String patch(String url, Headers headers, Params params, T t) {
        return DEFAULT_EXECUTOR.patch(url, headers, params, t);
    }

    public static <V, T> V patch(String url, Headers headers, Params params, T t, Class<V> c) {
        return DEFAULT_EXECUTOR.patch(url, headers, params, t, c);
    }

    public static <V, T> V patch(String url, Headers headers, Params params, T t, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.patch(url, headers, params, t, typeReference);
    }

    public static void patchAsync(String url) {
        DEFAULT_EXECUTOR.patchAsync(url);
    }

    public static void patchAsync(String url, RequestCallback callback) {
        DEFAULT_EXECUTOR.patchAsync(url, callback);
    }

    public static void patchAsync(String url, Headers headers) {
        DEFAULT_EXECUTOR.patchAsync(url, headers);
    }

    public static void patchAsync(String url, Headers headers, RequestCallback callback) {
        DEFAULT_EXECUTOR.patchAsync(url, headers, callback);
    }

    public static <T> void patchAsync(String url, Headers headers, T t) {
        DEFAULT_EXECUTOR.patchAsync(url, headers, t);
    }

    public static <T> void patchAsync(String url, Headers headers, T t, RequestCallback callback) {
        DEFAULT_EXECUTOR.patchAsync(url, headers, t, callback);
    }

    public static void patchAsync(String url, Params params) {
        DEFAULT_EXECUTOR.patchAsync(url, params);
    }

    public static void patchAsync(String url, Params params, RequestCallback callback) {
        DEFAULT_EXECUTOR.patchAsync(url, params, callback);
    }

    public static void patchAsync(String url, Headers headers, Params params) {
        DEFAULT_EXECUTOR.patchAsync(url, headers, params);
    }

    public static void patchAsync(String url, Headers headers, Params params, RequestCallback callback) {
        DEFAULT_EXECUTOR.patchAsync(url, headers, params, callback);
    }

    public static <T> void patchAsync(String url, Headers headers, Params params, T t) {
        DEFAULT_EXECUTOR.patchAsync(url, headers, params, t);
    }

    public static <T> void patchAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        DEFAULT_EXECUTOR.patchAsync(url, headers, params, t, callback);
    }


    /******** delete *********/

    public static String delete(String url) {
        return DEFAULT_EXECUTOR.delete(url);
    }

    public static <V> V delete(String url, Class<V> c) {
        return DEFAULT_EXECUTOR.delete(url, c);
    }

    public static <V> V delete(String url, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.delete(url, typeReference);
    }

    public static String delete(String url, Headers headers) {
        return DEFAULT_EXECUTOR.delete(url, headers);
    }

    public static <V> V delete(String url, Headers headers, Class<V> c) {
        return DEFAULT_EXECUTOR.delete(url, headers, c);
    }

    public static <V> V delete(String url, Headers headers, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.delete(url, headers, typeReference);
    }

    public static String delete(String url, Params params) {
        return DEFAULT_EXECUTOR.delete(url, params);
    }

    public static <V> V delete(String url, Params params, Class<V> c) {
        return DEFAULT_EXECUTOR.delete(url, params, c);
    }

    public static <V> V delete(String url, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.delete(url, params, typeReference);
    }

    public static String delete(String url, Headers headers, Params params) {
        return DEFAULT_EXECUTOR.delete(url, headers, params);
    }

    public static <V> V delete(String url, Headers headers, Params params, Class<V> c) {
        return DEFAULT_EXECUTOR.delete(url, headers, params, c);
    }

    public static <V> V delete(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.delete(url, headers, params, typeReference);
    }

    public static void deleteAsync(String url) {
        DEFAULT_EXECUTOR.deleteAsync(url);
    }

    public static void deleteAsync(String url, RequestCallback callback) {
        DEFAULT_EXECUTOR.deleteAsync(url, callback);
    }

    public static void deleteAsync(String url, Headers headers) {
        DEFAULT_EXECUTOR.deleteAsync(url, headers);
    }

    public static void deleteAsync(String url, Headers headers, RequestCallback callback) {
        DEFAULT_EXECUTOR.deleteAsync(url, headers, callback);
    }

    public static void deleteAsync(String url, Params params) {
        DEFAULT_EXECUTOR.deleteAsync(url, params);
    }

    public static void deleteAsync(String url, Params params, RequestCallback callback) {
        DEFAULT_EXECUTOR.deleteAsync(url, params, callback);
    }

    public static void deleteAsync(String url, Headers headers, Params params, RequestCallback callback) {
        DEFAULT_EXECUTOR.deleteAsync(url, headers, params, callback);
    }

    /******** download *********/

    public static void download(String url, String targetPath) {
        DEFAULT_EXECUTOR.download(url, targetPath);
    }

    public static void download(String url, Params params, String targetPath) {
        DEFAULT_EXECUTOR.download(url, targetPath);
    }

    public static void downloadAsync(String url, String targetPath) {
        DEFAULT_EXECUTOR.downloadAsync(url, targetPath);
    }

}
