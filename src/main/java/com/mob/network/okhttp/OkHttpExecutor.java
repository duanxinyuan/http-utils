package com.mob.network.okhttp;

import com.google.gson.reflect.TypeToken;
import com.mob.library.json.GsonUtil;
import com.mob.network.okhttp.builder.OkBuilder;
import com.mob.network.okhttp.callback.RequestCallback;
import com.mob.network.okhttp.constant.Method;
import com.mob.network.okhttp.header.Headers;
import com.mob.network.okhttp.param.Params;
import com.mob.network.okhttp.ssl.SSLSocketFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;

import javax.net.ssl.HostnameVerifier;
import java.io.*;
import java.security.KeyStore;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * OkHttp执行器
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
@Slf4j
final class OkHttpExecutor {

    private volatile static OkHttpExecutor instance;
    private volatile static OkHttpClient httpClient;
    private int ERROR_CODE = 500;
    private static boolean isLog = true;

    private OkHttpExecutor() {
    }

    static OkHttpExecutor getInstance() {
        if (instance == null) {
            synchronized (OkHttpUtil.class) {
                if (instance == null) {
                    instance = new OkHttpExecutor();
                }
            }
        }
        return instance;
    }

    private OkHttpClient getOkHttpClient() {
        if (null == httpClient) {
            synchronized (OkHttpClient.class) {
                if (null == httpClient) {
                    OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
                    builder.addInterceptor((Interceptor.Chain chain) -> {
                        Request request = chain.request();
                        Response response = chain.proceed(request);
                        if (!response.isSuccessful() && response.code() != 400) {
                            //请求异常
                            if (isLog) {
                                log.error(processHttpError(response, request));
                            }
                        }
                        return response;
                    });
                    try {
                        //配置忽略SSL证书
                        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                        trustStore.load(null, null);
                        SSLSocketFactoryImpl ssl = new SSLSocketFactoryImpl(KeyStore.getInstance(KeyStore.getDefaultType()));
                        HostnameVerifier doNotVerify = (hostname, session) -> true;
                        builder.sslSocketFactory(ssl.getSSLContext().getSocketFactory(), ssl.getTrustManager()).hostnameVerifier(doNotVerify);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (isLog) {
                            log.error("OKHttp的SSL证书配置错误", e);
                        }
                    }
                    httpClient = builder.build();
                }
            }
        }
        return httpClient;
    }

