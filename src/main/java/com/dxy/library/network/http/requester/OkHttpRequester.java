package com.dxy.library.network.http.requester;

import com.dxy.library.network.http.builder.OkBuilder;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.interceptor.RetryInterceptor;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import com.dxy.library.network.http.serializer.HttpSerializer;
import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.constant.Method;
import com.dxy.library.util.common.FileUtils;
import com.dxy.library.util.common.IOUtils;
import com.dxy.library.util.config.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp请求实例
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
@SuppressWarnings("unchecked")
@Slf4j
public final class OkHttpRequester extends BaseRequester {

    private static final int ERROR_CODE = 500;

    //请求被取消的响应标志
    private static final String CANCELED = "Canceled";

    //最大并发请求数，默认为64
    private static final Integer MAX_REQUESTS = ConfigUtils.getAsInt("http.maxRequests", 64);

    //单个域名最大并发请求数，默认为5
    private static final Integer MAX_REQUESTS_PER_HOST = ConfigUtils.getAsInt("http.maxRequestsPerHost", 5);

    private OkHttpClient httpClient;

    public OkHttpRequester(HttpSerializer httpSerializer, boolean requestLogEnable, int timeout, int retries, long retryIntervalMillis) {
        super(httpSerializer, requestLogEnable, timeout, retries, retryIntervalMillis);
        httpClient = newClient();
    }

    @Override
    public void setTimeout(int timeout) {
        super.setTimeout(timeout);
        httpClient = newClient();
    }

    @Override
    public void setRetries(int retries) {
        super.setRetries(retries);
        httpClient = newClient();
    }

    private OkHttpClient newClient() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.connectTimeout(getTimeout(), TimeUnit.SECONDS);
        builder.readTimeout(getTimeout(), TimeUnit.SECONDS);
        builder.writeTimeout(getTimeout(), TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(MAX_REQUESTS);
        dispatcher.setMaxRequestsPerHost(MAX_REQUESTS_PER_HOST);
        builder.dispatcher(dispatcher);
        if (getRetries() > 0) {
            builder.addInterceptor(new RetryInterceptor(getRetries(), getRetryIntervalMillis()));
        }
        try {
            //配置忽略SSL证书
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) { }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) { }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            HostnameVerifier doNotVerify = (hostname, session) -> true;
            builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager).hostnameVerifier(doNotVerify);
        } catch (Exception e) {
            if (isRequestLogEnable()) {
                log.error("ssl certificate config error", e);
            }
        }
        return builder.build();
    }

    /**
     * 异步请求
     */
    @Override
    public <T> void enqueue(Method method, String url, com.dxy.library.network.http.header.Headers headers, Params params, T t, List<FileParam> fileParams, RequestCallback callback) {
        OkBuilder builder = OkBuilder.builder(getHttpSerializer(), method, url, headers, params, t, fileParams);
        httpClient.newCall(builder.build()).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                //log
                if (isRequestLogEnable()) {
                    logResult(url, method, params, headers, t, ERROR_CODE, e);
                }
                if (null == callback || CANCELED.equals(e.getMessage())) {
                    //Http请求已经取消
                    return;
                }
                callback.failure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String responseStr = null;
                if (responseBody != null) {
                    responseStr = responseBody.string();
                    responseBody.close();
                }
                //log
                if (isRequestLogEnable()) {
                    logResult(url, method, params, headers, t, response.code(), null);
                }
                if (response.isSuccessful()) {
                    if (callback != null) {
                        callback.success(responseStr);
                    }
                } else {
                    if (null == callback || CANCELED.equals(responseStr)) {
                        //Http请求已经取消
                        return;
                    }
                    callback.failure(responseStr);
                }
            }
        });
    }

    /**
     * 同步请求
     */
    @Override
    public <V, T> V execute(Method method, String url, com.dxy.library.network.http.header.Headers headers, Params params, T t, List<FileParam> fileParams, Type type) {
        OkBuilder builder = OkBuilder.builder(getHttpSerializer(), method, url, headers, params, t, fileParams);
        try {
            Response response = httpClient.newCall(builder.build()).execute();
            //log
            if (isRequestLogEnable()) {
                logResult(url, method, params, headers, t, response.code(), null);
            }
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            V v = null;
            try {
                if (byte[].class == type || Byte[].class == type) {
                    v = (V) body.bytes();
                } else if (String.class == type) {
                    v = (V) body.string();
                } else if (InputStream.class == type) {
                    InputStream inputStream = IOUtils.cloneInputStream(body.byteStream());
                    v = (V) inputStream;
                } else if (Reader.class == type) {
                    InputStream inputStream = IOUtils.cloneInputStream(body.byteStream());
                    MediaType contentType = body.contentType();
                    Charset charset = contentType != null ? contentType.charset(StandardCharsets.UTF_8) : StandardCharsets.UTF_8;
                    if (null == charset) {
                        charset = StandardCharsets.UTF_8;
                    }
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
                    v = (V) inputStreamReader;
                } else {
                    v = getHttpSerializer().from(body.string(), type);
                }
            } catch (Exception e) {
                logResult(url, method, params, headers, t, response.code(), e);
            }
            body.close();
            return v;
        } catch (IOException e) {
            //log
            if (isRequestLogEnable()) {
                logResult(url, method, params, headers, t, ERROR_CODE, e);
            }
            return null;
        }
    }

    /**
     * 下载文件到本地
     */
    @Override
    public void download(String url, Headers headers, Params params, String targetPath, boolean isAsync) {
        OkBuilder builder = OkBuilder.builder(getHttpSerializer(), Method.GET, url, headers, params, null, null);
        if (isAsync) {
            httpClient.newCall(builder.build()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (isRequestLogEnable()) {
                        log.error("download error, url: {}, targetPath: {}, isAsync: {}", url, targetPath, isAsync, e);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) {
                    writeFile(response, targetPath);
                }
            });
        } else {
            try {
                Response response = httpClient.newCall(builder.build()).execute();
                writeFile(response, targetPath);
            } catch (IOException e) {
                if (isRequestLogEnable()) {
                    log.error("download error, url: {}, targetPath: {}, isAsync: {}", url, targetPath, isAsync, e);
                }
            }
        }
    }

    private void writeFile(Response response, String targetPath) {
        if (!response.isSuccessful()) {
            if (isRequestLogEnable()) {
                log.error("download failed, url: {}, code: {}", response.request().url().toString(), response.code());
            }
            response.close();
            return;
        }
        ResponseBody body = response.body();
        if (body == null) {
            if (isRequestLogEnable()) {
                log.error("downloaded resource body is null");
            }
            return;
        }
        FileUtils.createFile(targetPath);
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(targetPath));
             InputStream inputStream = body.byteStream()) {
            int readLength;
            byte[] buffer = new byte[4 * 1024];
            while ((readLength = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, readLength);
            }
        } catch (IOException e) {
            if (isRequestLogEnable()) {
                log.error("downloaded resource write to local error", e);
            }
        } finally {
            body.close();
        }
    }

}
