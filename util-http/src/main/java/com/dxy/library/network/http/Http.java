package com.dxy.library.network.http;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.constant.Method;
import com.dxy.library.network.http.executor.Executor;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.interceptor.RetryInterceptor;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import com.dxy.library.network.http.serializer.DefaultSerializer;
import com.dxy.library.network.http.serializer.HttpSerializer;
import com.dxy.library.util.config.ConfigUtils;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Http执行类，默认开启日志，不想开启日志可以调用log方法设置log，或者屏蔽log
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public final class Http {

    //默认序列化实现
    private static HttpSerializer defaultHttpSerializer = new DefaultSerializer();

    //默认开启请求日志
    private static final boolean DEFAULT_LOG_ENABLE = ConfigUtils.getAsBoolean("http.defaultRequestLogEnable", true);

    //默认超时时间为60秒
    private static final int DEFAULT_TIMEOUT_MILLIS = ConfigUtils.getAsInt("http.defaultTimoutMillis", 60000);

    //默认请求失败重试次数为0次
    private static final int DEFAULT_RETRIES = ConfigUtils.getAsInt("http.defaultRetries", 0);

    //默认不使用HTTP/2 without TLS协议
    private static final boolean ENABLE_H2C_DEFAULT = ConfigUtils.getAsBoolean("http.h2c.enable", false);

    //默认执行器，开启请求日志，60秒超时时间，0次重试
    private static final Executor DEFAULT_EXECUTOR = new Executor(defaultHttpSerializer, DEFAULT_LOG_ENABLE, DEFAULT_TIMEOUT_MILLIS, DEFAULT_RETRIES, RetryInterceptor.RETRY_INTERVAL_DEFAULT, false);

    /**
     * 设置请求日志开关
     */
    public static Executor requestLogEnable(boolean requestLogEnable) {
        return Executor.getExecutor(defaultHttpSerializer, requestLogEnable, DEFAULT_TIMEOUT_MILLIS, DEFAULT_RETRIES, ENABLE_H2C_DEFAULT);
    }

    /**
     * 使用HTTP/2 without TLS协议
     */
    public static Executor enableH2c() {
        return Executor.getExecutor(defaultHttpSerializer, DEFAULT_LOG_ENABLE, DEFAULT_TIMEOUT_MILLIS, DEFAULT_RETRIES, true);
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
     * 设置请求超时时间，单位为秒
     */
    public static Executor timeout(int timeout) {
        return Executor.getExecutor(defaultHttpSerializer, DEFAULT_LOG_ENABLE, TimeUnit.SECONDS.toMillis(timeout), DEFAULT_RETRIES, ENABLE_H2C_DEFAULT);
    }

    /**
     * 设置请求超时时间
     */
    public static Executor timeout(long timeout, TimeUnit timeUnit) {
        return Executor.getExecutor(defaultHttpSerializer, DEFAULT_LOG_ENABLE, timeUnit.toMillis(timeout), DEFAULT_RETRIES, ENABLE_H2C_DEFAULT);
    }

    /**
     * 设置请求超时时间，单位为毫秒
     */
    public static Executor timeoutMillis(long timeoutMillis) {
        return Executor.getExecutor(defaultHttpSerializer, DEFAULT_LOG_ENABLE, timeoutMillis, DEFAULT_RETRIES, ENABLE_H2C_DEFAULT);
    }

    /**
     * 设置失败重试次数
     */
    public static Executor retries(int retries) {
        return Executor.getExecutor(defaultHttpSerializer, DEFAULT_LOG_ENABLE, DEFAULT_TIMEOUT_MILLIS, retries, ENABLE_H2C_DEFAULT);
    }

    /**
     * 设置失败重试次数
     */
    public static Executor retries(int retries, long retryIntervalMillis) {
        return Executor.getExecutor(defaultHttpSerializer, DEFAULT_LOG_ENABLE, DEFAULT_TIMEOUT_MILLIS, retries, retryIntervalMillis, ENABLE_H2C_DEFAULT);
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
        DEFAULT_EXECUTOR.getRequester().setTimeoutMillis(TimeUnit.SECONDS.toMillis(timeout));
    }

    /**
     * 设置全局超时时间，单位为秒
     */
    public static void setDefaultTimeout(long timeout, TimeUnit timeUnit) {
        DEFAULT_EXECUTOR.getRequester().setTimeoutMillis(timeUnit.toMillis(timeout));
    }

    /**
     * 设置全局超时时间，单位为秒
     */
    public static void setDefaultTimeoutMillis(long timeoutMillis) {
        DEFAULT_EXECUTOR.getRequester().setTimeoutMillis(timeoutMillis);
    }

    /**
     * 设置全局重试次数
     */
    public static void setDefaultRetries(int retries) {
        DEFAULT_EXECUTOR.getRequester().setRetries(retries);
    }

    /**
     * 设置全局 h2c 开关
     * @param enable 是否开启
     */
    public static void setDefaultEnableH2c(boolean enable) {
        DEFAULT_EXECUTOR.getRequester().setEnableH2c(enable);
    }

    /******** get *********/

    public static String get(String url) {
        return DEFAULT_EXECUTOR.get(url);
    }

    public static <V> V get(String url, Class<V> type) {
        return DEFAULT_EXECUTOR.get(url, type);
    }

    public static <V> V get(String url, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.get(url, typeReference);
    }

    public static String get(String url, Headers headers) {
        return DEFAULT_EXECUTOR.get(url, headers);
    }

    public static <V> V get(String url, Headers headers, Class<V> type) {
        return DEFAULT_EXECUTOR.get(url, headers, type);
    }

    public static <V> V get(String url, Headers headers, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.get(url, headers, typeReference);
    }

    public static String get(String url, Params params) {
        return DEFAULT_EXECUTOR.get(url, params, String.class);
    }

    public static <V> V get(String url, Params params, Class<V> type) {
        return DEFAULT_EXECUTOR.get(url, params, type);
    }

    public static <V> V get(String url, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.get(url, params, typeReference);
    }

    public static String get(String url, Headers headers, Params params) {
        return DEFAULT_EXECUTOR.get(url, headers, params, String.class);
    }

    public static <V> V get(String url, Headers headers, Params params, Class<V> type) {
        return DEFAULT_EXECUTOR.get(url, headers, params, type);
    }

    public static <V> V get(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.get(url, headers, params, typeReference);
    }

    public static <V> V get(String url, Headers headers, Params params, Type type) {
        return DEFAULT_EXECUTOR.get(url, headers, params, type);
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

    public static <T> String post(String url, Headers headers, T body) {
        return DEFAULT_EXECUTOR.post(url, headers, body);
    }

    public static <V> V post(String url, Headers headers, Class<V> type) {
        return DEFAULT_EXECUTOR.post(url, headers, type);
    }

    public static <V> V post(String url, Headers headers, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.post(url, headers, typeReference);
    }

    public static <V, T> V post(String url, Headers headers, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.post(url, headers, body, type);
    }

    public static <V, T> V post(String url, Headers headers, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.post(url, headers, body, typeReference);
    }

    public static String post(String url, Params params) {
        return DEFAULT_EXECUTOR.post(url, params);
    }

    public static <V> V post(String url, Params params, Class<V> type) {
        return DEFAULT_EXECUTOR.post(url, params, type);
    }

    public static <V> V post(String url, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.post(url, params, typeReference);
    }

    public static String post(String url, Headers headers, Params params) {
        return DEFAULT_EXECUTOR.post(url, headers, params);
    }

    public static <V> V post(String url, Headers headers, Params params, Class<V> type) {
        return DEFAULT_EXECUTOR.post(url, headers, params, type);
    }

    public static <V> V post(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.post(url, headers, params, typeReference);
    }

    public static <T> String post(String url, Headers headers, Params params, T body) {
        return DEFAULT_EXECUTOR.post(url, headers, params, body);
    }

    public static <V, T> V post(String url, Headers headers, Params params, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.post(url, headers, params, body, type);
    }

    public static <V, T> V post(String url, Headers headers, Params params, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.post(url, headers, params, body, typeReference);
    }

    public static <V, T> V post(String url, Headers headers, Params params, T body, Type type) {
        return DEFAULT_EXECUTOR.post(url, headers, params, body, type);
    }

    public static String postFile(String url, FileParam fileParam) {
        return DEFAULT_EXECUTOR.postFile(url, fileParam);
    }

    public static <V> V postFile(String url, FileParam fileParam, Class<V> type) {
        return DEFAULT_EXECUTOR.postFile(url, fileParam, type);
    }

    public static <V> V postFile(String url, FileParam fileParam, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postFile(url, fileParam, typeReference);
    }

    public static String postFile(String url, Params params, FileParam fileParam) {
        return DEFAULT_EXECUTOR.postFile(url, params, fileParam);
    }

    public static <V> V postFile(String url, Params params, FileParam fileParam, Class<V> type) {
        return DEFAULT_EXECUTOR.postFile(url, params, fileParam, type);
    }

    public static <V> V postFile(String url, Params params, FileParam fileParam, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postFile(url, params, fileParam, typeReference);
    }

    public static String postFile(String url, Headers headers, Params params, FileParam fileParam) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParam);
    }

    public static <V> V postFile(String url, Headers headers, Params params, FileParam fileParam, Class<V> type) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParam, type);
    }

    public static <V> V postFile(String url, Headers headers, Params params, FileParam fileParam, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParam, typeReference);
    }

    public static <V> V postFile(String url, Headers headers, Params params, FileParam fileParam, Type type) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParam, type);
    }

    public static String postFile(String url, Params params, List<FileParam> fileParams) {
        return DEFAULT_EXECUTOR.postFile(url, params, fileParams);
    }

    public static <V> V postFile(String url, Params params, List<FileParam> fileParams, Class<V> type) {
        return DEFAULT_EXECUTOR.postFile(url, params, fileParams, type);
    }

    public static <V> V postFile(String url, Params params, List<FileParam> fileParams, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postFile(url, params, fileParams, typeReference);
    }

    public static String postFile(String url, Headers headers, Params params, List<FileParam> fileParams) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParams);
    }

    public static <V> V postFile(String url, Headers headers, Params params, List<FileParam> fileParams, Class<V> type) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParams, type);
    }

    public static <V> V postFile(String url, Headers headers, Params params, List<FileParam> fileParams, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParams, typeReference);
    }

    public static <V> V postFile(String url, Headers headers, Params params, List<FileParam> fileParams, Type type) {
        return DEFAULT_EXECUTOR.postFile(url, headers, params, fileParams, type);
    }

    public static <T> String postJson(String url, T body) {
        return DEFAULT_EXECUTOR.postJson(url, body);
    }

    public static <V, T> V postJson(String url, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.postJson(url, body, type);
    }

    public static <V, T> V postJson(String url, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postJson(url, body, typeReference);
    }

    public static <T> String postJson(String url, Headers headers, T body) {
        return DEFAULT_EXECUTOR.postJson(url, headers, body);
    }

    public static <V, T> V postJson(String url, Headers headers, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.postJson(url, headers, body, type);
    }

    public static <V, T> V postJson(String url, Headers headers, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postJson(url, headers, body, typeReference);
    }

    public static <T> String postJson(String url, Params params, T body) {
        return DEFAULT_EXECUTOR.postJson(url, params, body);
    }

    public static <V, T> V postJson(String url, Params params, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.postJson(url, params, body, type);
    }

    public static <V, T> V postJson(String url, Params params, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postJson(url, params, body, typeReference);
    }

    public static <V, T> V postJson(String url, Headers headers, Params params, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.postJson(url, headers, params, body, type);
    }

    public static <T> String postJson(String url, Headers headers, Params params, T body) {
        return DEFAULT_EXECUTOR.postJson(url, headers, params, body);
    }

    public static <V, T> V postJson(String url, Headers headers, Params params, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.postJson(url, headers, params, body, typeReference);
    }

    public static <V, T> V postJson(String url, Headers headers, Params params, T body, Type type) {
        return DEFAULT_EXECUTOR.postJson(url, headers, params, body, type);
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

    public static <T> void postAsync(String url, Headers headers, T body) {
        DEFAULT_EXECUTOR.postAsync(url, headers, body);
    }

    public static void postAsync(String url, Headers headers, RequestCallback callback) {
        DEFAULT_EXECUTOR.postAsync(url, headers, callback);
    }

    public static <T> void postAsync(String url, Headers headers, T body, RequestCallback callback) {
        DEFAULT_EXECUTOR.postAsync(url, headers, body, callback);
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

    public static <T> void postAsync(String url, Headers headers, Params params, T body) {
        DEFAULT_EXECUTOR.postAsync(url, headers, params, body);
    }

    public static <T> void postAsync(String url, Headers headers, Params params, T body, RequestCallback callback) {
        DEFAULT_EXECUTOR.postAsync(url, headers, params, body, callback);
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

    public static <T> void postJsonAsync(String url, T body) {
        DEFAULT_EXECUTOR.postJsonAsync(url, body);
    }

    public static <T> void postJsonAsync(String url, T body, RequestCallback callback) {
        DEFAULT_EXECUTOR.postJsonAsync(url, body, callback);
    }

    public static <T> void postJsonAsync(String url, Headers headers, T body) {
        DEFAULT_EXECUTOR.postJsonAsync(url, headers, body);
    }

    public static <T> void postJsonAsync(String url, Headers headers, T body, RequestCallback callback) {
        DEFAULT_EXECUTOR.postJsonAsync(url, headers, body, callback);
    }

    public static <T> void postJsonAsync(String url, Params params, T body) {
        DEFAULT_EXECUTOR.postJsonAsync(url, params, body);
    }

    public static <T> void postJsonAsync(String url, Params params, T body, RequestCallback callback) {
        DEFAULT_EXECUTOR.postJsonAsync(url, params, body, callback);
    }

    public static <T> void postJsonAsync(String url, Headers headers, Params params, T body) {
        DEFAULT_EXECUTOR.postJsonAsync(url, headers, params, body);
    }

    public static <T> void postJsonAsync(String url, Headers headers, Params params, T body, RequestCallback callback) {
        DEFAULT_EXECUTOR.postJsonAsync(url, headers, params, body, callback);
    }


    /******** put *********/

    public static String put(String url) {
        return DEFAULT_EXECUTOR.put(url);
    }

    public static String put(String url, Headers headers) {
        return DEFAULT_EXECUTOR.put(url, headers);
    }

    public static <T> String put(String url, Headers headers, T body) {
        return DEFAULT_EXECUTOR.put(url, headers, body);
    }

    public static <V> V put(String url, Headers headers, Class<V> type) {
        return DEFAULT_EXECUTOR.put(url, headers, type);
    }

    public static <V> V put(String url, Headers headers, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.put(url, headers, typeReference);
    }

    public static <V, T> V put(String url, Headers headers, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.put(url, headers, body, type);
    }

    public static <V, T> V put(String url, Headers headers, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.put(url, headers, body, typeReference);
    }

    public static String put(String url, Params params) {
        return DEFAULT_EXECUTOR.put(url, params);
    }

    public static <V> V put(String url, Params params, Class<V> type) {
        return DEFAULT_EXECUTOR.put(url, params, type);
    }

    public static <V> V put(String url, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.put(url, params, typeReference);
    }

    public static String put(String url, Headers headers, Params params) {
        return DEFAULT_EXECUTOR.put(url, headers, params);
    }

    public static <V> V put(String url, Headers headers, Params params, Class<V> type) {
        return DEFAULT_EXECUTOR.put(url, headers, params, type);
    }

    public static <V> V put(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.put(url, headers, params, typeReference);
    }

    public static <T> String put(String url, Headers headers, Params params, T body) {
        return DEFAULT_EXECUTOR.put(url, headers, params, body);
    }

    public static <V, T> V put(String url, Headers headers, Params params, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.put(url, headers, params, body, type);
    }

    public static <V, T> V put(String url, Headers headers, Params params, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.put(url, headers, params, body, typeReference);
    }

    public static <V, T> V put(String url, Headers headers, Params params, T body, Type type) {
        return DEFAULT_EXECUTOR.put(url, headers, params, body, type);
    }

    public static <T> String putJson(String url, T body) {
        return DEFAULT_EXECUTOR.putJson(url, body);
    }

    public static <V, T> V putJson(String url, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.putJson(url, body, type);
    }

    public static <V, T> V putJson(String url, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.putJson(url, body, typeReference);
    }

    public static <T> String putJson(String url, Headers headers, T body) {
        return DEFAULT_EXECUTOR.putJson(url, headers, body);
    }

    public static <V, T> V putJson(String url, Headers headers, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.putJson(url, headers, body, type);
    }

    public static <V, T> V putJson(String url, Headers headers, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.putJson(url, headers, body, typeReference);
    }

    public static <T> String putJson(String url, Params params, T body) {
        return DEFAULT_EXECUTOR.putJson(url, params, body);
    }

    public static <V, T> V putJson(String url, Params params, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.putJson(url, params, body, type);
    }

    public static <V, T> V putJson(String url, Params params, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.putJson(url, params, body, typeReference);
    }

    public static <T> String putJson(String url, Headers headers, Params params, T body) {
        return DEFAULT_EXECUTOR.putJson(url, headers, params, body);
    }

    public static <V, T> V putJson(String url, Headers headers, Params params, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.putJson(url, headers, params, body, type);
    }

    public static <V, T> V putJson(String url, Headers headers, Params params, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.putJson(url, headers, params, body, typeReference);
    }

    public static <V, T> V putJson(String url, Headers headers, Params params, T body, Type type) {
        return DEFAULT_EXECUTOR.putJson(url, headers, params, body, type);
    }

    public static void putAsync(String url) {
        DEFAULT_EXECUTOR.putAsync(url);
    }

    public static void putAsync(String url, Headers headers) {
        DEFAULT_EXECUTOR.putAsync(url, headers);
    }

    public static <T> void putAsync(String url, Headers headers, T body) {
        DEFAULT_EXECUTOR.putAsync(url, headers, body);
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

    public static <T> void putAsync(String url, Headers headers, Params params, T body, RequestCallback callback) {
        DEFAULT_EXECUTOR.putAsync(url, headers, params, body, callback);
    }

    public static <T> void putJsonAsync(String url, T body) {
        DEFAULT_EXECUTOR.putJsonAsync(url, body);
    }

    public static <T> void putJsonAsync(String url, T body, RequestCallback callback) {
        DEFAULT_EXECUTOR.putJsonAsync(url, body, callback);
    }

    public static <T> void putJsonAsync(String url, Headers headers, T body) {
        DEFAULT_EXECUTOR.putJsonAsync(url, headers, body);
    }

    public static <T> void putJsonAsync(String url, Headers headers, T body, RequestCallback callback) {
        DEFAULT_EXECUTOR.putJsonAsync(url, headers, body, callback);
    }

    public static <T> void putJsonAsync(String url, Headers headers, Params params, T body) {
        DEFAULT_EXECUTOR.putJsonAsync(url, headers, params, body);
    }

    public static <T> void putJsonAsync(String url, Headers headers, Params params, T body, RequestCallback callback) {
        DEFAULT_EXECUTOR.putJsonAsync(url, headers, params, body, callback);
    }


    /******** patch *********/

    public static String patch(String url) {
        return DEFAULT_EXECUTOR.patch(url);
    }

    public static <V> V patch(String url, Class<V> type) {
        return DEFAULT_EXECUTOR.patch(url, type);
    }

    public static <V> V patch(String url, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.patch(url, typeReference);
    }

    public static String patch(String url, Headers headers) {
        return DEFAULT_EXECUTOR.patch(url, headers);
    }

    public static <V> V patch(String url, Headers headers, Class<V> type) {
        return DEFAULT_EXECUTOR.patch(url, headers, type);
    }

    public static <V> V patch(String url, Headers headers, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.patch(url, headers, typeReference);
    }

    public static <T> String patch(String url, Headers headers, T body) {
        return DEFAULT_EXECUTOR.patch(url, headers, body);
    }

    public static <V, T> V patch(String url, Headers headers, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.patch(url, headers, body, type);
    }

    public static <V, T> V patch(String url, Headers headers, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.patch(url, headers, body, typeReference);
    }

    public static String patch(String url, Params params) {
        return DEFAULT_EXECUTOR.patch(url, params);
    }

    public static <V> V patch(String url, Params params, Class<V> type) {
        return DEFAULT_EXECUTOR.patch(url, params, type);
    }

    public static <V> V patch(String url, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.patch(url, params, typeReference);
    }

    public static String patch(String url, Headers headers, Params params) {
        return DEFAULT_EXECUTOR.patch(url, headers, params);
    }

    public static <V> V patch(String url, Headers headers, Params params, Class<V> type) {
        return DEFAULT_EXECUTOR.patch(url, headers, params, type);
    }

    public static <V> V patch(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.patch(url, headers, params, typeReference);
    }

    public static <T> String patch(String url, Headers headers, Params params, T body) {
        return DEFAULT_EXECUTOR.patch(url, headers, params, body);
    }

    public static <V, T> V patch(String url, Headers headers, Params params, T body, Class<V> type) {
        return DEFAULT_EXECUTOR.patch(url, headers, params, body, type);
    }

    public static <V, T> V patch(String url, Headers headers, Params params, T body, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.patch(url, headers, params, body, typeReference);
    }

    public static <V, T> V patch(String url, Headers headers, Params params, T body, Type type) {
        return DEFAULT_EXECUTOR.patch(url, headers, params, body, type);
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

    public static <T> void patchAsync(String url, Headers headers, T body) {
        DEFAULT_EXECUTOR.patchAsync(url, headers, body);
    }

    public static <T> void patchAsync(String url, Headers headers, T body, RequestCallback callback) {
        DEFAULT_EXECUTOR.patchAsync(url, headers, body, callback);
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

    public static <T> void patchAsync(String url, Headers headers, Params params, T body) {
        DEFAULT_EXECUTOR.patchAsync(url, headers, params, body);
    }

    public static <T> void patchAsync(String url, Headers headers, Params params, T body, RequestCallback callback) {
        DEFAULT_EXECUTOR.patchAsync(url, headers, params, body, callback);
    }

    /******** delete *********/

    public static String delete(String url) {
        return DEFAULT_EXECUTOR.delete(url);
    }

    public static <V> V delete(String url, Class<V> type) {
        return DEFAULT_EXECUTOR.delete(url, type);
    }

    public static <V> V delete(String url, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.delete(url, typeReference);
    }

    public static String delete(String url, Headers headers) {
        return DEFAULT_EXECUTOR.delete(url, headers);
    }

    public static <V> V delete(String url, Headers headers, Class<V> type) {
        return DEFAULT_EXECUTOR.delete(url, headers, type);
    }

    public static <V> V delete(String url, Headers headers, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.delete(url, headers, typeReference);
    }

    public static String delete(String url, Params params) {
        return DEFAULT_EXECUTOR.delete(url, params);
    }

    public static <V> V delete(String url, Params params, Class<V> type) {
        return DEFAULT_EXECUTOR.delete(url, params, type);
    }

    public static <V> V delete(String url, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.delete(url, params, typeReference);
    }

    public static String delete(String url, Headers headers, Params params) {
        return DEFAULT_EXECUTOR.delete(url, headers, params);
    }

    public static <V> V delete(String url, Headers headers, Params params, Class<V> type) {
        return DEFAULT_EXECUTOR.delete(url, headers, params, type);
    }

    public static <V> V delete(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.delete(url, headers, params, typeReference);
    }

    public static <V> V delete(String url, Headers headers, Params params, Type type) {
        return DEFAULT_EXECUTOR.delete(url, headers, params, type);
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


    public static void download(String url, OutputStream outputStream) {
        DEFAULT_EXECUTOR.download(url, outputStream);
    }

    public static void download(String url, Headers headers, String targetPath) {
        DEFAULT_EXECUTOR.download(url, headers, targetPath);
    }

    public static void download(String url, Headers headers, OutputStream outputStream) {
        DEFAULT_EXECUTOR.download(url, headers, outputStream);
    }

    public static void download(String url, Params params, String targetPath) {
        DEFAULT_EXECUTOR.download(url, params, targetPath);
    }

    public static void download(String url, Params params, OutputStream outputStream) {
        DEFAULT_EXECUTOR.download(url, params, outputStream);
    }

    public static void download(String url, Headers headers, Params params, String targetPath) {
        DEFAULT_EXECUTOR.download(url, headers, params, targetPath);
    }

    public static void download(String url, Headers headers, Params params, OutputStream outputStream) {
        DEFAULT_EXECUTOR.download(url, headers, params, outputStream);
    }

    public static void downloadAsync(String url, String targetPath) {
        DEFAULT_EXECUTOR.downloadAsync(url, targetPath);
    }

    public static void downloadAsync(String url, OutputStream outputStream) {
        DEFAULT_EXECUTOR.downloadAsync(url, outputStream);
    }

    public static void downloadAsync(String url, Headers headers, String targetPath) {
        DEFAULT_EXECUTOR.downloadAsync(url, headers, targetPath);
    }

    public static void downloadAsync(String url, Headers headers, OutputStream outputStream) {
        DEFAULT_EXECUTOR.downloadAsync(url, headers, outputStream);
    }

    public static void downloadAsync(String url, Params params, String targetPath) {
        DEFAULT_EXECUTOR.downloadAsync(url, params, targetPath);
    }

    public static void downloadAsync(String url, Params params, OutputStream outputStream) {
        DEFAULT_EXECUTOR.downloadAsync(url, params, outputStream);
    }

    public static void downloadAsync(String url, Headers headers, Params params, String targetPath) {
        DEFAULT_EXECUTOR.downloadAsync(url, headers, params, targetPath);
    }

    public static void downloadAsync(String url, Headers headers, Params params, OutputStream outputStream) {
        DEFAULT_EXECUTOR.downloadAsync(url, headers, params, outputStream);
    }

    /******** 同步请求总方法 *********/

    public static <V, T> V execute(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams, Class<V> type) {
        return DEFAULT_EXECUTOR.execute(method, url, headers, params, body, fileParams, type);
    }

    public static <V, T> V execute(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams, TypeReference<V> typeReference) {
        return DEFAULT_EXECUTOR.execute(method, url, headers, params, body, fileParams, typeReference);
    }

    public static <V, T> V execute(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams, Type type) {
        return DEFAULT_EXECUTOR.execute(method, url, headers, params, body, fileParams, type);
    }

    /******** 异步请求总方法 *********/

    public static <T> void enqueue(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams, RequestCallback callback) {
        DEFAULT_EXECUTOR.enqueue(method, url, headers, params, body, fileParams, callback);
    }

}
