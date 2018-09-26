package com.dxy.library.network.http.executor;

import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.constant.Method;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import com.dxy.library.network.http.requester.OkHttpRequester;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Http执行器
 * @author duanxinyuan
 * 2018/8/23 21:27
 */
public class Executor {

    //是否记录日志
    private boolean isLog;

    //超时时间，单位为秒
    private int timeout;

    private OkHttpRequester instance;

    public Executor(boolean isLog, int timeout) {
        this.isLog = isLog;
        this.timeout = timeout;
    }

    /**
     * 设置超时时间，单位为秒
     */
    public Executor timeout(int timeout) {
        if (timeout != this.timeout) {
            this.timeout = timeout;
            if (instance != null) {
                instance.setTimeout(timeout);
            }
        }
        return this;
    }

    public OkHttpRequester getExecutorInstance() {
        if (instance == null) {
            synchronized (OkHttpRequester.class) {
                if (instance == null) {
                    instance = new OkHttpRequester(isLog, timeout);
                }
            }
        }
        return instance;
    }

    /******** get *********/

    public String get(String url) {
        return get(url, null, null, String.class);
    }

    public <V> V get(String url, Class<V> c) {
        return get(url, null, null, c);
    }

    public <V> V get(String url, TypeToken<V> typeToken) {
        return get(url, null, null, typeToken);
    }

    public String get(String url, Headers headers) {
        return get(url, headers, null, String.class);
    }

    public <V> V get(String url, Headers headers, Class<V> c) {
        return get(url, headers, null, c);
    }

    public <V> V get(String url, Headers headers, TypeToken<V> typeToken) {
        return get(url, headers, null, typeToken);
    }

    public String get(String url, Params params) {
        return get(url, null, params, String.class);
    }

    public <V> V get(String url, Params params, Class<V> c) {
        return get(url, null, params, c);
    }

    public <V> V get(String url, Params params, TypeToken<V> typeToken) {
        return get(url, null, params, typeToken);
    }

    public String get(String url, Headers headers, Params params) {
        return getExecutorInstance().excute(Method.GET, url, headers, params, String.class);
    }

    public <V> V get(String url, Headers headers, Params params, Class<V> c) {
        return getExecutorInstance().excute(Method.GET, url, headers, params, c);
    }

    public <V> V get(String url, Headers headers, Params params, TypeToken<V> typeToken) {
        return getExecutorInstance().excute(Method.GET, url, headers, params, typeToken.getType());
    }

    public void getAsync(String url) {
        getAsync(url, null, null, null);
    }

    public void getAsync(String url, RequestCallback callback) {
        getAsync(url, null, null, callback);
    }

    public void getAsync(String url, Params params, RequestCallback callback) {
        getAsync(url, null, params, callback);
    }

    public void getAsync(String url, Headers headers, RequestCallback callback) {
        getAsync(url, headers, null, callback);
    }

