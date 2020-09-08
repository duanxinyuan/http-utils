package com.dxy.library.network.http.requester;

import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.constant.Method;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import com.dxy.library.network.http.serializer.HttpSerializer;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 请求实例基类
 * @author duanxinyuan
 * 2018/8/24 11:48
 */
@Slf4j
public abstract class AbstractRequester {

    private HttpSerializer httpSerializer;

    //是否记录请求日志
    private boolean requestLogEnable;

    //超时时间，单位为毫秒
    private long timeoutMillis;

    //请求重试次数
    private int retries;

    //重试间隔毫秒数
    private long retryIntervalMillis;

    // 开启 clearText Http2 注意线上因为有域名，需要确认nginx解析支持h2c,否则不能贸然开启
    private boolean enableH2c;

    public AbstractRequester(HttpSerializer httpSerializer, boolean requestLogEnable, long timeoutMillis, int retries, long retryIntervalMillis, boolean enableH2c) {
        this.httpSerializer = httpSerializer;
        this.requestLogEnable = requestLogEnable;
        this.timeoutMillis = timeoutMillis;
        this.retries = retries;
        this.retryIntervalMillis = retryIntervalMillis;
        this.enableH2c = enableH2c;
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

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
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

    public boolean isEnableH2c() {
        return enableH2c;
    }

    public void setEnableH2c(boolean enableH2c) {
        this.enableH2c = enableH2c;
    }


    /******** 同步请求 *********/

    /**
     * 同步请求，直接返回 Response
     */
    public abstract <T> Response execute(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams);

    /**
     * 同步请求，Response 会反序列化成对象
     */
    public abstract <V, T> V execute(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams, Type type);


    /******** 异步请求 *********/

    /**
     * 异步请求
     */
    public abstract <T> void enqueue(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams, RequestCallback callback);


    /******** 下载文件 *********/

    /**
     * 下载文件到本地
     */
    public abstract void download(String url, Headers headers, Params params, String targetPath, boolean isAsync);

    /**
     * 下载文件到 OutputStream
     */
    public abstract void download(String url, Headers headers, Params params, OutputStream outputStream, boolean isAsync);


    /******** Response 反序列化 *********/

    /**
     * 将 Response 反序列化为对象
     */
    public abstract <V> V serialize(Response response, Type type);


    /******** 打印请求日志 *********/

    <T> void logResult(String url, Method method, Params params, Headers headers, T body, int code, Throwable e, long executionTime) {
        StringBuilder sb = new StringBuilder();
        sb.append(", url: ").append(url);
        sb.append(", code: ").append(code);
        sb.append(", method: ").append(method.name());
        sb.append(headers == null ? "" : ", headers: " + httpSerializer.to(headers));
        sb.append(params == null ? "" : ", params: " + httpSerializer.to(params));
        sb.append(body == null ? "" : ", body: " + httpSerializer.to(body));
        sb.append(", execution time: ").append(executionTime).append("ms");

        if (e != null) {
            log.info("http request execute failed{}", sb.toString(), e);
        } else {
            log.info("http request execute successful{}", sb.toString());
        }
    }

}
