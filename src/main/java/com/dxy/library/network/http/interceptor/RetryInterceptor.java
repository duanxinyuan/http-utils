package com.dxy.library.network.http.interceptor;

import com.dxy.library.util.config.ConfigUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 请求失败的重试拦截器
 * @author duanxinyuan
 * 2019/10/29 18:37
 */
@SuppressWarnings("NullableProblems")
public class RetryInterceptor implements Interceptor {
    //默认重试间隔为0毫秒
    public static final long RETRY_INTERVAL_DEFAULT = ConfigUtils.getAsLong("http.retryIntervalMillis",0L);

    //最大重试次数
    private int maxRetryCount;

    //重试间隔毫秒数
    private long retryIntervalMillis;

    public RetryInterceptor(int maxRetryCount, long retryIntervalMillis) {
        this.maxRetryCount = maxRetryCount;
        this.retryIntervalMillis = retryIntervalMillis;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        int retryCount = 0;
        while (!response.isSuccessful() && retryCount < maxRetryCount) {
            if (retryIntervalMillis > 0) {
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(retryIntervalMillis));
            }
            retryCount++;
            response = chain.proceed(request);
        }
        return response;
    }

}
