package com.dxy.library.network.http.executor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.constant.Method;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.interceptor.RetryInterceptor;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import com.dxy.library.network.http.requester.AbstractRequester;
import com.dxy.library.network.http.requester.OkHttpRequester;
import com.dxy.library.network.http.serializer.HttpSerializer;
import okhttp3.Response;
import org.apache.commons.collections4.MapUtils;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Http执行器
 * @author duanxinyuan
 * 2018/8/23 21:27
 */
public class Executor {

    /**
     * Map<requestLogEnable, Map<timeout, Map<retries, Executor>>>
     * Map<请求日志开关, Map<请求超时时间, Map<重试次数, Executor>>>
     * 用于线程安全
     */
    private static final Map<Boolean, Map<Long, Map<Integer, Executor>>> EXECUTOR_MAP = Maps.newConcurrentMap();

    private final AbstractRequester requester;

    public Executor(HttpSerializer httpSerializer, boolean requestLogEnable, long timeoutMillis, int retries, long retryIntervalMillis, boolean enableH2c) {
        requester = new OkHttpRequester(httpSerializer, requestLogEnable, timeoutMillis, retries, retryIntervalMillis, enableH2c);
    }

    public AbstractRequester getRequester() {
        return requester;
    }

    /**
     * 替换序列化实现
     */
    public void httpSerializer(HttpSerializer httpSerializer) {
        this.requester.setHttpSerializer(httpSerializer);
        if (MapUtils.isNotEmpty(EXECUTOR_MAP)) {
            EXECUTOR_MAP.forEach((requestLogEnable, timeoutMap) -> {
                if (MapUtils.isNotEmpty(timeoutMap)) {
                    timeoutMap.forEach((timeout, retriesMap) -> {
                        if (MapUtils.isNotEmpty(retriesMap)) {
                            retriesMap.forEach((retries, executor) -> executor.getRequester().setHttpSerializer(httpSerializer));
                        }
                    });
                }
            });
        }
    }

    /**
     * 设置请求日志开关
     * @param requestLogEnable true表示开启请求日志
     */
    public Executor requestLogEnable(boolean requestLogEnable) {
        return getExecutor(requester.getHttpSerializer(), requestLogEnable, requester.getTimeoutMillis(), requester.getRetries(), requester.isEnableH2c());
    }

    /**
     * 关闭请求日志
     */
    public Executor disableRequestLog() {
        return requestLogEnable(false);
    }

    /**
     * 开启请求日志
     */
    public Executor enableRequestLog() {
        return requestLogEnable(true);
    }

    /**
     * 设置超时时间，单位为秒
     */
    public Executor timeout(int timeout) {
        return getExecutor(requester.getHttpSerializer(), requester.isRequestLogEnable(), TimeUnit.SECONDS.toMillis(timeout), requester.getRetries(), requester.isEnableH2c());
    }

    /**
     * 设置超时时间
     */
    public Executor timeout(long timeout, TimeUnit timeUnit) {
        return getExecutor(requester.getHttpSerializer(), requester.isRequestLogEnable(), timeUnit.toMillis(timeout), requester.getRetries(), requester.isEnableH2c());
    }

    /**
     * 设置超时时间，单位为毫秒
     */
    public Executor timeoutMillis(long timeoutMillis) {
        return getExecutor(requester.getHttpSerializer(), requester.isRequestLogEnable(), timeoutMillis, requester.getRetries(), requester.isEnableH2c());
    }

    /**
     * 设置重试次数，默认不重试
     */
    public Executor retries(int retries) {
        return getExecutor(requester.getHttpSerializer(), requester.isRequestLogEnable(), requester.getTimeoutMillis(), retries, requester.isEnableH2c());
    }

    /**
     * 设置重试次数，默认不重试
     */
    public Executor retries(int retries, long retryIntervalMillis) {
        return getExecutor(requester.getHttpSerializer(), requester.isRequestLogEnable(), requester.getTimeoutMillis(), retries, retryIntervalMillis, requester.isEnableH2c());
    }

    public static Executor getExecutor(HttpSerializer httpSerializer, boolean requestLogEnable, long timeoutMillis, int retries) {
        return getExecutor(httpSerializer, requestLogEnable, timeoutMillis, retries, false);
    }

    public static Executor getExecutor(HttpSerializer httpSerializer, boolean requestLogEnable, long timeoutMillis, int retries, boolean enableH2c) {
        Map<Long, Map<Integer, Executor>> timeoutMap = EXECUTOR_MAP.computeIfAbsent(requestLogEnable, k -> Maps.newConcurrentMap());
        Map<Integer, Executor> retriesMap = timeoutMap.computeIfAbsent(timeoutMillis, body -> Maps.newConcurrentMap());
        return retriesMap.computeIfAbsent(retries, r -> new Executor(httpSerializer, requestLogEnable, timeoutMillis, retries, RetryInterceptor.RETRY_INTERVAL_DEFAULT, enableH2c));
    }

