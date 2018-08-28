package com.dxy.library.network.http.requester;

import com.dxy.library.json.GsonUtil;
import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.constant.Method;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.Params;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * 请求实例基类
 * @author duanxinyuan
 * 2018/8/24 11:48
 */
@Slf4j
public abstract class BaseRequester {

    protected int ERROR_CODE = 500;

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
        enqueue(method, url, headers, params, null, null, null, null, null, callback);
    }

    public <T> void enqueue(Method method, String url, Headers headers, Params params, T t, RequestCallback callback) {
        enqueue(method, url, headers, params, t, null, null, null, null, callback);
    }

    public void enqueue(String url, Headers headers, Params params, String fileKey, File file, RequestCallback callback) {
        enqueue(Method.POST, url, headers, params, null, fileKey, file, null, null, callback);
    }

    public void enqueue(String url, Headers headers, Params params, String[] fileKeys, File[] files, RequestCallback callback) {
        enqueue(Method.POST, url, headers, params, null, null, null, fileKeys, files, callback);
    }


    /******** 同步请求 *********/

    public <V> V excute(Method method, String url, Headers headers, Params params, Class<V> c) {
        return excute(method, url, headers, params, null, c, null);
    }

    public <V> V excute(Method method, String url, Headers headers, Params params, TypeToken<V> typeToken) {
        return excute(method, url, headers, params, null, null, typeToken);
    }

    public <V, T> V excute(Method method, String url, Headers headers, Params params, T t, Class<V> c) {
        return excute(method, url, headers, params, t, c, null);
    }

    public <V, T> V excute(Method method, String url, Headers headers, Params params, T t, TypeToken<V> typeToken) {
        return excute(method, url, headers, params, t, null, typeToken);
    }


    /**
     * 异步请求
     */
    public abstract <T> void enqueue(Method method, String url, Headers headers, Params params, T t, String fileKey, File file, String[] fileKeys, File[] files, RequestCallback callback);

    /**
     * 同步请求
     */
    public abstract <V, T> V excute(Method method, String url, Headers headers, Params params, T t, Class<V> c, TypeToken<V> typeToken);

    /**
     * 下载文件到本地
     */
    public abstract void download(String url, String targetPath, boolean isAsync);

    protected <T> void logResult(String url, Method method, Params params, Headers headers, T t, int code, IOException e) {
        String urlStr = ", url: " + url;
        String responseCodeStr = ", code: " + code;
        String methodStr = ", method: " + method.getMethod();
        String headerStr = headers == null ? "" : ", headers: " + GsonUtil.to(headers);
        String paramStr = params == null ? "" : ", params: " + GsonUtil.to(params);
        String bodyStr = t == null ? "" : ", body: " + GsonUtil.to(t);
        if (e != null) {
            log.info("Http Execute Failed {}", urlStr + responseCodeStr + methodStr + headerStr + paramStr + bodyStr);
        } else {
            log.info("Http Execute Successed {}", urlStr + responseCodeStr + methodStr + headerStr + paramStr + bodyStr);
        }
    }

}
