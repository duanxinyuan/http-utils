package com.dxy.library.network.http.requester;

import com.dxy.library.network.http.builder.OkBuilder;
import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.constant.Method;
import com.dxy.library.network.http.exception.HttpException;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.interceptor.RetryInterceptor;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import com.dxy.library.network.http.serializer.HttpSerializer;
import com.dxy.library.util.common.FileUtils;
import com.dxy.library.util.common.IOUtils;
import com.dxy.library.util.config.ConfigUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * OkHttp请求实例
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
@SuppressWarnings("unchecked")
@Slf4j
public final class OkHttpRequester extends AbstractRequester {

    private static final int ERROR_CODE = 500;

    //请求被取消的响应标志
    private static final String CANCELED = "Canceled";

    //异步请求的异步请求的最大并发请求数，默认为64
    private static final Integer ASYNC_MAX_REQUESTS = ConfigUtils.getAsInt("http.async.maxRequests", 64);

    //异步请求的异步请求的单个域名最大并发请求数，默认为5
    private static final Integer ASYNC_MAX_REQUESTS_PER_HOST = ConfigUtils.getAsInt("http.async.maxRequestsPerHost", 5);

    //每个地址的最大连接数，默认为5
    private static final Integer MAX_IDLE_CONNECTIONS = ConfigUtils.getAsInt("http.maxIdleConnections", 5);

    //连接的存活时间，单位为分钟，默认5分钟
    private static final Integer KEEP_ALIVE_DURATION = ConfigUtils.getAsInt("http.keepAliveDuration", 5);

    private volatile OkHttpClient httpClient;

    public OkHttpRequester(HttpSerializer httpSerializer, boolean requestLogEnable, long timeoutMillis, int retries, long retryIntervalMillis, boolean enableH2c) {
        super(httpSerializer, requestLogEnable, timeoutMillis, retries, retryIntervalMillis, enableH2c);
        httpClient = newClient();
    }

    @Override
    public void setRequestLogEnable(boolean requestLogEnable) {
        super.setRequestLogEnable(requestLogEnable);
        httpClient = newClient();
    }

    @Override
    public void setTimeoutMillis(long timeoutMillis) {
        super.setTimeoutMillis(timeoutMillis);
        httpClient = newClient();
    }

    @Override
    public void setRetries(int retries) {
        super.setRetries(retries);
        httpClient = newClient();
    }

    @Override
    public void setEnableH2c(boolean enableH2c) {
        super.setEnableH2c(enableH2c);
        httpClient = newClient();
    }

    private OkHttpClient newClient() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

        //配置超时
        builder.connectTimeout(getTimeoutMillis(), TimeUnit.MILLISECONDS);
        builder.readTimeout(getTimeoutMillis(), TimeUnit.MILLISECONDS);
        builder.writeTimeout(getTimeoutMillis(), TimeUnit.MILLISECONDS);