    public static Executor getExecutor(HttpSerializer httpSerializer, boolean requestLogEnable, long timeoutMillis, int retries, long retryIntervalMillis) {
        return getExecutor(httpSerializer, requestLogEnable, timeoutMillis, retries, retryIntervalMillis, false);
    }

    public static Executor getExecutor(HttpSerializer httpSerializer, boolean requestLogEnable, long timeoutMillis, int retries, long retryIntervalMillis, boolean enableH2c) {
        Map<Long, Map<Integer, Executor>> timeoutMap = EXECUTOR_MAP.computeIfAbsent(requestLogEnable, k -> Maps.newConcurrentMap());
        Map<Integer, Executor> retriesMap = timeoutMap.computeIfAbsent(timeoutMillis, body -> Maps.newConcurrentMap());
        return retriesMap.computeIfAbsent(retries, r -> new Executor(httpSerializer, requestLogEnable, timeoutMillis, retries, retryIntervalMillis, enableH2c));
    }

    /******** get *********/

    public String get(String url) {
        return get(url, null, null, String.class);
    }

    public <V> V get(String url, Class<V> type) {
        return get(url, null, null, type);
    }

    public <V> V get(String url, TypeReference<V> typeReference) {
        return get(url, null, null, typeReference);
    }

    public String get(String url, Headers headers) {
        return get(url, headers, null, String.class);
    }

    public <V> V get(String url, Headers headers, Class<V> type) {
        return get(url, headers, null, type);
    }

    public <V> V get(String url, Headers headers, TypeReference<V> typeReference) {
        return get(url, headers, null, typeReference);
    }

    public String get(String url, Params params) {
        return get(url, null, params, String.class);
    }

    public <V> V get(String url, Params params, Class<V> type) {
        return get(url, null, params, type);
    }

    public <V> V get(String url, Params params, TypeReference<V> typeReference) {
        return get(url, null, params, typeReference);
    }

    public String get(String url, Headers headers, Params params) {
        return requester.execute(Method.GET, url, headers, params, null, null, String.class);
    }

    public <V> V get(String url, Headers headers, Params params, Class<V> type) {
        return requester.execute(Method.GET, url, headers, params, null, null, type);
    }

    public <V> V get(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return requester.execute(Method.GET, url, headers, params, null, null, typeReference.getType());
    }

    public <V> V get(String url, Headers headers, Params params, Type type) {
        return requester.execute(Method.GET, url, headers, params, null, null, type);
    }

    public Response getForNative(String url) {
        return requester.execute(Method.GET, url, null, null, null, null);
    }

    public Response getForNative(String url, Headers headers) {
        return requester.execute(Method.GET, url, headers, null, null, null);
    }

    public Response getForNative(String url, Params params) {
        return requester.execute(Method.GET, url, null, params, null, null);
    }

    public Response getForNative(String url, Headers headers, Params params) {
        return requester.execute(Method.GET, url, headers, params, null, null);
    }

    public void getAsync(String url) {
        getAsync(url, null, null, null);
    }

    public void getAsync(String url, RequestCallback callback) {
        getAsync(url, null, null, callback);
    }

    public void getAsync(String url, Params params) {
        getAsync(url, null, params, null);
    }

    public void getAsync(String url, Params params, RequestCallback callback) {
        getAsync(url, null, params, callback);
    }

    public void getAsync(String url, Headers headers) {
        getAsync(url, headers, null, null);
    }

    public void getAsync(String url, Headers headers, RequestCallback callback) {
        getAsync(url, headers, null, callback);
    }

    public void getAsync(String url, Headers headers, Params params) {
        requester.enqueue(Method.GET, url, headers, params, null, null, null);
    }

    public void getAsync(String url, Headers headers, Params params, RequestCallback callback) {
        requester.enqueue(Method.GET, url, headers, params, null, null, callback);
    }


    /******** post *********/

    public String post(String url) {
        return post(url, null, null, String.class);
    }

    public String post(String url, Headers headers) {
        return post(url, headers, null, String.class);
    }

    public <T> String post(String url, Headers headers, T body) {
        return post(url, headers, null, body, String.class);
    }

    public <V> V post(String url, Headers headers, Class<V> type) {
        return post(url, headers, null, type);
    }

    public <V> V post(String url, Headers headers, TypeReference<V> typeReference) {
        return post(url, headers, null, typeReference);
    }

    public <V, T> V post(String url, Headers headers, T body, Class<V> type) {
        return post(url, headers, null, body, type);
    }

    public <V, T> V post(String url, Headers headers, T body, TypeReference<V> typeReference) {
        return post(url, headers, null, body, typeReference);
    }

    public String post(String url, Params params) {
        return post(url, null, params, String.class);
    }

    public <V> V post(String url, Params params, Class<V> type) {
        return post(url, null, params, type);
    }

    public <V> V post(String url, Params params, TypeReference<V> typeReference) {
        return post(url, null, params, typeReference);
    }

