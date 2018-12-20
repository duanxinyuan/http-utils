package com.dxy.library.network.http.requester;

import com.dxy.library.json.gson.GsonUtil;
import com.dxy.library.network.http.builder.OkBuilder;
import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.constant.Method;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import com.dxy.library.network.http.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.lang.reflect.Type;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp请求实例
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
@Slf4j
public final class OkHttpRequester extends BaseRequester {

    private volatile static OkHttpClient httpClient;

    public OkHttpRequester(boolean isLog, int timeout) {
        super(isLog, timeout);
    }

    private OkHttpClient getOkHttpClient() {
        if (null == httpClient) {
            synchronized (OkHttpClient.class) {
                if (null == httpClient) {
                    httpClient = newClient(timeout);
                }
            }
        }
        return httpClient;
    }

    @Override
    public void setTimeout(int timeout) {
        super.setTimeout(timeout);
        httpClient = newClient(timeout);
    }

    private OkHttpClient newClient(long timeout) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.connectTimeout(timeout, TimeUnit.SECONDS);
        builder.readTimeout(timeout, TimeUnit.SECONDS);
        builder.writeTimeout(timeout, TimeUnit.SECONDS);
        try {
            //配置忽略SSL证书本地校验
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
            if (isLog) {
                log.error("ssl certificate config error", e);
            }
        }
        return builder.build();
    }

    /**
     * 异步请求
     */
    @Override
    public <T> void enqueue(Method method, String url, Headers headers, Params params, T t, List<FileParam> fileParams, RequestCallback callback) {
        OkBuilder builder = OkBuilder.builder(method, url, headers, params, t, fileParams);
        if (builder == null) {
            return;
        }
        getOkHttpClient().newCall(builder.build()).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                //log
                if (isLog) {
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
                if (isLog) {
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
    public <V, T> V excute(Method method, String url, Headers headers, Params params, T t, List<FileParam> fileParams, Type type) {
        OkBuilder builder = OkBuilder.builder(method, url, headers, params, t, fileParams);
        if (builder == null) {
            return null;
        }
        try {
            Response response = getOkHttpClient().newCall(builder.build()).execute();
            //log
            if (isLog) {
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
                    v = (V) body.byteStream();
                } else if (Reader.class == type) {
                    v = (V) body.charStream();
                } else {
                    v = GsonUtil.from(body.string(), type);
                }
            } catch (Exception e) {
                logResult(url, method, params, headers, t, response.code(), e);
            }
            body.close();
            return v;
        } catch (IOException e) {
            //log
            if (isLog) {
                logResult(url, method, params, headers, t, ERROR_CODE, e);
            }
            return null;
        }
    }

    /**
     * 下载文件到本地
     */
    @Override
    public void download(String url, String targetPath, boolean isAsync) {
        if (isAsync) {
            getOkHttpClient().newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (isLog) {
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
                Response response = getOkHttpClient().newCall(new Request.Builder().url(url).build()).execute();
                writeFile(response, targetPath);
            } catch (IOException e) {
                if (isLog) {
                    log.error("download error, url: {}, targetPath: {}, isAsync: {}", url, targetPath, isAsync, e);
                }
            }
        }
    }

    private void writeFile(Response response, String targetPath) {
        if (!response.isSuccessful()) {
            if (isLog) {
                log.error("download failed, url: {}, code: {}", response.request().url().toString(), response.code());
            }
            response.close();
            return;
        }
        ResponseBody body = response.body();
        if (body == null) {
            if (isLog) {
                log.error("downloaded resource body is null");
            }
            return;
        }
        FileUtil.createFile(targetPath);
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(targetPath));
             InputStream inputStream = body.byteStream()) {
            int readLength;
            byte[] buffer = new byte[4 * 1024];
            if (inputStream != null) {
                while ((readLength = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, readLength);
                }
            }
        } catch (IOException e) {
            if (isLog) {
                log.error("downloaded resource write to local error", e);
            }
        } finally {
            body.close();
        }
    }
}