        //配置连接池
        builder.connectionPool(new ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_DURATION, TimeUnit.MINUTES));

        //配置网络协议
        //启用TLSv1和TLSv1.1，OkHttp3.13.0之后不再支持，此处特意添加 ConnectionSpec.COMPATIBLE_TLS
        //ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT为默认支持的配置
        builder.connectionSpecs(Lists.newArrayList(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT, ConnectionSpec.COMPATIBLE_TLS));
        if (isEnableH2c()) {
            builder.protocols(Collections.singletonList(Protocol.H2_PRIOR_KNOWLEDGE));
        }

        //配置异步请求的并发数
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(ASYNC_MAX_REQUESTS);
        dispatcher.setMaxRequestsPerHost(ASYNC_MAX_REQUESTS_PER_HOST);
        builder.dispatcher(dispatcher);

        //配置重试
        if (getRetries() > 0) {
            builder.addInterceptor(new RetryInterceptor(getRetries(), getRetryIntervalMillis()));
        }

        //配置忽略客户端SSL证书
        try {
            //配置忽略SSL证书
            X509TrustManager trustManager = new X509ExtendedTrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
                }

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
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());

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
     * 同步请求
     */
    @Override
    public <T> Response execute(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams) {
        OkBuilder builder = OkBuilder.builder(getHttpSerializer(), method, url, headers, params, body, fileParams);
        long startTime = System.currentTimeMillis();
        try {
            Response response = httpClient.newCall(builder.build()).execute();
            //log
            if (isRequestLogEnable()) {
                long executionTime = System.currentTimeMillis() - startTime;
                logResult(url, method, params, headers, body, response.code(), null, executionTime);
            }
            return response;
        } catch (IOException e) {
            //log
            if (isRequestLogEnable()) {
                long executionTime = System.currentTimeMillis() - startTime;
                logResult(url, method, params, headers, body, ERROR_CODE, e, executionTime);
            }
            throw new HttpException("http call execute error", e);
        }
    }

    /**
     * 同步请求
     */
    @Override
    public <V, T> V execute(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams, Type type) {
        Response response = execute(method, url, headers, params, body, fileParams);
        return serialize(response, type);
    }

    /**
     * 异步请求
     */
    @Override
    public <T> void enqueue(Method method, String url, Headers headers, Params params, T body, List<FileParam> fileParams, RequestCallback callback) {
        OkBuilder builder = OkBuilder.builder(getHttpSerializer(), method, url, headers, params, body, fileParams);
        long startTime = System.currentTimeMillis();
        httpClient.newCall(builder.build()).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                //log
                if (isRequestLogEnable()) {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logResult(url, method, params, headers, body, ERROR_CODE, e, executionTime);
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
                    long executionTime = System.currentTimeMillis() - startTime;
                    logResult(url, method, params, headers, body, response.code(), null, executionTime);
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
                    writeResponse(response, targetPath);
                }
            });
        } else {
            try {
                Response response = httpClient.newCall(builder.build()).execute();
                writeResponse(response, targetPath);
            } catch (IOException e) {
                if (isRequestLogEnable()) {
                    log.error("download error, url: {}, targetPath: {}, isAsync: {}", url, targetPath, isAsync, e);
                }
            }
        }
    }

    /**
     * 下载文件到 OutputStream
     */
    @Override
    public void download(String url, Headers headers, Params params, OutputStream outputStream, boolean isAsync) {
        OkBuilder builder = OkBuilder.builder(getHttpSerializer(), Method.GET, url, headers, params, null, null);
        if (isAsync) {
            httpClient.newCall(builder.build()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (isRequestLogEnable()) {
                        log.error("download error, url: {}, isAsync: {}", url, isAsync, e);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) {
                    writeResponse(response, outputStream);
                }
            });
        } else {
            try {
                Response response = httpClient.newCall(builder.build()).execute();
                writeResponse(response, outputStream);
            } catch (IOException e) {
                if (isRequestLogEnable()) {
                    log.error("download error, url: {}, isAsync: {}", url, isAsync, e);
                }
            }
        }
    }

    /**
     * 将 Response 反序列化为对象
     */
    @Override
    public <V> V serialize(Response response, Type type) {
        try {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            V v;
            if (byte[].class == type || Byte[].class == type) {
                v = (V) responseBody.bytes();
            } else if (String.class == type) {
                v = (V) responseBody.string();
            } else if (InputStream.class == type) {
                InputStream inputStream = IOUtils.cloneInputStream(responseBody.byteStream());
                v = (V) inputStream;
            } else if (Reader.class == type) {
                InputStream inputStream = IOUtils.cloneInputStream(responseBody.byteStream());
                MediaType contentType = responseBody.contentType();
                Charset charset = contentType != null ? contentType.charset(StandardCharsets.UTF_8) : StandardCharsets.UTF_8;
                if (null == charset) {
                    charset = StandardCharsets.UTF_8;
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
                v = (V) inputStreamReader;
            } else {
                v = getHttpSerializer().from(responseBody.string(), type);
            }
            responseBody.close();
            return v;
        } catch (Exception e) {
            throw new HttpException("http response serialize error", e);
        }
    }

    /**
     * 将 Response 写到本地文件
     */
    private void writeResponse(Response response, String targetPath) {
        ResponseBody body = checkResponse(response);
        if (body == null) {
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

    /**
     * 将 Response 写到 OutputStream
     */
    private void writeResponse(Response response, OutputStream outputStream) {
        ResponseBody body = checkResponse(response);
        if (body == null) {
            return;
        }
        try (InputStream inputStream = body.byteStream()) {
            int readLength;
            byte[] buffer = new byte[4 * 1024];
            while ((readLength = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readLength);
            }
        } catch (IOException e) {
            if (isRequestLogEnable()) {
                log.error("downloaded resource write to local error", e);
            }
        } finally {
            body.close();
        }
    }

    /**
     * 检查 Response
     */
    private ResponseBody checkResponse(Response response) {
        if (!response.isSuccessful()) {
            if (isRequestLogEnable()) {
                log.error("download failed, url: {}, code: {}", response.request().url().toString(), response.code());
            }
            response.close();
            return null;
        }
        ResponseBody body = response.body();
        if (body == null) {
            if (isRequestLogEnable()) {
                log.error("downloaded resource body is null");
            }
            return null;
        }
        return body;
    }

}