    public String post(String url, Headers headers, Params params) {
        return requester.execute(Method.POST, url, headers, params, null, null, String.class);
    }

    public <V> V post(String url, Headers headers, Params params, Class<V> type) {
        return requester.execute(Method.POST, url, headers, params, null, null, type);
    }

    public <V> V post(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return requester.execute(Method.POST, url, headers, params, null, null, typeReference.getType());
    }

    public <T> String post(String url, Headers headers, Params params, T body) {
        return requester.execute(Method.POST, url, headers, params, body, null, String.class);
    }

    public <V, T> V post(String url, Headers headers, Params params, T body, Class<V> type) {
        return requester.execute(Method.POST, url, headers, params, body, null, type);
    }

    public <V, T> V post(String url, Headers headers, Params params, T body, TypeReference<V> typeReference) {
        return requester.execute(Method.POST, url, headers, params, body, null, typeReference.getType());
    }

    public <V, T> V post(String url, Headers headers, Params params, T body, Type type) {
        return requester.execute(Method.POST, url, headers, params, body, null, type);
    }

    public Response postForNative(String url) {
        return requester.execute(Method.POST, url, null, null, null, null);
    }

    public Response postForNative(String url, Headers headers) {
        return requester.execute(Method.POST, url, headers, null, null, null);
    }

    public Response postForNative(String url, Params params) {
        return requester.execute(Method.POST, url, null, params, null, null);
    }

    public Response postForNative(String url, Headers headers, Params params) {
        return requester.execute(Method.POST, url, headers, params, null, null);
    }

    public <T> Response postForNative(String url, Headers headers, Params params, T body) {
        return requester.execute(Method.POST, url, headers, params, body, null);
    }

    public String postFile(String url, FileParam fileParam) {
        return postFile(url, null, null, fileParam, String.class);
    }

    public <V> V postFile(String url, FileParam fileParam, Class<V> type) {
        return postFile(url, null, null, fileParam, type);
    }

    public <V> V postFile(String url, FileParam fileParam, TypeReference<V> typeReference) {
        return postFile(url, null, null, fileParam, typeReference);
    }

    public String postFile(String url, Params params, FileParam fileParam) {
        return postFile(url, null, params, fileParam, String.class);
    }

    public <V> V postFile(String url, Params params, FileParam fileParam, Class<V> type) {
        return postFile(url, null, params, fileParam, type);
    }

    public <V> V postFile(String url, Params params, FileParam fileParam, TypeReference<V> typeReference) {
        return postFile(url, null, params, fileParam, typeReference);
    }

    public String postFile(String url, Headers headers, Params params, FileParam fileParam) {
        return requester.execute(Method.POST, url, headers, params, null, Lists.newArrayList(fileParam), String.class);
    }

    public <V> V postFile(String url, Headers headers, Params params, FileParam fileParam, Class<V> type) {
        return requester.execute(Method.POST, url, headers, params, null, Lists.newArrayList(fileParam), type);
    }

    public <V> V postFile(String url, Headers headers, Params params, FileParam fileParam, TypeReference<V> typeReference) {
        return requester.execute(Method.POST, url, headers, params, null, Lists.newArrayList(fileParam), typeReference.getType());
    }

    public <V> V postFile(String url, Headers headers, Params params, FileParam fileParam, Type type) {
        return requester.execute(Method.POST, url, headers, params, null, Lists.newArrayList(fileParam), type);
    }

    public String postFile(String url, Params params, List<FileParam> fileParams) {
        return postFile(url, null, params, fileParams, String.class);
    }

    public <V> V postFile(String url, Params params, List<FileParam> fileParams, Class<V> type) {
        return postFile(url, null, params, fileParams, type);
    }

    public <V> V postFile(String url, Params params, List<FileParam> fileParams, TypeReference<V> typeReference) {
        return postFile(url, null, params, fileParams, typeReference);
    }

    public String postFile(String url, Headers headers, Params params, List<FileParam> fileParams) {
        return requester.execute(Method.POST, url, headers, params, null, fileParams, String.class);
    }

    public <V> V postFile(String url, Headers headers, Params params, List<FileParam> fileParams, Class<V> type) {
        return requester.execute(Method.POST, url, headers, params, null, fileParams, type);
    }

    public <V> V postFile(String url, Headers headers, Params params, List<FileParam> fileParams, TypeReference<V> typeReference) {
        return requester.execute(Method.POST, url, headers, params, null, fileParams, typeReference.getType());
    }

    public <V> V postFile(String url, Headers headers, Params params, List<FileParam> fileParams, Type type) {
        return requester.execute(Method.POST, url, headers, params, null, fileParams, type);
    }

    public Response postFileForNative(String url, FileParam fileParam) {
        return requester.execute(Method.POST, url, null, null, null, Lists.newArrayList(fileParam));
    }

    public Response postFileForNative(String url, Params params, FileParam fileParam) {
        return requester.execute(Method.POST, url, null, params, null, Lists.newArrayList(fileParam));
    }