    /**
     * 根据Tag取消网络请求，Tag在RequestBuilder中设置
     */
    public static void cancelTag(Object tag) {
        if (tag == null) {
            return;
        }
        synchronized (getInstance().getOkHttpClient().dispatcher().getClass()) {
            for (Call call : getInstance().getOkHttpClient().dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }

            for (Call call : getInstance().getOkHttpClient().dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
        }
    }

    /**
     * 是否记录日志
     */
    static void isLog(boolean isLog) {
        OkHttpExecutor.isLog = isLog;
    }

    /******** 异步请求 *********/

    void enqueue(Method method, String url, Headers headers, Params params, RequestCallback callback) {
        enqueue(method, url, headers, params, null, null, null, null, null, callback);
    }

    <T> void enqueue(Method method, String url, Headers headers, Params params, T t, RequestCallback callback) {
        enqueue(method, url, headers, params, t, null, null, null, null, callback);
    }

    void enqueue(String url, Headers headers, Params params, String fileKey, File file, RequestCallback callback) {
        enqueue(Method.POST, url, headers, params, null, fileKey, file, null, null, callback);
    }

    void enqueue(String url, Headers headers, Params params, String[] fileKeys, File[] files, RequestCallback callback) {
        enqueue(Method.POST, url, headers, params, null, null, null, fileKeys, files, callback);
    }


    /******** 同步请求 *********/

    <V> V excute(Method method, String url, Headers headers, Params params, Class<V> c) {
        return excute(method, url, headers, params, null, c, null);
    }

    <V> V excute(Method method, String url, Headers headers, Params params, TypeToken<V> typeToken) {
        return excute(method, url, headers, params, null, null, typeToken);
    }

    <V, T> V excute(Method method, String url, Headers headers, Params params, T t, Class<V> c) {
        return excute(method, url, headers, params, t, c, null);
    }

    <V, T> V excute(Method method, String url, Headers headers, Params params, T t, TypeToken<V> typeToken) {
        return excute(method, url, headers, params, t, null, typeToken);
    }

    /**
     * 异步请求
     */
    private <T> void enqueue(Method method, String url, Headers headers, Params params, T t, String fileKey, File file, String[] fileKeys, File[] files, RequestCallback callback) {
        OkBuilder builder = OkBuilder.builder(method, url, headers, params, t, fileKey, file, fileKeys, files);
        if (builder == null) {
            return;
        }
        getInstance().getOkHttpClient().newCall(builder.build()).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                //log
                if (isLog) {
                    logResult(url, method, params, headers, t, ERROR_CODE, e);
                }
                if (null == callback || "Canceled".equals(e.getMessage())) {
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
                    if (null == callback || "Canceled".equals(responseStr)) {
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
    private <V, T> V excute(Method method, String url, Headers headers, Params params, T t, Class<V> c, TypeToken<V> typeToken) {
        OkBuilder builder = OkBuilder.builder(method, url, headers, params, t, null, null, null, null);
        if (builder == null) {
            return null;
        }
        try {
            Response response = getInstance().getOkHttpClient().newCall(builder.build()).execute();
            //log
            if (isLog) {
                logResult(url, method, params, headers, t, response.code(), null);
            }
            if (response != null && (response.isSuccessful() || response.code() == 400)) {
                ResponseBody body = response.body();
                if (body == null) {
                    return null;
                }
                V v;
                if (c != null) {
                    if (byte[].class == c || Byte[].class == c) {
                        v = (V) body.bytes();
                    } else if (String.class == c) {
                        v = (V) body.string();
                    } else if (InputStream.class == c) {
                        v = (V) body.byteStream();
                    } else if (Reader.class == c) {
                        v = (V) body.charStream();
                    } else {
                        v = GsonUtil.from(body.string(), c);
                    }
                } else {
                    v = GsonUtil.from(body.string(), typeToken);
                }
                body.close();
                return v;
            } else {
                return null;
            }
        } catch (IOException e) {
            //log
            if (isLog) {
                logResult(url, method, params, headers, t, ERROR_CODE, e);
            }
            return null;
        }
    }

    private <T> void logResult(String url, Method method, Params params, Headers headers, T t, int code, IOException e) {
        String urlStr = ", url：" + url;
        String responseCodeStr = ", code：" + code;
        String methodStr = ", method：" + method.getMethod();
        String headerStr = headers == null ? "" : ", headers：" + GsonUtil.to(headers);
        String paramStr = params == null ? "" : ", params：" + GsonUtil.to(params);
        String bodyStr = t == null ? "" : ", body：" + GsonUtil.to(t);
        if (e != null) {
            log.info("Http Execute Failed，{}", urlStr + responseCodeStr + methodStr + headerStr + paramStr + bodyStr);
        } else {
            log.info("Http Execute Successed，{}", urlStr + responseCodeStr + methodStr + headerStr + paramStr + bodyStr);
        }
    }

    /**
     * 同步下载文件到本地
     */
    void download(String url, String targetPath) {
        try {
            Response response = getInstance().getOkHttpClient().newCall(new Request.Builder().url(url).build()).execute();
            writeFile(response, targetPath);
        } catch (IOException e) {
            e.printStackTrace();
            if (isLog) {
                log.error("文件同步下载失败", e);
            }
        }
    }

    /**
     * 异步下载文件到本地
     */
    void downloadAsync(String url, String targetPath) {
        getInstance().getOkHttpClient().newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (isLog) {
                    log.error("文件异步下载失败", e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                writeFile(response, targetPath);
            }
        });
    }

    private void writeFile(Response response, String targetPath) {
        if (!response.isSuccessful()) {
            if (isLog) {
                log.error("文件下载请求失败，url：{}，code：{}", response.request().url().toString(), response.code());
            }
            response.close();
            return;
        }
        ResponseBody body = response.body();
        if (body == null) {
            if (isLog) {
                log.error("文件下载请求的Body为空");
            }
            return;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(targetPath));
             InputStream inputStream = body.byteStream()) {
            createFile(targetPath);
            int readLength;
            byte buffer[] = new byte[4 * 1024];
            if (inputStream != null) {
                while ((readLength = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, readLength);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (isLog) {
                log.error("文件下载---文件流写入本地失败", e);
            }
        } finally {
            body.close();
        }
    }

    /**
     * 创建文件
     */
    private static void createFile(String localPath) {
        // 本地文件的地址
        File localFile = new File(localPath);
        if (localFile.isDirectory()) {
            if (!localFile.exists()) {
                localFile.mkdirs();
            }
        } else {
            String localPathDir = localPath.substring(0, localPath.lastIndexOf("/"));
            File localPathDirFile = new File(localPathDir);
            if (!localPathDirFile.exists()) {
                localPathDirFile.mkdirs();
            }
            if (!localFile.exists()) {
                try {
                    localFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /***
     *  处理http error
     */
    private static String processHttpError(Response response, Request request) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("url", request.url().toString());
        map.put("httpMethod", request.method());
        map.put("header", GsonUtil.to(request.headers().toMultimap()));
        if (response == null) {
            map.put("httpCode", "error");
            map.put("message", "无法请求该服务");
            return GsonUtil.to(map);
        }
        map.put("httpCode", response.code());
        map.put("message", response.message());
        try {
            ResponseBody body = response.body();
            if (body != null) {
                BufferedSource source = body.source();
                source.request(5000);
                // Buffer the entire body.
                map.put("responseContent", source.buffer().clone().readUtf8());
            }
        } catch (IOException e) {
            log.error("网络请求ResponseBody解析异常", e);
        }

        //获取到response的body的string字符串
        Set<String> strings = request.url().queryParameterNames();
        StringBuilder stringBuilder = new StringBuilder("");
        for (String str : strings) {
            stringBuilder.append(str).append("=").append(request.url().queryParameter(str)).append(",");
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        }

        //如果有body，再从body中拿参数
        if (request.body() instanceof FormBody) {
            FormBody body = (FormBody) request.body();
            for (int i = 0; i < body.size(); i++) {
                stringBuilder.append(body.name(i)).append("=").append(body.value(i)).append(",");
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            }
        } else {
            RequestBody body = request.body();
            if (body != null) {
                //get请求可能会空指针
                Buffer buffer1 = new Buffer();
                try {
                    body.writeTo(buffer1);
                    stringBuilder.append(buffer1.readUtf8());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        map.put("params", stringBuilder.toString());
        return GsonUtil.to(map);
    }
}
