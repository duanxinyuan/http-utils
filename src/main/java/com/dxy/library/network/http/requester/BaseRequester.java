package com.dxy.library.network.http.requester;

import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import com.dxy.library.network.http.serializer.HttpSerializer;
import com.google.common.collect.Lists;
import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.constant.Method;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 请求实例基类
 * @author duanxinyuan
 * 2018/8/24 11:48
 */
@Slf4j
public abstract class BaseRequester {

    private HttpSerializer httpSerializer;

    //是否记录请求日志
    private boolean requestLogEnable;

    //超时时间，单位为秒
    private int timeout;

    //请求重试次数
    private int retries;

    //重试间隔毫秒数
    private long retryIntervalMillis;

    public BaseRequester(HttpSerializer httpSerializer, boolean requestLogEnable, int timeout, int retries, long retryIntervalMillis) {
        this.httpSerializer = httpSerializer;
        this.requestLogEnable = requestLogEnable;
        this.timeout = timeout;
        this.retries = retries;
        this.retryIntervalMillis = retryIntervalMillis;
    }

    public HttpSerializer getHttpSerializer() {
        return httpSerializer;
    }

    public void setHttpSerializer(HttpSerializer httpSerializer) {
        this.httpSerializer = httpSerializer;
    }

    public boolean isRequestLogEnable() {
        return requestLogEnable;
    }

    public void setRequestLogEnable(boolean requestLogEnable) {
        this.requestLogEnable = requestLogEnable;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public long getRetryIntervalMillis() {
        return retryIntervalMillis;
    }

    public void setRetryIntervalMillis(long retryIntervalMillis) {
        this.retryIntervalMillis = retryIntervalMillis;
    }

    /******** 异步请求 *********/

    public void enqueue(Method method, String url, Headers headers, Params params, RequestCallback callback) {
        enqueue(method, url, headers, params, null, null, callback);
    }

    public <T> void enqueue(Method method, String url, Headers headers, Params params, T t, RequestCallback callback) {
        enqueue(method, url, headers, params, t, null, callback);
    }

    public void enqueue(String url, Headers headers, Params params, FileParam fileParam, RequestCallback callback) {
        enqueue(Method.POST, url, headers, params, null, Lists.newArrayList(fileParam), callback);
    }

    public void enqueue(String url, Headers headers, Params params, List<FileParam> fileParams, RequestCallback callback) {
        enqueue(Method.POST, url, headers, params, null, fileParams, callback);
    }


    /******** 同步请求 *********/

    public <V> V execute(Method method, String url, Headers headers, Params params, Type type) {
        return execute(method, url, headers, params, null, null, type);
    }

    public <V, T> V execute(Method method, String url, Headers headers, Params params, T t, Type type) {
        return execute(method, url, headers, params, t, null, type);
    }

    public <V> V execute(String url, Headers headers, Params params, FileParam fileParam, Type type) {
        return execute(Method.POST, url, headers, params, null, Lists.newArrayList(fileParam), type);
    }

    public <V> V execute(String url, Headers headers, Params params, List<FileParam> fileParams, Type type) {
        return execute(Method.POST, url, headers, params, null, fileParams, type);
    }

    /**
     * 异步请求
     */
    public abstract <T> void enqueue(Method method, String url, Headers headers, Params params, T t, List<FileParam> fileParams, RequestCallback callback);

    /**
     * 同步请求
     */
    public abstract <V, T> V execute(Method method, String url, Headers headers, Params params, T t, List<FileParam> fileParams, Type type);

    /**
     * 下载文件到本地
     */
    public abstract void download(String url, Headers headers, Params params, String targetPath, boolean isAsync);

    <T> void logResult(String url, Method method, Params params, Headers headers, T t, int code, Exception e) {
        String urlStr = ", url: " + url;
        String responseCodeStr = ", code: " + code;
        String methodStr = ", method: " + method.getMethod();
        String headerStr = headers == null ? "" : ", headers: " + httpSerializer.to(headers);
        String paramStr = params == null ? "" : ", params: " + httpSerializer.to(params);
        String bodyStr = t == null ? "" : ", body: " + httpSerializer.to(t);
        if (e != null) {
            log.info("http request execute failed{}", urlStr + responseCodeStr + methodStr + headerStr + paramStr + bodyStr, e);
        } else {
            log.info("http request execute successful{}", urlStr + responseCodeStr + methodStr + headerStr + paramStr + bodyStr);
        }
    }

}