    public Response postFileForNative(String url, Headers headers, Params params, FileParam fileParam) {
        return requester.execute(Method.POST, url, headers, params, null, Lists.newArrayList(fileParam));
    }

    public Response postFileForNative(String url, Headers headers, Params params, List<FileParam> fileParams) {
        return requester.execute(Method.POST, url, headers, params, null, fileParams);
    }

    public <T> String postJson(String url, T body) {
        return postJson(url, null, null, body, String.class);
    }

    public <V, T> V postJson(String url, T body, Class<V> type) {
        return postJson(url, null, null, body, type);
    }

    public <V, T> V postJson(String url, T body, TypeReference<V> typeReference) {
        return postJson(url, null, null, body, typeReference);
    }

    public <T> String postJson(String url, Headers headers, T body) {
        return postJson(url, headers, null, body, String.class);
    }

    public <V, T> V postJson(String url, Headers headers, T body, Class<V> type) {
        return postJson(url, headers, null, body, type);
    }

    public <V, T> V postJson(String url, Headers headers, T body, TypeReference<V> typeReference) {
        return postJson(url, headers, null, body, typeReference);
    }

    public <T> String postJson(String url, Params params, T body) {
        return postJson(url, null, params, body, String.class);
    }

    public <V, T> V postJson(String url, Params params, T body, Class<V> type) {
        return postJson(url, null, params, body, type);
    }

    public <V, T> V postJson(String url, Params params, T body, TypeReference<V> typeReference) {
        return postJson(url, null, params, body, typeReference);
    }

    public <V, T> V postJson(String url, Headers headers, Params params, T body, Class<V> type) {
        return requester.execute(Method.POST, url, headers, params, body, null, type);
    }

    public <T> String postJson(String url, Headers headers, Params params, T body) {
        return requester.execute(Method.POST, url, headers, params, body, null, String.class);
    }

    public <V, T> V postJson(String url, Headers headers, Params params, T body, TypeReference<V> typeReference) {
        return requester.execute(Method.POST, url, headers, params, body, null, typeReference.getType());
    }

    public <V, T> V postJson(String url, Headers headers, Params params, T body, Type type) {
        return requester.execute(Method.POST, url, headers, params, body, null, type);
    }

    public <T> Response postJsonForNative(String url, T body) {
        return requester.execute(Method.POST, url, null, null, body, null);
    }

    public <T> Response postJsonForNative(String url, Headers headers, T body) {
        return requester.execute(Method.POST, url, headers, null, body, null);
    }

    public <T> Response postJsonForNative(String url, Params params, T body) {
        return requester.execute(Method.POST, url, null, params, body, null);
    }

    public <T> Response postJsonForNative(String url, Headers headers, Params params, T body) {
        return requester.execute(Method.POST, url, headers, params, body, null);
    }

    public void postAsync(String url) {
        postAsync(url, null, null, null);
    }

    public void postAsync(String url, RequestCallback callback) {
        postAsync(url, null, null, callback);
    }

    public void postAsync(String url, Headers headers) {
        postAsync(url, headers, null, null);
    }

    public <T> void postAsync(String url, Headers headers, T body) {
        postAsync(url, headers, null, body, null);
    }

    public void postAsync(String url, Headers headers, RequestCallback callback) {
        postAsync(url, headers, null, callback);
    }

    public <T> void postAsync(String url, Headers headers, T body, RequestCallback callback) {
        postAsync(url, headers, null, body, callback);
    }

    public void postAsync(String url, Params params) {
        postAsync(url, null, params, null);
    }

    public void postAsync(String url, Params params, RequestCallback callback) {
        postAsync(url, null, params, callback);
    }

    public void postAsync(String url, Headers headers, Params params) {
        requester.enqueue(Method.POST, url, headers, params, null, null, null);
    }

    public void postAsync(String url, Headers headers, Params params, RequestCallback callback) {
        requester.enqueue(Method.POST, url, headers, params, null, null, callback);
    }

    public <T> void postAsync(String url, Headers headers, Params params, T body) {
        requester.enqueue(Method.POST, url, headers, params, body, null, null);
    }

    public <T> void postAsync(String url, Headers headers, Params params, T body, RequestCallback callback) {
        requester.enqueue(Method.POST, url, headers, params, body, null, callback);
    }

    public void postFileAsync(String url, FileParam fileParam) {
        postFileAsync(url, null, null, fileParam, null);
    }

    public void postFileAsync(String url, FileParam fileParam, RequestCallback callback) {
        postFileAsync(url, null, null, fileParam, callback);
    }

    public void postFileAsync(String url, Params params, FileParam fileParam) {
        postFileAsync(url, null, params, fileParam, null);
    }

    public void postFileAsync(String url, Params params, FileParam fileParam, RequestCallback callback) {
        postFileAsync(url, null, params, fileParam, callback);
    }

