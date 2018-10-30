package com.dxy.library.network.http.requester;

import com.google.common.collect.Lists;
import com.dxy.library.json.gson.GsonUtil;
import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.constant.Method;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 请求实例基类
 * @author duanxinyuan
 * 2018/8/24 11:48
 */
@Slf4j
public abstract class BaseRequester {

    protected int ERROR_CODE = 500;

    protected String CANCELED = "Canceled";

    //是否记录日志
    protected boolean isLog;

    //超时时间，单位为秒
    protected int timeout;

    public BaseRequester(boolean isLog, int timeout) {
        this.isLog = isLog;
        this.timeout = timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
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

    public <V> V excute(Method method, String url, Headers headers, Params params, Type type) {
        return excute(method, url, headers, params, null, null, type);
    }

    public <V, T> V excute(Method method, String url, Headers headers, Params params, T t, Type type) {
        return excute(method, url, headers, params, t, null, type);
    }

    public <V> V excute(String url, Headers headers, Params params, FileParam fileParam, Type type) {
        return excute(Method.POST, url, headers, params, null, Lists.newArrayList(fileParam), type);
    }

    public <V> V excute(String url, Headers headers, Params params, List<FileParam> fileParams, Type type) {
        return excute(Method.POST, url, headers, params, null, fileParams, type);
    }

    /**
     * 异步请求
     */
    public abstract <T> void enqueue(Method method, String url, Headers headers, Params params, T t, List<FileParam> fileParams, RequestCallback callback);

    /**
     * 同步请求
     */
    public abstract <V, T> V excute(Method method, String url, Headers headers, Params params, T t, List<FileParam> fileParams, Type type);

    /**
     * 下载文件到本地
     */
    public abstract void download(String url, String targetPath, boolean isAsync);

    <T> void logResult(String url, Method method, Params params, Headers headers, T t, int code, Exception e) {
        String urlStr = ", url: " + url;
        String responseCodeStr = ", code: " + code;
        String methodStr = ", method: " + method.getMethod();
        String headerStr = headers == null ? "" : ", headers: " + GsonUtil.to(headers);
        String paramStr = params == null ? "" : ", params: " + GsonUtil.to(params);
        String bodyStr = t == null ? "" : ", body: " + GsonUtil.to(t);
        if (e != null) {
            log.info("http request execute failed{}", urlStr + responseCodeStr + methodStr + headerStr + paramStr + bodyStr, e);
        } else {
            log.info("http request execute successful{}", urlStr + responseCodeStr + methodStr + headerStr + paramStr + bodyStr);
        }
    }

}
