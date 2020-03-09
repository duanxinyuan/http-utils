package com.dxy.library.network.http.executor;

import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.interceptor.RetryInterceptor;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import com.dxy.library.network.http.requester.BaseRequester;
import com.dxy.library.network.http.requester.OkHttpRequester;
import com.dxy.library.network.http.serializer.HttpSerializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.constant.Method;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

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
    private static final Map<Boolean, Map<Integer, Map<Integer, Executor>>> EXECUTOR_MAP = Maps.newConcurrentMap();

    private final BaseRequester requester;

    public Executor(HttpSerializer httpSerializer, boolean requestLogEnable, int timeout, int retries, long retryIntervalMillis) {
        requester = new OkHttpRequester(httpSerializer, requestLogEnable, timeout, retries, retryIntervalMillis);
    }

    public BaseRequester getRequester() {
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
        return getExecutor(requester.getHttpSerializer(), requestLogEnable, requester.getTimeout(), requester.getRetries());
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
        return getExecutor(requester.getHttpSerializer(), requester.isRequestLogEnable(), timeout, requester.getRetries());
    }

    /**
     * 设置重试次数，默认不重试
     */
    public Executor retries(int retries) {
        return getExecutor(requester.getHttpSerializer(), requester.isRequestLogEnable(), requester.getTimeout(), retries);
    }

    /**
     * 设置重试次数，默认不重试
     */
    public Executor retries(int retries, long retryIntervalMillis) {
        return getExecutor(requester.getHttpSerializer(), requester.isRequestLogEnable(), requester.getTimeout(), retries, retryIntervalMillis);
    }

    public static Executor getExecutor(HttpSerializer httpSerializer, boolean requestLogEnable, int timeout, int retries) {
        Map<Integer, Map<Integer, Executor>> timeoutMap = EXECUTOR_MAP.computeIfAbsent(requestLogEnable, k -> Maps.newConcurrentMap());
        Map<Integer, Executor> retriesMap = timeoutMap.computeIfAbsent(timeout, t -> Maps.newConcurrentMap());
        return retriesMap.computeIfAbsent(retries, r -> new Executor(httpSerializer, requestLogEnable, timeout, retries, RetryInterceptor.RETRY_INTERVAL_DEFAULT));
    }

    public static Executor getExecutor(HttpSerializer httpSerializer, boolean requestLogEnable, int timeout, int retries, long retryIntervalMillis) {
        Map<Integer, Map<Integer, Executor>> timeoutMap = EXECUTOR_MAP.computeIfAbsent(requestLogEnable, k -> Maps.newConcurrentMap());
        Map<Integer, Executor> retriesMap = timeoutMap.computeIfAbsent(timeout, t -> Maps.newConcurrentMap());
        return retriesMap.computeIfAbsent(retries, r -> new Executor(httpSerializer, requestLogEnable, timeout, retries, retryIntervalMillis));
    }

    /******** get *********/

    public String get(String url) {
        return get(url, null, null, String.class);
    }

    public <V> V get(String url, Class<V> c) {
        return get(url, null, null, c);
    }

    public <V> V get(String url, TypeReference<V> typeReference) {
        return get(url, null, null, typeReference);
    }

    public String get(String url, Headers headers) {
        return get(url, headers, null, String.class);
    }

    public <V> V get(String url, Headers headers, Class<V> c) {
        return get(url, headers, null, c);
    }

    public <V> V get(String url, Headers headers, TypeReference<V> typeReference) {
        return get(url, headers, null, typeReference);
    }

    public String get(String url, Params params) {
        return get(url, null, params, String.class);
    }

    public <V> V get(String url, Params params, Class<V> c) {
        return get(url, null, params, c);
    }

    public <V> V get(String url, Params params, TypeReference<V> typeReference) {
        return get(url, null, params, typeReference);
    }

    public String get(String url, Headers headers, Params params) {
        return requester.execute(Method.GET, url, headers, params, String.class);
    }

    public <V> V get(String url, Headers headers, Params params, Class<V> c) {
        return requester.execute(Method.GET, url, headers, params, c);
    }

    public <V> V get(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return requester.execute(Method.GET, url, headers, params, typeReference.getType());
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
        requester.enqueue(Method.GET, url, headers, params, null);
    }

    public void getAsync(String url, Headers headers, Params params, RequestCallback callback) {
        requester.enqueue(Method.GET, url, headers, params, callback);
    }


    /******** post *********/

    public String post(String url) {
        return post(url, null, null, String.class);
    }

    public String post(String url, Headers headers) {
        return post(url, headers, null, String.class);
    }

    public <T> String post(String url, Headers headers, T t) {
        return post(url, headers, null, t, String.class);
    }

    public <V> V post(String url, Headers headers, Class<V> c) {
        return post(url, headers, null, c);
    }

    public <V> V post(String url, Headers headers, TypeReference<V> typeReference) {
        return post(url, headers, null, typeReference);
    }

    public <V, T> V post(String url, Headers headers, T t, Class<V> c) {
        return post(url, headers, null, t, c);
    }

    public <V, T> V post(String url, Headers headers, T t, TypeReference<V> typeReference) {
        return post(url, headers, null, t, typeReference);
    }

    public String post(String url, Params params) {
        return post(url, null, params, String.class);
    }

    public <V> V post(String url, Params params, Class<V> c) {
        return post(url, null, params, c);
    }

    public <V> V post(String url, Params params, TypeReference<V> typeReference) {
        return post(url, null, params, typeReference);
    }

    public String post(String url, Headers headers, Params params) {
        return requester.execute(Method.POST, url, headers, params, String.class);
    }

    public <V> V post(String url, Headers headers, Params params, Class<V> c) {
        return requester.execute(Method.POST, url, headers, params, c);
    }

    public <V> V post(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return requester.execute(Method.POST, url, headers, params, typeReference.getType());
    }

    public <T> String post(String url, Headers headers, Params params, T t) {
        return requester.execute(Method.POST, url, headers, params, t, String.class);
    }

    public <V, T> V post(String url, Headers headers, Params params, T t, Class<V> c) {
        return requester.execute(Method.POST, url, headers, params, t, c);
    }

    public <V, T> V post(String url, Headers headers, Params params, T t, TypeReference<V> typeReference) {
        return requester.execute(Method.POST, url, headers, params, t, typeReference.getType());
    }

    public String postFile(String url, FileParam fileParam) {
        return postFile(url, null, null, fileParam, String.class);
    }

    public <V> V postFile(String url, FileParam fileParam, Class<V> c) {
        return postFile(url, null, null, fileParam, c);
    }

    public <V> V postFile(String url, FileParam fileParam, TypeReference<V> typeReference) {
        return postFile(url, null, null, fileParam, typeReference);
    }

    public String postFile(String url, Params params, FileParam fileParam) {
        return postFile(url, null, params, fileParam, String.class);
    }

    public <V> V postFile(String url, Params params, FileParam fileParam, Class<V> c) {
        return postFile(url, null, params, fileParam, c);
    }

    public <V> V postFile(String url, Params params, FileParam fileParam, TypeReference<V> typeReference) {
        return postFile(url, null, params, fileParam, typeReference);
    }

    public String postFile(String url, Params params, List<FileParam> fileParams) {
        return postFile(url, null, params, fileParams, String.class);
    }

    public <V> V postFile(String url, Params params, List<FileParam> fileParams, Class<V> c) {
        return postFile(url, null, params, fileParams, c);
    }

    public <V> V postFile(String url, Params params, List<FileParam> fileParams, TypeReference<V> typeReference) {
        return postFile(url, null, params, fileParams, typeReference);
    }

    public String postFile(String url, Headers headers, Params params, FileParam fileParam) {
        return requester.execute(url, headers, params, fileParam, String.class);
    }

    public <V> V postFile(String url, Headers headers, Params params, FileParam fileParam, Class<V> c) {
        return requester.execute(url, headers, params, fileParam, c);
    }

    public <V> V postFile(String url, Headers headers, Params params, FileParam fileParam, TypeReference<V> typeReference) {
        return requester.execute(url, headers, params, fileParam, typeReference.getType());
    }

    public String postFile(String url, Headers headers, Params params, List<FileParam> fileParams) {
        return requester.execute(url, headers, params, fileParams, String.class);
    }

    public <V> V postFile(String url, Headers headers, Params params, List<FileParam> fileParams, Class<V> c) {
        return requester.execute(url, headers, params, fileParams, c);
    }

    public <V> V postFile(String url, Headers headers, Params params, List<FileParam> fileParams, TypeReference<V> typeReference) {
        return requester.execute(url, headers, params, fileParams, typeReference.getType());
    }

    public <T> String postJson(String url, T t) {
        return postJson(url, null, null, t, String.class);
    }

    public <V, T> V postJson(String url, T t, Class<V> c) {
        return postJson(url, null, null, t, c);
    }

    public <V, T> V postJson(String url, T t, TypeReference<V> typeReference) {
        return postJson(url, null, null, t, typeReference);
    }

    public <T> String postJson(String url, Headers headers, T t) {
        return postJson(url, headers, null, t, String.class);
    }

    public <V, T> V postJson(String url, Headers headers, T t, Class<V> c) {
        return postJson(url, headers, null, t, c);
    }

    public <V, T> V postJson(String url, Headers headers, T t, TypeReference<V> typeReference) {
        return postJson(url, headers, null, t, typeReference);
    }

    public <T> String postJson(String url, Params params, T t) {
        return postJson(url, null, params, t, String.class);
    }

    public <V, T> V postJson(String url, Params params, T t, Class<V> c) {
        return postJson(url, null, params, t, c);
    }

    public <V, T> V postJson(String url, Params params, T t, TypeReference<V> typeReference) {
        return postJson(url, null, params, t, typeReference);
    }

    public <V, T> V postJson(String url, Headers headers, Params params, T t, Class<V> c) {
        return requester.execute(Method.POST, url, headers, params, t, c);
    }

    public <T> String postJson(String url, Headers headers, Params params, T t) {
        return requester.execute(Method.POST, url, headers, params, t, String.class);
    }

    public <V, T> V postJson(String url, Headers headers, Params params, T t, TypeReference<V> typeReference) {
        return requester.execute(Method.POST, url, headers, params, t, typeReference.getType());
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

    public <T> void postAsync(String url, Headers headers, T t) {
        postAsync(url, headers, null, t, null);
    }

    public void postAsync(String url, Headers headers, RequestCallback callback) {
        postAsync(url, headers, null, callback);
    }

    public <T> void postAsync(String url, Headers headers, T t, RequestCallback callback) {
        postAsync(url, headers, null, t, callback);
    }

    public void postAsync(String url, Params params) {
        postAsync(url, null, params, null);
    }

    public void postAsync(String url, Params params, RequestCallback callback) {
        postAsync(url, null, params, callback);
    }

    public void postAsync(String url, Headers headers, Params params) {
        requester.enqueue(Method.POST, url, headers, params, null);
    }

    public void postAsync(String url, Headers headers, Params params, RequestCallback callback) {
        requester.enqueue(Method.POST, url, headers, params, callback);
    }

    public <T> void postAsync(String url, Headers headers, Params params, T t) {
        requester.enqueue(Method.POST, url, headers, params, t, null);
    }

    public <T> void postAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        requester.enqueue(Method.POST, url, headers, params, t, callback);
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
        requester.enqueue(url, headers, params, fileParam, callback);
    }

    public void postFileAsync(String url, Headers headers, Params params, List<FileParam> fileParams) {
        requester.enqueue(url, headers, params, fileParams, null);
    }

    public void postFileAsync(String url, Headers headers, Params params, List<FileParam> fileParams, RequestCallback callback) {
        requester.enqueue(url, headers, params, fileParams, callback);
    }

    public <T> void postJsonAsync(String url, T t) {
        postJsonAsync(url, null, null, t, null);
    }

    public <T> void postJsonAsync(String url, T t, RequestCallback callback) {
        postJsonAsync(url, null, null, t, callback);
    }

    public <T> void postJsonAsync(String url, Headers headers, T t) {
        postJsonAsync(url, headers, null, t, null);
    }

    public <T> void postJsonAsync(String url, Headers headers, T t, RequestCallback callback) {
        postJsonAsync(url, headers, null, t, callback);
    }

    public <T> void postJsonAsync(String url, Params params, T t) {
        postJsonAsync(url, null, params, t, null);
    }

    public <T> void postJsonAsync(String url, Params params, T t, RequestCallback callback) {
        postJsonAsync(url, null, params, t, callback);
    }

    public <T> void postJsonAsync(String url, Headers headers, Params params, T t) {
        postJsonAsync(url, headers, params, t, null);
    }

    public <T> void postJsonAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        requester.enqueue(Method.POST, url, headers, params, t, callback);
    }


    /******** put *********/

    public String put(String url) {
        return put(url, null, null, String.class);
    }

    public String put(String url, Headers headers) {
        return put(url, headers, null, String.class);
    }

    public <T> String put(String url, Headers headers, T t) {
        return put(url, headers, null, t, String.class);
    }

    public <V> V put(String url, Headers headers, Class<V> c) {
        return put(url, headers, null, c);
    }

    public <V> V put(String url, Headers headers, TypeReference<V> typeReference) {
        return put(url, headers, null, typeReference);
    }

    public <V, T> V put(String url, Headers headers, T t, Class<V> c) {
        return put(url, headers, null, t, c);
    }

    public <V, T> V put(String url, Headers headers, T t, TypeReference<V> typeReference) {
        return put(url, headers, null, t, typeReference);
    }

    public String put(String url, Params params) {
        return put(url, null, params, String.class);
    }

    public <V> V put(String url, Params params, Class<V> c) {
        return put(url, null, params, c);
    }

    public <V> V put(String url, Params params, TypeReference<V> typeReference) {
        return put(url, null, params, typeReference);
    }

    public String put(String url, Headers headers, Params params) {
        return put(url, headers, params, String.class);
    }

    public <V> V put(String url, Headers headers, Params params, Class<V> c) {
        return requester.execute(Method.PUT, url, headers, params, c);
    }

    public <V> V put(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return requester.execute(Method.PUT, url, headers, params, typeReference.getType());
    }

    public <T> String put(String url, Headers headers, Params params, T t) {
        return put(url, headers, params, t, String.class);
    }

    public <V, T> V put(String url, Headers headers, Params params, T t, Class<V> c) {
        return requester.execute(Method.PUT, url, headers, params, t, c);
    }

    public <V, T> V put(String url, Headers headers, Params params, T t, TypeReference<V> typeReference) {
        return requester.execute(Method.PUT, url, headers, params, t, typeReference.getType());
    }

    public <T> String putJson(String url, T t) {
        return putJson(url, null, null, t, String.class);
    }

    public <V, T> V putJson(String url, T t, Class<V> c) {
        return putJson(url, null, null, t, c);
    }

    public <V, T> V putJson(String url, T t, TypeReference<V> typeReference) {
        return putJson(url, null, null, t, typeReference);
    }

    public <T> String putJson(String url, Headers headers, T t) {
        return putJson(url, headers, null, t, String.class);
    }

    public <V, T> V putJson(String url, Headers headers, T t, Class<V> c) {
        return putJson(url, headers, null, t, c);
    }

    public <V, T> V putJson(String url, Headers headers, T t, TypeReference<V> typeReference) {
        return putJson(url, headers, null, t, typeReference);
    }

    public <T> String putJson(String url, Params params, T t) {
        return putJson(url, null, params, t, String.class);
    }

    public <V, T> V putJson(String url, Params params, T t, Class<V> c) {
        return putJson(url, null, params, t, c);
    }

    public <V, T> V putJson(String url, Params params, T t, TypeReference<V> typeReference) {
        return putJson(url, null, params, t, typeReference);
    }

    public <T> String putJson(String url, Headers headers, Params params, T t) {
        return requester.execute(Method.PUT, url, headers, params, t, String.class);
    }

    public <V, T> V putJson(String url, Headers headers, Params params, T t, Class<V> c) {
        return requester.execute(Method.PUT, url, headers, params, t, c);
    }

    public <V, T> V putJson(String url, Headers headers, Params params, T t, TypeReference<V> typeReference) {
        return requester.execute(Method.PUT, url, headers, params, t, typeReference.getType());
    }

    public void putAsync(String url) {
        putAsync(url, null, null, null);
    }

    public void putAsync(String url, Headers headers) {
        putAsync(url, headers, null, null);
    }

    public <T> void putAsync(String url, Headers headers, T t) {
        putAsync(url, headers, null, t, null);
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
        requester.enqueue(Method.PUT, url, headers, params, callback);
    }

    public <T> void putAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        requester.enqueue(Method.PUT, url, headers, params, t, callback);
    }

    public <T> void putJsonAsync(String url, T t) {
        putJsonAsync(url, null, t, null);
    }

    public <T> void putJsonAsync(String url, T t, RequestCallback callback) {
        putJsonAsync(url, null, t, callback);
    }

    public <T> void putJsonAsync(String url, Headers headers, T t) {
        putJsonAsync(url, headers, t, null);
    }

    public <T> void putJsonAsync(String url, Headers headers, T t, RequestCallback callback) {
        putJsonAsync(url, headers, null, t, callback);
    }

    public <T> void putJsonAsync(String url, Headers headers, Params params, T t) {
        putJsonAsync(url, headers, params, t, null);
    }

    public <T> void putJsonAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        requester.enqueue(Method.PUT, url, headers, params, t, callback);
    }


    /******** patch *********/

    public String patch(String url) {
        return patch(url, null, null, String.class);
    }

    public <V> V patch(String url, Class<V> c) {
        return patch(url, null, null, c);
    }

    public <V> V patch(String url, TypeReference<V> typeReference) {
        return patch(url, null, null, typeReference);
    }

    public String patch(String url, Headers headers) {
        return patch(url, headers, null, String.class);
    }

    public <V> V patch(String url, Headers headers, Class<V> c) {
        return patch(url, headers, null, c);
    }

    public <V> V patch(String url, Headers headers, TypeReference<V> typeReference) {
        return patch(url, headers, null, typeReference);
    }

    public <T> String patch(String url, Headers headers, T t) {
        return patch(url, headers, null, t, String.class);
    }

    public <V, T> V patch(String url, Headers headers, T t, Class<V> c) {
        return patch(url, headers, null, t, c);
    }

    public <V, T> V patch(String url, Headers headers, T t, TypeReference<V> typeReference) {
        return patch(url, headers, null, t, typeReference);
    }

    public String patch(String url, Params params) {
        return patch(url, null, params, String.class);
    }

    public <V> V patch(String url, Params params, Class<V> c) {
        return patch(url, null, params, c);
    }

    public <V> V patch(String url, Params params, TypeReference<V> typeReference) {
        return patch(url, null, params, typeReference);
    }

    public String patch(String url, Headers headers, Params params) {
        return requester.execute(Method.PATCH, url, headers, params, String.class);
    }

    public <V> V patch(String url, Headers headers, Params params, Class<V> c) {
        return requester.execute(Method.PATCH, url, headers, params, c);
    }

    public <V> V patch(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return requester.execute(Method.PATCH, url, headers, params, typeReference.getType());
    }

    public <T> String patch(String url, Headers headers, Params params, T t) {
        return requester.execute(Method.PATCH, url, headers, params, t, String.class);
    }

    public <V, T> V patch(String url, Headers headers, Params params, T t, Class<V> c) {
        return requester.execute(Method.PATCH, url, headers, params, t, c);
    }

    public <V, T> V patch(String url, Headers headers, Params params, T t, TypeReference<V> typeReference) {
        return requester.execute(Method.PATCH, url, headers, params, t, typeReference.getType());
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

    public <T> void patchAsync(String url, Headers headers, T t) {
        patchAsync(url, headers, null, t, null);
    }

    public <T> void patchAsync(String url, Headers headers, T t, RequestCallback callback) {
        patchAsync(url, headers, null, t, callback);
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
        requester.enqueue(Method.PATCH, url, headers, params, callback);
    }

    public <T> void patchAsync(String url, Headers headers, Params params, T t) {
        patchAsync(url, headers, params, t, null);
    }

    public <T> void patchAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        requester.enqueue(Method.PATCH, url, headers, params, t, callback);
    }

    /******** delete *********/

    public String delete(String url) {
        return delete(url, null, null, String.class);
    }

    public <V> V delete(String url, Class<V> c) {
        return delete(url, null, null, c);
    }

    public <V> V delete(String url, TypeReference<V> typeReference) {
        return delete(url, null, null, typeReference);
    }

    public String delete(String url, Headers headers) {
        return delete(url, headers, null, String.class);
    }

    public <V> V delete(String url, Headers headers, Class<V> c) {
        return delete(url, headers, null, c);
    }

    public <V> V delete(String url, Headers headers, TypeReference<V> typeReference) {
        return delete(url, headers, null, typeReference);
    }

    public String delete(String url, Params params) {
        return delete(url, null, params, String.class);
    }

    public <V> V delete(String url, Params params, Class<V> c) {
        return delete(url, null, params, c);
    }

    public <V> V delete(String url, Params params, TypeReference<V> typeReference) {
        return delete(url, null, params, typeReference);
    }

    public String delete(String url, Headers headers, Params params) {
        return requester.execute(Method.DELETE, url, headers, params, String.class);
    }

    public <V> V delete(String url, Headers headers, Params params, Class<V> c) {
        return requester.execute(Method.DELETE, url, headers, params, c);
    }

    public <V> V delete(String url, Headers headers, Params params, TypeReference<V> typeReference) {
        return requester.execute(Method.DELETE, url, headers, params, typeReference.getType());
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
        requester.enqueue(Method.DELETE, url, headers, params, callback);
    }

    /******** download *********/

    public void download(String url, String targetPath) {
        requester.download(url, null, null, targetPath, false);
    }

    public void download(String url, Headers headers, String targetPath) {
        requester.download(url, headers, null, targetPath, false);
    }

    public void download(String url, Params params, String targetPath) {
        requester.download(url, null, params, targetPath, false);
    }

    public void download(String url, Headers headers, Params params, String targetPath) {
        requester.download(url, headers, params, targetPath, false);
    }

    public void downloadAsync(String url, String targetPath) {
        requester.download(url, null, null, targetPath, true);
    }

    public void downloadAsync(String url, Headers headers, String targetPath) {
        requester.download(url, headers, null, targetPath, true);
    }

    public void downloadAsync(String url, Params params, String targetPath) {
        requester.download(url, null, params, targetPath, true);
    }

    public void downloadAsync(String url, Headers headers, Params params, String targetPath) {
        requester.download(url, headers, params, targetPath, true);
    }

}