    public void postFileAsync(String url, Params params, List<FileParam> fileParams) {
        postFileAsync(url, null, params, fileParams, null);
    }

    public void postFileAsync(String url, Params params, List<FileParam> fileParams, RequestCallback callback) {
        postFileAsync(url, null, params, fileParams, callback);
    }

    public void postFileAsync(String url, Headers headers, Params params, FileParam fileParam, RequestCallback callback) {
        requester.enqueue(Method.POST, url, headers, params, null, Lists.newArrayList(fileParam), callback);
    }

    public void postFileAsync(String url, Headers headers, Params params, List<FileParam> fileParams) {
        requester.enqueue(Method.POST, url, headers, params, null, fileParams, null);
    }

    public void postFileAsync(String url, Headers headers, Params params, List<FileParam> fileParams, RequestCallback callback) {
        requester.enqueue(Method.POST, url, headers, params, null, fileParams, callback);
    }

    public <T> void postJsonAsync(String url, T body) {
        postJsonAsync(url, null, null, body, null);
    }

    public <T> void postJsonAsync(String url, T body, RequestCallback callback) {
        postJsonAsync(url, null, null, body, callback);
    }

    public <T> void postJsonAsync(String url, Headers headers, T body) {
        postJsonAsync(url, headers, null, body, null);
    }

    public <T> void postJsonAsync(String url, Headers headers, T body, RequestCallback callback) {
        postJsonAsync(url, headers, null, body, callback);
    }

    public <T> void postJsonAsync(String url, Params params, T body) {
        postJsonAsync(url, null, params, body, null);
    }

    public <T> void postJsonAsync(String url, Params params, T body, RequestCallback callback) {
        postJsonAsync(url, null, params, body, callback);
    }

    public <T> void postJsonAsync(String url, Headers headers, Params params, T body) {
        postJsonAsync(url, headers, params, body, null);
    }

    public <T> void postJsonAsync(String url, Headers headers, Params params, T body, RequestCallback callback) {
        requester.enqueue(Method.POST, url, headers, params, body, null, callback);
    }


    /******** put *********/

    public String put(String url) {
        return put(url, null, null, String.class);
    }

    public String put(String url, Headers headers) {
        return put(url, headers, null, String.class);
    }

    public <T> String put(String url, Headers headers, T body) {
        return put(url, headers, null, body, String.class);
    }

    public <V> V put(String url, Headers headers, Class<V> type) {
        return put(url, headers, null, type);
    }

    public <V> V put(String url, Headers headers, TypeReference<V> typeReference) {
        return put(url, headers, null, typeReference);
    }

    public <V, T> V put(String url, Headers headers, T body, Class<V> type) {
        return put(url, headers, null, body, type);
    }

    public <V, T> V put(String url, Headers headers, T body, TypeReference<V> typeReference) {
        return put(url, headers, null, body, typeReference);
    }

    public String put(String url, Params params) {
        return put(url, null, params, String.class);
    }

    public <V> V put(String url, Params params, Class<V> type) {
        return put(url, null, params, type);
    }

    public <V> V put(String url, Params params, TypeReference<V> typeReference) {
        return put(url, null, params, typeReference);
    }

    public String put(String url, Headers headers, Params params) {
        return put(url, headers, params, String.class);
    }

    public <V> V put(String url, Headers headers, Params params, Class<V> type) {
        return requester.execute(Method.PUT, url, headers, params, null, null, type);
    }

    public <V> V put(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return requester.execute(Method.PUT, url, headers, params, null, null, typeReference.getType());
    }

    public <T> String put(String url, Headers headers, Params params, T body) {
        return put(url, headers, params, body, String.class);
    }

    public <V, T> V put(String url, Headers headers, Params params, T body, Class<V> type) {
        return requester.execute(Method.PUT, url, headers, params, body, null, type);
    }

    public <V, T> V put(String url, Headers headers, Params params, T body, TypeReference<V> typeReference) {
        return requester.execute(Method.PUT, url, headers, params, body, null, typeReference.getType());
    }

    public <V, T> V put(String url, Headers headers, Params params, T body, Type type) {
        return requester.execute(Method.PUT, url, headers, params, body, null, type);
    }

    public Response putForNative(String url) {
        return requester.execute(Method.PUT, url, null, null, null, null);
    }

    public Response putForNative(String url, Headers headers) {
        return requester.execute(Method.PUT, url, headers, null, null, null);
    }

    public Response putForNative(String url, Params params) {
        return requester.execute(Method.PUT, url, null, params, null, null);
    }

    public Response putForNative(String url, Headers headers, Params params) {
        return requester.execute(Method.PUT, url, headers, params, null, null);
    }

    public <T> Response putForNative(String url, Headers headers, Params params, T body) {
        return requester.execute(Method.PUT, url, headers, params, body, null);
    }

    public <T> String putJson(String url, T body) {
        return putJson(url, null, null, body, String.class);
    }

