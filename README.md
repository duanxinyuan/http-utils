# util-http

- Http 协议网络交互 API
- 极简 API
- 支持 GET、POST、PATCH、PUT、DELETE、DOWNLOAD
- 支持配置 请求日志、请求超时时间、失败重试次数、重试间隔毫秒数
- Http工具类：Http

## Maven 依赖
20241010dxy
```xml
<dependency>
    <groupId>com.github.duanxinyuan</groupId>
    <artifactId>util-http</artifactId>
</dependency>
```

## 配置示例

### properties 配置示例

```properties
#是否默认开启请求日志，默认为true
http.defaultRequestLogEnable=true
#默认请求超时时间，单位为秒，默认为60秒
http.defaultTimout=60
#默认请求失败重试次数，默认为0
http.defaultRetries=0
#请求失败重试间隔毫秒数，默认为0
http.retryIntervalMillis=0
#最大并发请求数，默认为64
http.maxRequests=64
#单个域名最大并发请求数，默认为5
http.maxRequestsPerHost=5
```

### yaml 配置示例

```yaml
http:
  #是否默认开启请求日志，默认为true
  defaultRequestLogEnable: true
  #默认请求超时时间，单位为秒，默认为60秒
  defaultTimout: 60
  #默认请求失败重试次数，默认为0
  defaultRetries: 0
  #请求失败重试间隔毫秒数，默认为0
  retryIntervalMillis: 0
  #最大并发请求数，默认为64
  maxRequests: 64
  #单个域名最大并发请求数，默认为5
  maxRequestsPerHost: 5
```

## Http 使用示例

```java

public class HttpTest{

    private String url = "http://test.com/api";
    private Headers headers;
    private Params params;

    @Before
    public void headersAndParams() {
        headers = new Headers("token", "").add("key", "").add("k1", "v1", "k2", "v2", "k3", "v3");
        params = new Params("name", "").add("key", "").add("k1", "v1", "k2", "v2", "k3", "v3");
    }

    /**
     * GET
     */
    public void get() {
        //同步请求
        String s = Http.get(url, headers, params);
        Result result = Http.get(url, headers, params, Result.class);

        //异步请求
        Http.getAsync(url, headers, params, callback);
    }

    /**
     * POST
     */
    public void post() {
        //同步请求：
        String s = Http.post(url, headers, params);
        Result result = Http.post(url, headers, params, Result.class);

        //异步请求：
        Http.postAsync(url, headers, params, callback);

        //POST同步提交JSON：
        String s = Http.postJson(url, headers, params, testBean);
        Result result = Http.postJson(url, testBean, Result.class);

        //POST异步提交JSON：
        Http.postJsonAsync(url, testBean, callback);

        //POST同步上传文件：
        String s = Http.postJson(url, headers, params, testBean);
        Result result = Http.postJson(url, testBean, Result.class);

        //POST同步上传文件：
        Http.postFileAsync(url, testBean, fileparam);

        //POST异步上传文件：
        Http.postFileAsync(url, testBean, fileparam, callback);
    }

    /**
     * PUT
     */
    public void put() {
        //同步请求：
        String s = Http.put(url, headers, params);
        Result result = Http.put(url, headers, params, Result.class);

        //异步请求：
        Http.putAsync(url, headers, params, callback);

        //PUT同步提交JSON：
        String s = Http.putJson(url, headers, params, testBean);
        Result result = Http.putJson(url, testBean, Result.class);

        //PUT异步提交JSON：
        Http.putJsonAsync(url, testBean, callback);
    }

    /**
     * PATCH
     */
    public void patch() {
        //同步请求
        String s = Http.patch(url, headers, params);
        Result result = Http.patch(url, headers, params, Result.class);

        //异步请求
        Http.patchAsync(url, headers, params, callback);

    }

    /**
     * DELETE
     */
    public void delete() {
        //同步请求
        String s = Http.delete(url, headers, params);
        Result result = Http.delete(url, headers, params, Result.class);

        //异步请求
        Http.deleteAsync(url, headers, params, callback);
    }

    /**
     * DOWNLOAD
     */
    public void download() {
        //同步下载，targetPath为本地文件存储地址
        Http.download(url, targetPath);

        //异步下载，targetPath为本地文件存储地址
        Http.downloadAsync(url, targetPath);

        //以byte[]、InputStream或者Reader的形式接收数据
        InputStream inputStream = Http.get(url, InputStream.class);
        Reader reader = Http.get(url, Reader.class);

    }

    /**
     * 请求配置（请求日志开关、请求超时时间、失败重试次数、重试间隔毫秒数）
     */
    public void config() {

        //完整示例
        Http.requestLogEnable(false).timeout(300).retries(2).get(url, headers, params);
        Http.requestLogEnable(false).timeout(300).retries(3, 30000).get(url, headers, params);

        //设置请求日志开关
        Http.requestLogEnable(false).get(url, headers, params);

        //关闭日志请求：
        Http.disableRequestLog().get(url, headers, params);

        //开启日志请求：
        Http.enableRequestLog().get(url, headers, params);

        //设置请求超时时间，单位为秒：
        Http.timeout(100).get(url, headers, params);

        //设置失败重试次数
        Http.retries(100).get(url, headers, params);

        //设置全局请求日志开关：
        Http.setDefaultRequestLogEnable(false);

        //设置全局超时时间，单位为秒：
        Http.setDefaultTimeout(120);

        //设置全局重试次数
        Http.setDefaultRetries(3);
    }

}

```