    public void getAsync(String url, Headers headers, Params params, RequestCallback callback) {
        getExecutorInstance().enqueue(Method.GET, url, headers, params, callback);
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

    public <V> V post(String url, Headers headers, TypeToken<V> typeToken) {
        return post(url, headers, null, typeToken);
    }

    public <V, T> V post(String url, Headers headers, T t, Class<V> c) {
        return post(url, headers, null, t, c);
    }

    public <V, T> V post(String url, Headers headers, T t, TypeToken<V> typeToken) {
        return post(url, headers, null, t, typeToken);
    }

    public String post(String url, Params params) {
        return post(url, null, params, String.class);
    }

    public <V> V post(String url, Params params, Class<V> c) {
        return post(url, null, params, c);
    }

    public <V> V post(String url, Params params, TypeToken<V> typeToken) {
        return post(url, null, params, typeToken);
    }

    public String post(String url, Headers headers, Params params) {
        return getExecutorInstance().excute(Method.POST, url, headers, params, String.class);
    }

    public <V> V post(String url, Headers headers, Params params, Class<V> c) {
        return getExecutorInstance().excute(Method.POST, url, headers, params, c);
    }

    public <V> V post(String url, Headers headers, Params params, TypeToken<V> typeToken) {
        return getExecutorInstance().excute(Method.POST, url, headers, params, typeToken.getType());
    }

    public <T> String post(String url, Headers headers, Params params, T t) {
        return getExecutorInstance().excute(Method.POST, url, headers, params, t, String.class);
    }

    public <V, T> V post(String url, Headers headers, Params params, T t, Class<V> c) {
        return getExecutorInstance().excute(Method.POST, url, headers, params, t, c);
    }

    public <V, T> V post(String url, Headers headers, Params params, T t, TypeToken<V> typeToken) {
        return getExecutorInstance().excute(Method.POST, url, headers, params, t, typeToken.getType());
    }

    public String postFile(String url, FileParam fileParam) {
        return postFile(url, null, null, fileParam, String.class);
    }

    public <V> V postFile(String url, FileParam fileParam, Class<V> c) {
        return postFile(url, null, null, fileParam, c);
    }

    public <V> V postFile(String url, FileParam fileParam, TypeToken<V> typeToken) {
        return postFile(url, null, null, fileParam, typeToken);
    }

    public String postFile(String url, Params params, FileParam fileParam) {
        return postFile(url, null, params, fileParam, String.class);
    }

    public <V> V postFile(String url, Params params, FileParam fileParam, Class<V> c) {
        return postFile(url, null, params, fileParam, c);
    }

    public <V> V postFile(String url, Params params, FileParam fileParam, TypeToken<V> typeToken) {
        return postFile(url, null, params, fileParam, typeToken);
    }

    public String postFile(String url, Params params, List<FileParam> fileParams) {
        return postFile(url, null, params, fileParams, String.class);
    }

    public <V> V postFile(String url, Params params, List<FileParam> fileParams, Class<V> c) {
        return postFile(url, null, params, fileParams, c);
    }

    public <V> V postFile(String url, Params params, List<FileParam> fileParams, TypeToken<V> typeToken) {
        return postFile(url, null, params, fileParams, typeToken);
    }

    public String postFile(String url, Headers headers, Params params, FileParam fileParam) {
        return getExecutorInstance().excute(url, headers, params, fileParam, String.class);
    }

    public <V> V postFile(String url, Headers headers, Params params, FileParam fileParam, Class<V> c) {
        return getExecutorInstance().excute(url, headers, params, fileParam, c);
    }

    public <V> V postFile(String url, Headers headers, Params params, FileParam fileParam, TypeToken<V> typeToken) {
        return getExecutorInstance().excute(url, headers, params, fileParam, typeToken.getType());
    }

    public String postFile(String url, Headers headers, Params params, List<FileParam> fileParams) {
        return getExecutorInstance().excute(url, headers, params, fileParams, String.class);
    }

    public <V> V postFile(String url, Headers headers, Params params, List<FileParam> fileParams, Class<V> c) {
        return getExecutorInstance().excute(url, headers, params, fileParams, c);
    }

    public <V> V postFile(String url, Headers headers, Params params, List<FileParam> fileParams, TypeToken<V> typeToken) {
        return getExecutorInstance().excute(url, headers, params, fileParams, typeToken.getType());
    }

    public <T> String postJson(String url, T t) {
        return postJson(url, null, null, t, String.class);
    }

    public <V, T> V postJson(String url, T t, Class<V> c) {
        return postJson(url, null, null, t, c);
    }

    public <V, T> V postJson(String url, T t, TypeToken<V> typeToken) {
        return postJson(url, null, null, t, typeToken);
    }

    public <T> String postJson(String url, Headers headers, T t) {
        return postJson(url, headers, null, t, String.class);
    }

    public <V, T> V postJson(String url, Headers headers, T t, Class<V> c) {
        return postJson(url, headers, null, t, c);
    }

    public <V, T> V postJson(String url, Headers headers, T t, TypeToken<V> typeToken) {
        return postJson(url, headers, null, t, typeToken);
    }

    public <T> String postJson(String url, Params params, T t) {
        return postJson(url, null, params, t, String.class);
    }

    public <V, T> V postJson(String url, Params params, T t, Class<V> c) {
        return postJson(url, null, params, t, c);
    }

    public <V, T> V postJson(String url, Params params, T t, TypeToken<V> typeToken) {
        return postJson(url, null, params, t, typeToken);
    }

    public <V, T> V postJson(String url, Headers headers, Params params, T t, Class<V> c) {
        return getExecutorInstance().excute(Method.POST, url, headers, params, t, c);
    }

    public <T> String postJson(String url, Headers headers, Params params, T t) {
        return getExecutorInstance().excute(Method.POST, url, headers, params, t, String.class);
    }

    public <V, T> V postJson(String url, Headers headers, Params params, T t, TypeToken<V> typeToken) {
        return getExecutorInstance().excute(Method.POST, url, headers, params, t, typeToken.getType());
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

    public void postAsync(String url, Headers headers, Params params, RequestCallback callback) {
        getExecutorInstance().enqueue(Method.POST, url, headers, params, callback);
    }

    public <T> void postAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        getExecutorInstance().enqueue(Method.POST, url, headers, params, t, callback);
    }

    public void postFileAsync(String url, FileParam fileParam, RequestCallback callback) {
        postFileAsync(url, null, null, fileParam, callback);
    }

    public void postFileAsync(String url, Params params, FileParam fileParam, RequestCallback callback) {
        postFileAsync(url, null, params, fileParam, callback);
    }

    public void postFileAsync(String url, Params params, List<FileParam> fileParams, RequestCallback callback) {
        postFileAsync(url, null, params, fileParams, callback);
    }

    public void postFileAsync(String url, Headers headers, Params params, FileParam fileParam, RequestCallback callback) {
        getExecutorInstance().enqueue(url, headers, params, fileParam, callback);
    }

    public void postFileAsync(String url, Headers headers, Params params, List<FileParam> fileParams, RequestCallback callback) {
        getExecutorInstance().enqueue(url, headers, params, fileParams, callback);
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
        getExecutorInstance().enqueue(Method.POST, url, headers, params, t, callback);
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

    public <V> V put(String url, Headers headers, TypeToken<V> typeToken) {
        return put(url, headers, null, typeToken);
    }

    public <V, T> V put(String url, Headers headers, T t, Class<V> c) {
        return put(url, headers, null, t, c);
    }

    public <V, T> V put(String url, Headers headers, T t, TypeToken<V> typeToken) {
        return put(url, headers, null, t, typeToken);
    }

    public String put(String url, Params params) {
        return put(url, null, params, String.class);
    }

    public <V> V put(String url, Params params, Class<V> c) {
        return put(url, null, params, c);
    }

    public <V> V put(String url, Params params, TypeToken<V> typeToken) {
        return put(url, null, params, typeToken);
    }

    public String put(String url, Headers headers, Params params) {
        return put(url, headers, params, String.class);
    }

    public <V> V put(String url, Headers headers, Params params, Class<V> c) {
        return getExecutorInstance().excute(Method.PUT, url, headers, params, c);
    }

    public <V> V put(String url, Headers headers, Params params, TypeToken<V> typeToken) {
        return getExecutorInstance().excute(Method.PUT, url, headers, params, typeToken.getType());
    }

    public <T> String put(String url, Headers headers, Params params, T t) {
        return put(url, headers, params, t, String.class);
    }

    public <V, T> V put(String url, Headers headers, Params params, T t, Class<V> c) {
        return getExecutorInstance().excute(Method.PUT, url, headers, params, t, c);
    }

    public <V, T> V put(String url, Headers headers, Params params, T t, TypeToken<V> typeToken) {
        return getExecutorInstance().excute(Method.PUT, url, headers, params, t, typeToken.getType());
    }

    public <T> String putJson(String url, T t) {
        return putJson(url, null, null, t, String.class);
    }

    public <V, T> V putJson(String url, T t, Class<V> c) {
        return putJson(url, null, null, t, c);
    }

    public <V, T> V putJson(String url, T t, TypeToken<V> typeToken) {
        return putJson(url, null, null, t, typeToken);
    }

    public <T> String putJson(String url, Headers headers, T t) {
        return putJson(url, headers, null, t, String.class);
    }

    public <V, T> V putJson(String url, Headers headers, T t, Class<V> c) {
        return putJson(url, headers, null, t, c);
    }

    public <V, T> V putJson(String url, Headers headers, T t, TypeToken<V> typeToken) {
        return putJson(url, headers, null, t, typeToken);
    }

    public <T> String putJson(String url, Params params, T t) {
        return putJson(url, null, params, t, String.class);
    }

    public <V, T> V putJson(String url, Params params, T t, Class<V> c) {
        return putJson(url, null, params, t, c);
    }

    public <V, T> V putJson(String url, Params params, T t, TypeToken<V> typeToken) {
        return putJson(url, null, params, t, typeToken);
    }

    public <T> String putJson(String url, Headers headers, Params params, T t) {
        return getExecutorInstance().excute(Method.PUT, url, headers, params, t, String.class);
    }

    public <V, T> V putJson(String url, Headers headers, Params params, T t, Class<V> c) {
        return getExecutorInstance().excute(Method.PUT, url, headers, params, t, c);
    }

    public <V, T> V putJson(String url, Headers headers, Params params, T t, TypeToken<V> typeToken) {
        return getExecutorInstance().excute(Method.PUT, url, headers, params, t, typeToken.getType());
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
        getExecutorInstance().enqueue(Method.PUT, url, headers, params, callback);
    }

    public <T> void putAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        getExecutorInstance().enqueue(Method.PUT, url, headers, params, t, callback);
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
        getExecutorInstance().enqueue(Method.PUT, url, headers, params, t, callback);
    }


    /******** patch *********/

    public String patch(String url) {
        return patch(url, null, null, String.class);
    }

    public <V> V patch(String url, Class<V> c) {
        return patch(url, null, null, c);
    }

    public <V> V patch(String url, TypeToken<V> typeToken) {
        return patch(url, null, null, typeToken);
    }

    public String patch(String url, Headers headers) {
        return patch(url, headers, null, String.class);
    }

    public <V> V patch(String url, Headers headers, Class<V> c) {
        return patch(url, headers, null, c);
    }

    public <V> V patch(String url, Headers headers, TypeToken<V> typeToken) {
        return patch(url, headers, null, typeToken);
    }

    public <T> String patch(String url, Headers headers, T t) {
        return patch(url, headers, null, t, String.class);
    }

    public <V, T> V patch(String url, Headers headers, T t, Class<V> c) {
        return patch(url, headers, null, t, c);
    }

    public <V, T> V patch(String url, Headers headers, T t, TypeToken<V> typeToken) {
        return patch(url, headers, null, t, typeToken);
    }

    public String patch(String url, Params params) {
        return patch(url, null, params, String.class);
    }

    public <V> V patch(String url, Params params, Class<V> c) {
        return patch(url, null, params, c);
    }

    public <V> V patch(String url, Params params, TypeToken<V> typeToken) {
        return patch(url, null, params, typeToken);
    }

    public String patch(String url, Headers headers, Params params) {
        return getExecutorInstance().excute(Method.PATCH, url, headers, params, String.class);
    }

    public <V> V patch(String url, Headers headers, Params params, Class<V> c) {
        return getExecutorInstance().excute(Method.PATCH, url, headers, params, c);
    }

    public <V> V patch(String url, Headers headers, Params params, TypeToken<V> typeToken) {
        return getExecutorInstance().excute(Method.PATCH, url, headers, params, typeToken.getType());
    }

    public <T> String patch(String url, Headers headers, Params params, T t) {
        return getExecutorInstance().excute(Method.PATCH, url, headers, params, t, String.class);
    }

    public <V, T> V patch(String url, Headers headers, Params params, T t, Class<V> c) {
        return getExecutorInstance().excute(Method.PATCH, url, headers, params, t, c);
    }

    public <V, T> V patch(String url, Headers headers, Params params, T t, TypeToken<V> typeToken) {
        return getExecutorInstance().excute(Method.PATCH, url, headers, params, t, typeToken.getType());
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
        getExecutorInstance().enqueue(Method.PATCH, url, headers, params, callback);
    }

    public <T> void patchAsync(String url, Headers headers, Params params, T t) {
        patchAsync(url, headers, params, t, null);
    }

    public <T> void patchAsync(String url, Headers headers, Params params, T t, RequestCallback callback) {
        getExecutorInstance().enqueue(Method.PATCH, url, headers, params, t, callback);
    }

    /******** delete *********/

    public String delete(String url) {
        return delete(url, null, null, String.class);
    }

    public <V> V delete(String url, Class<V> c) {
        return delete(url, null, null, c);
    }

    public <V> V delete(String url, TypeToken<V> typeToken) {
        return delete(url, null, null, typeToken);
    }

    public String delete(String url, Headers headers) {
        return delete(url, headers, null, String.class);
    }

    public <V> V delete(String url, Headers headers, Class<V> c) {
        return delete(url, headers, null, c);
    }

    public <V> V delete(String url, Headers headers, TypeToken<V> typeToken) {
        return delete(url, headers, null, typeToken);
    }

    public String delete(String url, Params params) {
        return delete(url, null, params, String.class);
    }

    public <V> V delete(String url, Params params, Class<V> c) {
        return delete(url, null, params, c);
    }

    public <V> V delete(String url, Params params, TypeToken<V> typeToken) {
        return delete(url, null, params, typeToken);
    }

    public String delete(String url, Headers headers, Params params) {
        return getExecutorInstance().excute(Method.DELETE, url, headers, params, String.class);
    }

    public <V> V delete(String url, Headers headers, Params params, Class<V> c) {
        return getExecutorInstance().excute(Method.DELETE, url, headers, params, c);
    }

    public <V> V delete(String url, Headers headers, Params params, TypeToken<V> typeToken) {
        return getExecutorInstance().excute(Method.DELETE, url, headers, params, typeToken.getType());
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
        getExecutorInstance().enqueue(Method.DELETE, url, headers, params, callback);
    }

    /******** download *********/

    public void download(String url, String targetDir) {
        getExecutorInstance().download(url, targetDir, false);
    }

    public void downloadAsync(String url, String targetDir) {
        getExecutorInstance().download(url, targetDir, true);
    }

}