    public <V, T> V putJson(String url, T body, Class<V> type) {
        return putJson(url, null, null, body, type);
    }

    public <V, T> V putJson(String url, T body, TypeReference<V> typeReference) {
        return putJson(url, null, null, body, typeReference);
    }

    public <T> String putJson(String url, Headers headers, T body) {
        return putJson(url, headers, null, body, String.class);
    }

    public <V, T> V putJson(String url, Headers headers, T body, Class<V> type) {
        return putJson(url, headers, null, body, type);
    }

    public <V, T> V putJson(String url, Headers headers, T body, TypeReference<V> typeReference) {
        return putJson(url, headers, null, body, typeReference);
    }

    public <T> String putJson(String url, Params params, T body) {
        return putJson(url, null, params, body, String.class);
    }

    public <V, T> V putJson(String url, Params params, T body, Class<V> type) {
        return putJson(url, null, params, body, type);
    }

    public <V, T> V putJson(String url, Params params, T body, TypeReference<V> typeReference) {
        return putJson(url, null, params, body, typeReference);
    }

    public <T> String putJson(String url, Headers headers, Params params, T body) {
        return requester.execute(Method.PUT, url, headers, params, body, null, String.class);
    }

    public <V, T> V putJson(String url, Headers headers, Params params, T body, Class<V> type) {
        return requester.execute(Method.PUT, url, headers, params, body, null, type);
    }

    public <V, T> V putJson(String url, Headers headers, Params params, T body, TypeReference<V> typeReference) {
        return requester.execute(Method.PUT, url, headers, params, body, null, typeReference.getType());
    }

    public <V, T> V putJson(String url, Headers headers, Params params, T body, Type type) {
        return requester.execute(Method.PUT, url, headers, params, body, null, type);
    }

    public <T> Response putJsonForNative(String url, T body) {
        return requester.execute(Method.PUT, url, null, null, body, null);
    }

    public <T> Response putJsonForNative(String url, Headers headers, T body) {
        return requester.execute(Method.PUT, url, headers, null, body, null);
    }

    public <T> Response putJsonForNative(String url, Params params, T body) {
        return requester.execute(Method.PUT, url, null, params, body, null);
    }

    public <T> Response putJsonForNative(String url, Headers headers, Params params, T body) {
        return requester.execute(Method.PUT, url, headers, params, body, null);
    }

    public void putAsync(String url) {
        putAsync(url, null, null, null);
    }

    public void putAsync(String url, Headers headers) {
        putAsync(url, headers, null, null);
    }

    public <T> void putAsync(String url, Headers headers, T body) {
        putAsync(url, headers, null, body, null);
    }

    public void putAsync(String url, Headers headers, RequestCallback callback) {
        putAsync(url, headers, null, callback);
    }

    public void putAsync(String url, Params params) {
        putAsync(url, null, params, null);
    }

    public void putAsync(String url, Params params, RequestCallback callback) {
        putAsync(url, null, params, callback);
    }

    public void putAsync(String url, Headers headers, Params params, RequestCallback callback) {
        requester.enqueue(Method.PUT, url, headers, params, null, null, callback);
    }

    public <T> void putAsync(String url, Headers headers, Params params, T body, RequestCallback callback) {
        requester.enqueue(Method.PUT, url, headers, params, body, null, callback);
    }

    public <T> void putJsonAsync(String url, T body) {
        putJsonAsync(url, null, body, null);
    }

    public <T> void putJsonAsync(String url, T body, RequestCallback callback) {
        putJsonAsync(url, null, body, callback);
    }

    public <T> void putJsonAsync(String url, Headers headers, T body) {
        putJsonAsync(url, headers, body, null);
    }

    public <T> void putJsonAsync(String url, Headers headers, T body, RequestCallback callback) {
        putJsonAsync(url, headers, null, body, callback);
    }

    public <T> void putJsonAsync(String url, Headers headers, Params params, T body) {
        putJsonAsync(url, headers, params, body, null);
    }

    public <T> void putJsonAsync(String url, Headers headers, Params params, T body, RequestCallback callback) {
        requester.enqueue(Method.PUT, url, headers, params, body, null, callback);
    }


    /******** patch *********/

    public String patch(String url) {
        return patch(url, null, null, String.class);
    }

    public <V> V patch(String url, Class<V> type) {
        return patch(url, null, null, type);
    }

    public <V> V patch(String url, TypeReference<V> typeReference) {
        return patch(url, null, null, typeReference);
    }

    public String patch(String url, Headers headers) {
        return patch(url, headers, null, String.class);
    }

    public <V> V patch(String url, Headers headers, Class<V> type) {
        return patch(url, headers, null, type);
    }

    public <V> V patch(String url, Headers headers, TypeReference<V> typeReference) {
        return patch(url, headers, null, typeReference);
    }

    public <T> String patch(String url, Headers headers, T body) {
        return patch(url, headers, null, body, String.class);
    }

    public <V, T> V patch(String url, Headers headers, T body, Class<V> type) {
        return patch(url, headers, null, body, type);
    }

    public <V, T> V patch(String url, Headers headers, T body, TypeReference<V> typeReference) {
        return patch(url, headers, null, body, typeReference);
    }

    public String patch(String url, Params params) {
        return patch(url, null, params, String.class);
    }

    public <V> V patch(String url, Params params, Class<V> type) {
        return patch(url, null, params, type);
    }

    public <V> V patch(String url, Params params, TypeReference<V> typeReference) {
        return patch(url, null, params, typeReference);
    }

    public String patch(String url, Headers headers, Params params) {
        return requester.execute(Method.PATCH, url, headers, params, null, null, String.class);
    }

    public <V> V patch(String url, Headers headers, Params params, Class<V> type) {
        return requester.execute(Method.PATCH, url, headers, params, null, null, type);
    }

    public <V> V patch(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return requester.execute(Method.PATCH, url, headers, params, null, null, typeReference.getType());
    }

    public <T> String patch(String url, Headers headers, Params params, T body) {
        return requester.execute(Method.PATCH, url, headers, params, body, null, String.class);
    }

    public <V, T> V patch(String url, Headers headers, Params params, T body, Class<V> type) {
        return requester.execute(Method.PATCH, url, headers, params, body, null, type);
    }

    public <V, T> V patch(String url, Headers headers, Params params, T body, TypeReference<V> typeReference) {
        return requester.execute(Method.PATCH, url, headers, params, body, null, typeReference.getType());
    }

    public <V, T> V patch(String url, Headers headers, Params params, T body, Type type) {
        return requester.execute(Method.PATCH, url, headers, params, body, null, type);
    }

    public Response patchForNative(String url) {
        return requester.execute(Method.PATCH, url, null, null, null, null);
    }

    public Response patchForNative(String url, Headers headers) {
        return requester.execute(Method.PATCH, url, headers, null, null, null);
    }

    public Response patchForNative(String url, Params params) {
        return requester.execute(Method.PATCH, url, null, params, null, null);
    }

    public Response patchForNative(String url, Headers headers, Params params) {
        return requester.execute(Method.PATCH, url, headers, params, null, null);
    }

    public <T> Response patchForNative(String url, Headers headers, Params params, T body) {
        return requester.execute(Method.PATCH, url, headers, params, body, null);
    }

    public void patchAsync(String url) {
        patchAsync(url, null, null, null);
    }

    public void patchAsync(String url, RequestCallback callback) {
        patchAsync(url, null, null, callback);
    }

    public void patchAsync(String url, Headers headers) {
        patchAsync(url, headers, null, null);
    }

    public void patchAsync(String url, Headers headers, RequestCallback callback) {
        patchAsync(url, headers, null, callback);
    }

    public <T> void patchAsync(String url, Headers headers, T body) {
        patchAsync(url, headers, null, body, null);
    }

    public <T> void patchAsync(String url, Headers headers, T body, RequestCallback callback) {
        patchAsync(url, headers, null, body, callback);
    }

    public void patchAsync(String url, Params params) {
        patchAsync(url, null, params, null);
    }

    public void patchAsync(String url, Params params, RequestCallback callback) {
        patchAsync(url, null, params, callback);
    }

    public void patchAsync(String url, Headers headers, Params params) {
        patchAsync(url, headers, params, null);
    }

    public void patchAsync(String url, Headers headers, Params params, RequestCallback callback) {
        requester.enqueue(Method.PATCH, url, headers, params, null, null, callback);
    }

    public <T> void patchAsync(String url, Headers headers, Params params, T body) {
        patchAsync(url, headers, params, body, null);
    }

    public <T> void patchAsync(String url, Headers headers, Params params, T body, RequestCallback callback) {
        requester.enqueue(Method.PATCH, url, headers, params, body, null, callback);
    }

    /******** delete *********/

    public String delete(String url) {
        return delete(url, null, null, String.class);
    }

    public <V> V delete(String url, Class<V> type) {
        return delete(url, null, null, type);
    }

    public <V> V delete(String url, TypeReference<V> typeReference) {
        return delete(url, null, null, typeReference);
    }

    public String delete(String url, Headers headers) {
        return delete(url, headers, null, String.class);
    }

    public <V> V delete(String url, Headers headers, Class<V> type) {
        return delete(url, headers, null, type);
    }

    public <V> V delete(String url, Headers headers, TypeReference<V> typeReference) {
        return delete(url, headers, null, typeReference);
    }

    public String delete(String url, Params params) {
        return delete(url, null, params, String.class);
    }

    public <V> V delete(String url, Params params, Class<V> type) {
        return delete(url, null, params, type);
    }

    public <V> V delete(String url, Params params, TypeReference<V> typeReference) {
        return delete(url, null, params, typeReference);
    }

    public String delete(String url, Headers headers, Params params) {
        return requester.execute(Method.DELETE, url, headers, params, null, null, String.class);
    }

    public <V> V delete(String url, Headers headers, Params params, Class<V> type) {
        return requester.execute(Method.DELETE, url, headers, params, null, null, type);
    }

    public <V> V delete(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return requester.execute(Method.DELETE, url, headers, params, null, null, typeReference.getType());
    }

    public <V> V delete(String url, Headers headers, Params params, Type type) {
        return requester.execute(Method.DELETE, url, headers, params, null, null, type);
    }

    public Response deleteForNative(String url) {
        return requester.execute(Method.DELETE, url, null, null, null, null);
    }

    public Response deleteForNative(String url, Headers headers) {
        return requester.execute(Method.DELETE, url, headers, null, null, null);
    }

    public Response deleteForNative(String url, Params params) {
        return requester.execute(Method.DELETE, url, null, params, null, null);
    }

    public Response deleteForNative(String url, Headers headers, Params params) {
        return requester.execute(Method.DELETE, url, headers, params, null, null);
    }

    public void deleteAsync(String url) {
        deleteAsync(url, null, null, null);
    }

    public void deleteAsync(String url, RequestCallback callback) {
        deleteAsync(url, null, null, callback);
    }

    public void deleteAsync(String url, Headers headers) {
        deleteAsync(url, headers, null);
    }

    public void deleteAsync(String url, Headers headers, RequestCallback callback) {
        deleteAsync(url, headers, null, callback);
    }

    public void deleteAsync(String url, Params params) {
        deleteAsync(url, params, null);
    }

    public void deleteAsync(String url, Params params, RequestCallback callback) {
        deleteAsync(url, null, params, callback);
    }

    public void deleteAsync(String url, Headers headers, Params params, RequestCallback callback) {
        requester.enqueue(Method.DELETE, url, headers, params, null, null, callback);
    }

    /******** download *********/

    public void download(String url, String targetPath) {
        requester.download(url, null, null, targetPath, false);
    }

    public void download(String url, OutputStream outputStream) {
        requester.download(url, null, null, outputStream, false);
    }

    public void download(String url, Headers headers, String targetPath) {
        requester.download(url, headers, null, targetPath, false);
    }

    public void download(String url, Headers headers, OutputStream outputStream) {
        requester.download(url, headers, null, outputStream, false);
    }

    public void download(String url, Params params, String targetPath) {
        requester.download(url, null, params, targetPath, false);
    }

    public void download(String url, Params params, OutputStream outputStream) {
        requester.download(url, null, params, outputStream, false);
    }

    public void download(String url, Headers headers, Params params, String targetPath) {
        requester.download(url, headers, params, targetPath, false);
    }

    public void download(String url, Headers headers, Params params, OutputStream outputStream) {
        requester.download(url, headers, params, outputStream, false);
    }

    public void downloadAsync(String url, String targetPath) {
        requester.download(url, null, null, targetPath, true);
    }

    public void downloadAsync(String url, OutputStream outputStream) {
        requester.download(url, null, null, outputStream, true);
    }

    public void downloadAsync(String url, Headers headers, String targetPath) {
        requester.download(url, headers, null, targetPath, true);
    }

    public void downloadAsync(String url, Headers headers, OutputStream outputStream) {
        requester.download(url, headers, null, outputStream, true);
    }

    public void downloadAsync(String url, Params params, String targetPath) {
        requester.download(url, null, params, targetPath, true);
    }

    public void downloadAsync(String url, Params params, OutputStream outputStream) {
        requester.download(url, null, params, outputStream, true);
    }

    public void downloadAsync(String url, Headers headers, Params params, String targetPath) {
        requester.download(url, headers, params, targetPath, true);
    }

    public void downloadAsync(String url, Headers headers, Params params, OutputStream outputStream) {
        requester.download(url, headers, params, outputStream, true);
    }

    /******** 同步请求总方法 *********/

    public <T> Response execute(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams) {
        return requester.execute(method, url, headers, params, body, fileParams);
    }

    public <V, T> V execute(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams, Class<V> type) {
        return requester.execute(method, url, headers, params, body, fileParams, type);
    }

    public <V, T> V execute(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams, TypeReference<V> typeReference) {
        return requester.execute(method, url, headers, params, body, fileParams, typeReference.getType());
    }

    public <V, T> V execute(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams, Type type) {
        return requester.execute(method, url, headers, params, body, fileParams, type);
    }

    /******** 异步请求总方法 *********/

    public <T> void enqueue(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams, RequestCallback callback) {
        requester.enqueue(method, url, headers, params, body, fileParams, callback);
    }

    /******** Response 反序列化 *********/

    public <V> V serialize(Response response, Class<V> type) {
        return requester.serialize(response, type);
    }

    public <V> V serialize(Response response, TypeReference<V> typeReference) {
        return requester.serialize(response, typeReference.getType());
    }

    public <V> V serialize(Response response, Type type) {
        return requester.serialize(response, type);
    }

}
